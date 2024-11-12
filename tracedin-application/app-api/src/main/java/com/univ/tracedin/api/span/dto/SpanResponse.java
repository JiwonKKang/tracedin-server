package com.univ.tracedin.api.span.dto;

import static com.univ.tracedin.domain.span.Span.ANOMALY;

import java.time.LocalDateTime;
import java.util.Map;

import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.domain.span.SpanKind;
import com.univ.tracedin.domain.span.SpanType;

public record SpanResponse(
        String id,
        String traceId,
        String parentSpanId,
        String name,
        String serviceName,
        String projectKey,
        SpanKind kind,
        SpanType spanType,
        long startEpochMillis,
        long endEpochMillis,
        long duration,
        LocalDateTime startDateTime,
        Map<String, Object> data,
        Integer capacity,
        Integer totalAddedValues,
        boolean anomaly,
        boolean error,
        StackTraceResponse stackTrace) {

    public static SpanResponse from(Span span) {
        return new SpanResponse(
                span.getId().getValue(),
                span.getTraceId().getValue(),
                span.getParentId().getValue(),
                span.getName(),
                span.getServiceName(),
                span.getProjectKey(),
                span.getKind(),
                span.getSpanType(),
                span.getTiming().startEpochMillis(),
                span.getTiming().endEpochMillis(),
                span.getDuration(),
                span.getStartDateTime(),
                span.getAttributes().data(),
                span.getAttributes().capacity(),
                span.getAttributes().totalAddedValues(),
                span.getAttributes().data().containsKey(ANOMALY),
                span.isError(),
                span.isError() ? StackTraceResponse.from(span.getExceptionEvent()) : null);
    }
}
