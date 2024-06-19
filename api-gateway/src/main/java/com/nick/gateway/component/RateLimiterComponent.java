package com.nick.gateway.component;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimiterComponent implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String requestPath = exchange.getRequest().getPath().toString();
        return Mono.just(requestPath);
    }
}
