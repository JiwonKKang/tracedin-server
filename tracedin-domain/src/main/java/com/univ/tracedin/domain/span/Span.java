package com.univ.tracedin.domain.span;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Span {

    private SpanId id;
    private TraceId traceId;
    private SpanId parentId;
    private String name;
    private String serviceName;
    private String projectKey;
    private SpanKind kind;
    private SpanType spanType;
    private SpanTiming timing;
    private SpanStatus status;
    private SpanAttributes attributes;
    private List<SpanEvent> events;

    public boolean hasParent() {
        return !Objects.equals(parentId.getValue(), "0000000000000000");
    }

    public long getDuration() {
        return timing.duration();
    }

    public Span setAnomaly() {
        attributes.data().put("anomaly", true);
        return this;
    }

    public void updateSpanType(SpanType spanType) {
        this.spanType = spanType;
    }

    public LocalDateTime getStartDateTime() {
        return timing.startDateTime();
    }
}
