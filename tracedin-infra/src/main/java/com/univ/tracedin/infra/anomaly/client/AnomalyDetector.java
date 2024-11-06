package com.univ.tracedin.infra.anomaly.client;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.infra.anomaly.client.dto.DetectRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnomalyDetector implements AnomalyDetectionClient {

    @Value("${anomaly-detection.url}")
    private String anomalyDetectionUrl;

    @Nullable
    @Override
    public AnomalyTraceResult detect(List<Span> traceSpans) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final List<DetectRequest> detectRequests =
                traceSpans.stream().map(DetectRequest::from).toList();
        final HttpEntity<List<DetectRequest>> httpEntity =
                new HttpEntity<>(detectRequests, headers);
        AnomalyTraceResult body =
                restTemplate
                        .exchange(
                                anomalyDetectionUrl,
                                HttpMethod.POST,
                                httpEntity,
                                AnomalyTraceResult.class)
                        .getBody();
        return body;
    }
}
