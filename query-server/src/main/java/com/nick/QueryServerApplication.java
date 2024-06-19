package com.nick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class QueryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryServerApplication.class, args);
    }

}
