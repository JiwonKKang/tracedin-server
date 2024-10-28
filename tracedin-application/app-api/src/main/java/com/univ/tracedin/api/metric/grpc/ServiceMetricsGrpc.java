package com.univ.tracedin.api.metric.grpc;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.api.metric.grpc.ServiceMetricsGrpcAppenderGrpc.ServiceMetricsGrpcAppenderImplBase;
import com.univ.tracedin.api.metric.grpc.ServiceMetricsProto.AppendServiceMetricsRequest;
import com.univ.tracedin.api.metric.grpc.ServiceMetricsProto.AppendServiceMetricsResponse;
import com.univ.tracedin.api.metric.grpc.ServiceMetricsProto.MetricRequest;
import com.univ.tracedin.domain.metric.Metric;
import com.univ.tracedin.domain.metric.MetricType;
import com.univ.tracedin.domain.metric.ServiceMetrics;
import com.univ.tracedin.domain.metric.ServiceMetricsService;
import com.univ.tracedin.domain.project.ProjectKey;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class ServiceMetricsGrpc extends ServiceMetricsGrpcAppenderImplBase {

    private final ServiceMetricsService serviceMetricsService;

    @Override
    public void appendServiceMetrics(
            AppendServiceMetricsRequest request,
            StreamObserver<AppendServiceMetricsResponse> responseObserver) {
        try {
            log.info("appendServiceMetrics request: {}", request.toString());
            serviceMetricsService.appendMetrics(toServiceMetrics(request));
            responseObserver.onNext(
                    AppendServiceMetricsResponse.newBuilder().setStatusCode(200).build());
        } catch (Exception e) {
            log.error("Failed to append service metrics", e);
            responseObserver.onNext(
                    AppendServiceMetricsResponse.newBuilder().setStatusCode(500).build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    private ServiceMetrics toServiceMetrics(AppendServiceMetricsRequest request) {
        return ServiceMetrics.builder()
                .projectKey(ProjectKey.from(request.getProjectKey()))
                .serviceName(request.getServiceName())
                .metrics(request.getMetricsList().stream().map(this::toMetric).toList())
                .build();
    }

    private Metric toMetric(MetricRequest metricRequest) {
        return Metric.builder()
                .name(metricRequest.getName())
                .description(metricRequest.getDescription())
                .unit(metricRequest.getUnit())
                .type(MetricType.fromValue(metricRequest.getType()))
                .value(metricRequest.getValue())
                .count(metricRequest.getCount())
                .sum(metricRequest.getSum())
                .min(metricRequest.getMin())
                .max(metricRequest.getMax())
                .attributes(
                        metricRequest.getAttributesMap().entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
