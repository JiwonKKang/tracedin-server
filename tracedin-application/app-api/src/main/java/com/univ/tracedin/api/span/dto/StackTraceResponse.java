package com.univ.tracedin.api.span.dto;

import com.univ.tracedin.domain.span.SpanEvent;

public record StackTraceResponse(String exceptionType, String message, String stackTrace) {

    public static StackTraceResponse from(SpanEvent event) {
        return new StackTraceResponse(
                event.attributes().get("exception.type").toString(),
                event.attributes().get("exception.message").toString(),
                event.attributes().get("exception.stacktrace").toString());
    }
}
