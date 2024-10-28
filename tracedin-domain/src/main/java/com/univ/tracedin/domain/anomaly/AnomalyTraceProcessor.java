package com.univ.tracedin.domain.anomaly;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.alert.Alert;
import com.univ.tracedin.domain.alert.AlertSender;
import com.univ.tracedin.domain.project.Project;
import com.univ.tracedin.domain.project.ProjectReader;
import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.domain.span.SpanId;
import com.univ.tracedin.domain.span.SpanReader;
import com.univ.tracedin.domain.span.SpanUpdater;

@Component
@RequiredArgsConstructor
public class AnomalyTraceProcessor {

    private final AlertSender alertSender;
    private final ProjectReader projectReader;
    private final SpanUpdater spanUpdater;
    private final SpanReader spanReader;

    public void process(List<AnomalyTrace> anomalyTraces) {
        updateAnomalySpans(anomalyTraces);
        sendAlerts(anomalyTraces);
    }

    private void sendAlerts(List<AnomalyTrace> anomalyTraces) {
        for (AnomalyTrace anomalyTrace : anomalyTraces) {
            Alert anomalyAlert = createAnomalyAlert(anomalyTrace);
            alertSender.send(anomalyAlert);
        }
    }

    private Alert createAnomalyAlert(AnomalyTrace anomalyTrace) {
        Project project = projectReader.readByKey(anomalyTrace.projectKey());
        HashMap<String, String> details =
                new HashMap<>() {
                    {
                        put("traceId", anomalyTrace.traceId().getValue());
                        put("anomalySpanIds", getSpanIdToString(anomalyTrace));
                    }
                };
        return Alert.create("트랜잭션에 이상치가 탐지되었습니다!", project.getId(), details);
    }

    private void updateAnomalySpans(List<AnomalyTrace> anomalyTraces) {
        for (AnomalyTrace anomalyTrace : anomalyTraces) {
            anomalyTrace.anomalySpanIds().stream()
                    .map(spanReader::read)
                    .map(Span::setAnomaly)
                    .forEach(spanUpdater::update);
        }
    }

    private String getSpanIdToString(AnomalyTrace anomalyTrace) {
        return anomalyTrace.anomalySpanIds().stream().map(SpanId::getValue).toList().toString();
    }
}
