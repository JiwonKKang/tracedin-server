package com.univ.tracedin.infra.anomaly.client;

import java.util.List;

import org.springframework.stereotype.Component;

import com.univ.tracedin.domain.span.Span;

@Component
public class DefaultAnomalyDetector implements AnomalyDetectionClient {

    @Override
    public AnomalyTraceResult detect(List<Span> traceSpans) {
        final List<Span> anomalySpans =
                traceSpans.stream().filter(DefaultAnomalyDetector::isAnomaly).toList();
        final boolean isAnomaly = !anomalySpans.isEmpty();
        return new AnomalyTraceResult(
                isAnomaly,
                traceSpans.stream()
                        .map(Span::getProjectKey)
                        .findAny()
                        .orElse("1206887328-a7863a66-528e-4f37-b805-04e1314fb924"),
                anomalySpans.stream().map(span -> span.getId().getValue()).toList());
    }

    private static boolean isAnomaly(Span span) {
        return span.getTiming().duration() > 1000;
    }
}
