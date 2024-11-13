package com.univ.tracedin.domain.project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record StatusCodeDistribution(List<StatusCodeBucket> statusCodeBuckets) {

    public record StatusCodeBucket(String statusCode, long count) {

        public static StatusCodeBucket of(String statusCode, long count) {
            return new StatusCodeBucket(statusCode, count);
        }
    }

    public static StatusCodeDistribution from(List<StatusCodeBucket> statusCodeBuckets) {
        final Map<String, Long> counts =
                new HashMap<>() {
                    {
                        put("2xx", 0L);
                        put("4xx", 0L);
                        put("5xx", 0L);
                    }
                };

        for (StatusCodeBucket bucket : statusCodeBuckets) {
            counts.put(bucket.statusCode(), bucket.count());
        }

        final List<StatusCodeBucket> completeStatusCodeBuckets =
                counts.entrySet().stream()
                        .map(entry -> new StatusCodeBucket(entry.getKey(), entry.getValue()))
                        .toList();

        return new StatusCodeDistribution(completeStatusCodeBuckets);
    }
}
