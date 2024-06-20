package com.nick.gateway.controller;

import io.github.resilience4j.bulkhead.Bulkhead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitBreakerController {
    @Autowired
    private Bulkhead queryServerBulkhead;

    @RequestMapping("/query/dataset/fallback")
    public String queryDataSet() {
        return "HystrixController Return Response: QS not available";
    }

    @GetMapping("/bulkhead-available-calls")
    public int getAvailableConcurrentCalls() {
        return queryServerBulkhead.getMetrics().getAvailableConcurrentCalls();
    }
}
