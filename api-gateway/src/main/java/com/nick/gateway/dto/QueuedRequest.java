package com.nick.gateway.dto;

import reactor.core.publisher.MonoSink;

public class QueuedRequest {
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
