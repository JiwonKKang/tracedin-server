package com.univ.tracedin.domain.metric;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceMetricsMessageProcessor {

    private final ServiceMetricsAppender serviceMetricsAppender;
    private final ServiceMetricsSender serviceMetricsSender;

    public void process(List<ServiceMetricsCollectedEvent> messages) {
        final List<ServiceMetrics> metrics =
                messages.stream().map(ServiceMetricsCollectedEvent::serviceMetrics).toList();
        metrics.forEach(serviceMetricsSender::send);
        serviceMetricsAppender.appendAll(metrics);
    }
}
