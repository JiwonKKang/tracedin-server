package com.univ.tracedin.domain.anomaly;

import java.util.List;

import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.span.SpanId;
import com.univ.tracedin.domain.span.TraceId;

public record AnomalyTrace(TraceId traceId, ProjectKey projectKey, List<SpanId> anomalySpanIds) {

    public static AnomalyTrace from(
            TraceId traceId, String projectKey, List<String> anomalySpanIds) {
        final List<SpanId> spanIds = anomalySpanIds.stream().map(SpanId::from).toList();
        return new AnomalyTrace(traceId, ProjectKey.from(projectKey), spanIds);
    }
}
