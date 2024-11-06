package com.univ.tracedin.infra.anomaly.client.dto;

import com.univ.tracedin.domain.span.Span;

public record DetectRequest(String id, String traceId, String projectKey, long duration) {

    public static DetectRequest from(Span span) {
        return new DetectRequest(
                span.getId().getValue(),
                span.getTraceId().getValue(),
                span.getProjectKey(),
                span.getDuration());
    }
}
