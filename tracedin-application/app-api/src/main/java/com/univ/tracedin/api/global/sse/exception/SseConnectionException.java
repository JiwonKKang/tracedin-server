package com.univ.tracedin.api.global.sse.exception;

import com.univ.tracedin.common.exception.WebException;

public final class SseConnectionException extends WebException {

    public static final WebException EXCEPTION = new SseConnectionException();

    private SseConnectionException() {
        super(SseErrorCode.SSE_CONNECTION_FAILED);
    }
}
