package com.univ.tracedin.domain.span;

import java.util.Map;

import lombok.Builder;

@Builder
public record SpanEvent(String name, Map<String, Object> attributes, long epochNanos) {

    public boolean isExceptionEvent() {
        return "exception".equals(name);
    }
}
