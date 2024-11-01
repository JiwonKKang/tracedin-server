package com.univ.tracedin.domain.metric;

import java.io.Serializable;

public record ServiceMetricsCollectedEvent(ServiceMetrics serviceMetrics) implements Serializable {

    public static ServiceMetricsCollectedEvent from(ServiceMetrics metrics) {
        return new ServiceMetricsCollectedEvent(metrics);
    }

    public String getKey() {
        return serviceMetrics.getProjectKey().value() + '-' + serviceMetrics.getServiceName();
    }
}
