package com.nick.gateway.configuration;

import com.nick.gateway.dto.QueuedRequest;
import io.github.resilience4j.bulkhead.Bulkhead;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class ThreadPoolManager {
    private Logger logger = LogManager.getLogger(ThreadPoolManager.class);
    private CachedRequestQueue cachedRequestQueue;
    private Bulkhead queryServerBulkhead;
    private ThreadPoolTaskExecutor taskExecutor;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        int corePoolSize = 5;
        int maxPoolSize = 50;
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(maxPoolSize * 1000);
        threadPoolTaskExecutor.setThreadPriority(Thread.MAX_PRIORITY);
        threadPoolTaskExecutor.setDaemon(true);
        threadPoolTaskExecutor.setKeepAliveSeconds(300);
        threadPoolTaskExecutor.setThreadNamePrefix("AGW-Handle-QueuedRequest-Thread-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Autowired
    public ThreadPoolManager(
            CachedRequestQueue cachedRequestQueue,
            Bulkhead queryServerBulkhead
    ) {
        this.cachedRequestQueue = cachedRequestQueue;
        this.queryServerBulkhead = queryServerBulkhead;
    }


    @PostConstruct
    public void init() {
        taskExecutor = taskExecutor();
        taskExecutor.execute(() -> {
            while (true) {
                processQueuedRequests();
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        taskExecutor.shutdown();
    }

    public void processQueuedRequests() {
        if (cachedRequestQueue.isEmpty()) {
            return;
        }
        while (queryServerBulkhead.tryAcquirePermission() && !cachedRequestQueue.isEmpty()) {
            QueuedRequest queuedRequest = cachedRequestQueue.poll();
            if (queuedRequest != null) {
                logger.info("Forward request to QS with id: {}", queuedRequest.getExchange().getRequest().getQueryParams().getFirst("dataSet"));
                queuedRequest.getChain().filter(queuedRequest.getExchange())
                        .publishOn(Schedulers.fromExecutor(taskExecutor))
                        .doOnSuccess(aVoid -> queuedRequest.getSink().success())
                        .doOnError(queuedRequest.getSink()::error)
                        .doFinally(signalType -> {
                            queryServerBulkhead.onComplete();
                            logger.info("Finished: {}", queuedRequest.getExchange().getRequest().getQueryParams().getFirst("dataSet"));
                        })
                        .subscribe();
            }
            logger.info("Forwarded: {}", queuedRequest.getExchange().getRequest().getQueryParams().getFirst("dataSet"));
        }
    }
}
