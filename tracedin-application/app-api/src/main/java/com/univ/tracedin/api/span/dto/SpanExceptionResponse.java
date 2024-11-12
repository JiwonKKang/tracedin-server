package com.univ.tracedin.api.span.dto;

import com.univ.tracedin.domain.span.SpanEvent;

public record SpanExceptionResponse(String exceptionType, String message, String stackTrace) {

    public static SpanExceptionResponse from(SpanEvent event) {
        return new SpanExceptionResponse(
                event.attributes().get("exception.type").toString(),
                event.attributes().get("exception.message").toString(),
                event.attributes().get("exception.stacktrace").toString());
    }
}
