package com.univ.tracedin.infra.elasticsearch;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.infra.span.exception.ElasticSearchException;

@Slf4j
public final class ESUtils {

    public static <T> T executeESQuery(ESSupplier<T> request) {
        try {
            return request.get();
        } catch (IOException e) {
            log.error("Failed to search spans", e);
            throw ElasticSearchException.EXCEPTION;
        }
    }

    private ESUtils() {}
}
