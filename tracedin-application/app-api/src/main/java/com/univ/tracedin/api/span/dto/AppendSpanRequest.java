package com.univ.tracedin.api.span.dto;

import java.util.List;
import java.util.Map;

import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.domain.span.SpanAttributes;
import com.univ.tracedin.domain.span.SpanEvent;
import com.univ.tracedin.domain.span.SpanId;
import com.univ.tracedin.domain.span.SpanKind;
import com.univ.tracedin.domain.span.SpanStatus;
import com.univ.tracedin.domain.span.SpanTiming;
import com.univ.tracedin.domain.span.SpanType;
import com.univ.tracedin.domain.span.TraceId;

import io.micrometer.common.util.StringUtils;

public record AppendSpanRequest(
        String serviceName,
        String projectKey,
        String traceId,
        String spanId,
        String parentSpanId,
        String spanType,
        String name,
        String kind,
        long startEpochNanos,
        long endEpochNanos,
        String spanStatus,
        Attributes attributes,
        List<Event> events) {

    public record Attributes(Map<String, Object> data, int capacity, int totalAddedValues) {

        public SpanAttributes toDomain() {
            return SpanAttributes.builder()
                    .data(data)
                    .capacity(capacity)
                    .totalAddedValues(totalAddedValues)
                    .build();
        }
    }

    public record Event(String name, Map<String, Object> attributes, long epochNanos) {

        public SpanEvent toDomain() {
            return new SpanEvent(name, attributes, epochNanos);
        }
    }

    public Span toSpan() {
        return Span.builder()
                .id(SpanId.from(spanId))
                .traceId(TraceId.from(traceId))
                .parentId(SpanId.from(parentSpanId))
                .name(name)
                .serviceName(serviceName)
                .projectKey(projectKey)
                .spanType(getSpanType())
                .kind(SpanKind.fromValue(kind))
                .timing(
                        SpanTiming.builder()
                                .startEpochMillis(nanosToMillis(startEpochNanos))
                                .endEpochMillis(nanosToMillis(endEpochNanos))
                                .build())
                .status(SpanStatus.fromValue(spanStatus))
                .attributes(attributes.toDomain())
                .events(events.stream().map(Event::toDomain).toList())
                .build();
    }

    private SpanType getSpanType() {
        SpanType type =
                StringUtils.isBlank(spanType) ? SpanType.UNKNOWN : SpanType.fromValue(spanType);
        if (attributes.data().containsKey("db.operation")) {
            type = SpanType.QUERY;
        }
        return type;
    }

    private long nanosToMillis(long epochNanos) {
        return epochNanos / 1_000_000L; // 나노초를 밀리초로 변환
    }
}
