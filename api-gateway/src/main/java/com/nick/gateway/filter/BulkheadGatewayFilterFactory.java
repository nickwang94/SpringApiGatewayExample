package com.nick.gateway.filter;

import com.nick.gateway.configuration.CachedRequestQueue;
import com.nick.gateway.configuration.ThreadPoolManager;
import com.nick.gateway.dto.QueuedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BulkheadGatewayFilterFactory extends AbstractGatewayFilterFactory<BulkheadGatewayFilterFactory.Config> {
    private CachedRequestQueue cachedRequestQueue;
    private ThreadPoolManager threadPoolManager;

    @Autowired
    public BulkheadGatewayFilterFactory(CachedRequestQueue cachedRequestQueue, ThreadPoolManager threadPoolManager) {
        super(Config.class);
        this.cachedRequestQueue = cachedRequestQueue;
        this.threadPoolManager = threadPoolManager;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return Mono.create(sink -> {
                cachedRequestQueue.add(new QueuedRequest(exchange, chain, sink));
            });
        };
    }

    public static class Config {
    }
}
