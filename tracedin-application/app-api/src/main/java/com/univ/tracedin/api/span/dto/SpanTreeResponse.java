package com.univ.tracedin.api.span.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.univ.tracedin.domain.span.SpanTree;

@JsonInclude(value = NON_EMPTY)
public record SpanTreeResponse(
        SpanResponse span,
        List<SpanTreeResponse> children,
        boolean hasAnomaly,
        boolean hasError,
        SpanExceptionResponse stackTrace) {

    public static SpanTreeResponse from(SpanTree spanTree) {
        return from(spanTree, true); // 최상위 노드 플래그 설정
    }

    private static SpanTreeResponse from(SpanTree spanTree, boolean isRoot) {
        return new SpanTreeResponse(
                SpanResponse.from(spanTree.getSpan()),
                spanTree.getChildren().stream()
                        .map(child -> from(child, false)) // 하위 노드는 최상위가 아님
                        .toList(),
                spanTree.hasAnomalySpan(),
                spanTree.hasErrorSpan(),
                isRoot && spanTree.hasErrorSpan() // 최상위 노드에서만 stackTrace 설정
                        ? SpanExceptionResponse.from(spanTree.findExceptionCause())
                        : null);
    }
}
