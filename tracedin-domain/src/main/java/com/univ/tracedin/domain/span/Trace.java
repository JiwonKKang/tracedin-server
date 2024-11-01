package com.univ.tracedin.domain.span;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Trace {

    private TraceId id;
    private String endPoint;
    private boolean isAnomaly;
    private String serviceName;
    private int statusCode;
    private long startEpochMillis;
    private long endEpochMillis;

    public long duration() {
        return endEpochMillis - startEpochMillis;
    }

    public LocalDateTime startDateTime() {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(startEpochMillis), ZoneId.systemDefault());
    }
}
