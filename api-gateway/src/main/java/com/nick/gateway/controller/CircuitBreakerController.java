package com.nick.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitBreakerController {
    @RequestMapping("/query/dataset/fallback")
    public String queryDataSet() {
        return "HystrixController Return Response: QS not available";
    }
}
