package com.nick.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component
public class GatewayGlobalFilter implements GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(GatewayGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Request: {}", exchange.getRequest().getPath());
        Mono<Void> filter = chain.filter(exchange);
        logger.info("Response: {}", exchange.getResponse().getStatusCode());
        return filter;
    }
}
