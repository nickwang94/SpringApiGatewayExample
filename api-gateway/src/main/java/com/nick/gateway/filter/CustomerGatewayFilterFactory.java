package com.nick.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomerGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomerGatewayFilterFactory.Config> {
    private static final Logger logger = LoggerFactory.getLogger(CustomerGatewayFilterFactory.class);

    public CustomerGatewayFilterFactory() {
        super(CustomerGatewayFilterFactory.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                logger.info("GatewayFilter: handle request, {}", exchange.getRequest().getPath());
                logger.info("Config info: {}:{}", config.getName(), config.getAge());
                Mono<Void> filter = chain.filter(exchange);
                logger.info("GatewayFilter: handle response, {}", exchange.getResponse().getStatusCode());
                return filter;
            }
        };
    }

    public static class Config {
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
