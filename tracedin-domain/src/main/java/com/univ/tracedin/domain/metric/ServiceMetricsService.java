package com.univ.tracedin.domain.metric;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceMetricsService {

    private final ServiceMetricsMessagePublisher serviceMetricsMessagePublisher;

    public void appendMetrics(ServiceMetrics metrics) {
        serviceMetricsMessagePublisher.publish(ServiceMetricsCollectedEvent.from(metrics));
    }
}
