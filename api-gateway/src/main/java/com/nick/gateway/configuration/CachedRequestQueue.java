package com.nick.gateway.configuration;

import com.nick.gateway.dto.QueuedRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class CachedRequestQueue {
    private final ConcurrentLinkedQueue<QueuedRequest> queue = new ConcurrentLinkedQueue<>();

    public void add(QueuedRequest request) {
        queue.offer(request);
    }

    public QueuedRequest poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
