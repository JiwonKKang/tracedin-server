package com.univ.tracedin.infra.span.exception;

import com.univ.tracedin.common.exception.InfraException;
import com.univ.tracedin.domain.span.exception.SpanErrorCode;

public final class ElasticSearchException extends InfraException {

    public static final ElasticSearchException EXCEPTION = new ElasticSearchException();

    private ElasticSearchException() {
        super(SpanErrorCode.ELASTIC_SEARCH_ERROR);
    }
}
