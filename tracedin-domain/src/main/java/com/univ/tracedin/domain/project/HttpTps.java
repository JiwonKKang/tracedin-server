package com.univ.tracedin.domain.project;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record HttpTps(LocalDateTime timestamp, int httpRequestCount) {

    public static HttpTps of(long endEpochMillis, double httpRequestCount) {
        final LocalDateTime timestamp =
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(endEpochMillis), ZoneId.systemDefault());
        return new HttpTps(timestamp, (int) httpRequestCount);
    }
}
