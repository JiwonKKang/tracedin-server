package com.univ.tracedin.common.dto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SearchResult<T>(List<T> results, Map<String, Object> afterKey, long totalCount) {

    public static <T> SearchResult<T> success(
            List<T> results, Map<String, Object> nextSearchAfter, long totalCount) {
        return new SearchResult<>(results, nextSearchAfter, totalCount);
    }

    public <R> SearchResult<R> map(Function<T, R> mapper) {
        final List<R> mappedContent = results.stream().map(mapper).collect(Collectors.toList());
        return new SearchResult<>(mappedContent, afterKey, totalCount);
    }
}
