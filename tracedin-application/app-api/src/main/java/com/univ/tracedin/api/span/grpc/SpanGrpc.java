package com.univ.tracedin.api.span.grpc;

import static com.univ.tracedin.api.global.util.GrpcMappingUtils.convertValue;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.api.span.grpc.SpanGrpcAppenderGrpc.SpanGrpcAppenderImplBase;
import com.univ.tracedin.api.span.grpc.SpanProto.AppendSpanResponse;
import com.univ.tracedin.api.span.grpc.SpanProto.AppendSpansRequest;
import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.domain.span.SpanAttributes;
import com.univ.tracedin.domain.span.SpanEvent;
import com.univ.tracedin.domain.span.SpanId;
import com.univ.tracedin.domain.span.SpanKind;
import com.univ.tracedin.domain.span.SpanService;
import com.univ.tracedin.domain.span.SpanStatus;
import com.univ.tracedin.domain.span.SpanTiming;
import com.univ.tracedin.domain.span.SpanType;
import com.univ.tracedin.domain.span.TraceId;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class SpanGrpc extends SpanGrpcAppenderImplBase {

    private final SpanService spanService;

    @Override
    public void appendSpans(
            AppendSpansRequest request, StreamObserver<AppendSpanResponse> responseObserver) {
        try {
            log.info("appendSpans request: {}", request.toString());
            spanService.appendSpans(request.getSpansList().stream().map(this::toSpan).toList());
            responseObserver.onNext(AppendSpanResponse.newBuilder().setStatusCode(200).build());
        } catch (Exception e) {
            log.error("Failed to append spans", e);
            responseObserver.onNext(AppendSpanResponse.newBuilder().setStatusCode(500).build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    private Span toSpan(SpanProto.Span span) {
        return Span.builder()
                .id(SpanId.from(span.getSpanId()))
                .traceId(TraceId.from(span.getTraceId()))
                .parentId(SpanId.from(span.getParentSpanId()))
                .name(span.getName())
                .serviceName(span.getServiceName())
                .projectKey(span.getProjectKey())
                .spanType(SpanType.fromValue(span.getSpanType()))
                .kind(SpanKind.fromValue(span.getKind()))
                .timing(
                        SpanTiming.builder()
                                .startEpochMillis(nanosToMillis(span.getStartEpochNanos()))
                                .endEpochMillis(nanosToMillis(span.getEndEpochNanos()))
                                .build())
                .status(SpanStatus.fromValue(span.getSpanStatus()))
                .attributes(
                        SpanAttributes.builder()
                                .data(
                                        span.getAttributes().getDataMap().entrySet().stream()
                                                .collect(
                                                        Collectors.toMap(
                                                                Map.Entry::getKey,
                                                                entry ->
                                                                        convertValue(
                                                                                entry.getValue()))))
                                .capacity(span.getAttributes().getCapacity())
                                .totalAddedValues(span.getAttributes().getTotalAddedValues())
                                .build())
                .events(
                        span.getEventsList().stream()
                                .map(
                                        event ->
                                                new SpanEvent(
                                                        event.getName(),
                                                        event.getAttributesMap().entrySet().stream()
                                                                .collect(
                                                                        Collectors.toMap(
                                                                                Map.Entry::getKey,
                                                                                Map.Entry
                                                                                        ::getValue)),
                                                        event.getEpochNanos()))
                                .toList())
                .build();
    }

    private long nanosToMillis(long nanos) {
        return nanos / 1_000_000;
    }
}
