package com.nick.gateway.filter;

import io.github.resilience4j.bulkhead.Bulkhead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class BulkheadGatewayFilterFactory extends AbstractGatewayFilterFactory<BulkheadGatewayFilterFactory.Config> {
    @Autowired
    private Bulkhead queryServerBulkhead;

    private final ConcurrentLinkedQueue<QueuedRequest> queue = new ConcurrentLinkedQueue<>();
    private ScheduledExecutorService scheduler;

    public BulkheadGatewayFilterFactory() {
        super(Config.class);
    }

    @PostConstruct
    public void init() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::processQueuedRequests, 0, 10, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return Mono.create(sink -> {
               queue.add(new QueuedRequest(exchange, chain, sink));
               processQueuedRequests();
            });
        };
    }

    private void processQueuedRequests() {
        while (!queue.isEmpty() && queryServerBulkhead.tryAcquirePermission()) {
            QueuedRequest queuedRequest = queue.poll();
            if (queuedRequest != null) {
                queuedRequest.getChain().filter(queuedRequest.getExchange())
                        .doOnSuccess(aVoid -> queuedRequest.getSink().success())
                        .doOnError(queuedRequest.getSink()::error)
                        .doFinally(signalType -> {
                            queryServerBulkhead.onComplete();
                            processQueuedRequests();
                        })
                        .subscribe();
            } else {
                break;
            }
        }
    }

    public static class Config {
    }

    private static class QueuedRequest {
        private final org.springframework.web.server.ServerWebExchange exchange;
        private final org.springframework.cloud.gateway.filter.GatewayFilterChain chain;
        private final MonoSink<Void> sink;

        public QueuedRequest(
                org.springframework.web.server.ServerWebExchange exchange,
                org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
                MonoSink<Void> sink
        ) {
            this.exchange = exchange;
            this.chain = chain;
            this.sink = sink;
        }

        public org.springframework.web.server.ServerWebExchange getExchange() {
            return exchange;
        }

        public org.springframework.cloud.gateway.filter.GatewayFilterChain getChain() {
            return chain;
        }

        public MonoSink<Void> getSink() {
            return sink;
        }
    }
}
