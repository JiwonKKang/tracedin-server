package com.univ.tracedin.api.span.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.univ.tracedin.domain.span.SpanTree;

@JsonInclude(value = NON_EMPTY)
public record SpanTreeResponse(
        SpanResponse span,
        Boolean hasAnomaly,
        Boolean hasError,
        SpanExceptionResponse stackTrace,
        List<SpanTreeResponse> children) {

    public static SpanTreeResponse from(SpanTree spanTree) {
        return from(spanTree, true); // 최상위 노드 플래그 설정
    }

    private static SpanTreeResponse from(SpanTree spanTree, boolean isRoot) {
        return new SpanTreeResponse(
                SpanResponse.from(spanTree.getSpan()),
                isRoot ? spanTree.hasAnomalySpan() : null,
                isRoot ? spanTree.hasErrorSpan() : null,
                isRoot && spanTree.hasErrorSpan() // 최상위 노드에서만 stackTrace 설정
                        ? SpanExceptionResponse.from(spanTree.findExceptionCause())
                        : null,
                spanTree.getChildren().stream()
                        .map(child -> from(child, false)) // 하위 노드는 최상위가 아님
                        .toList());
    }
}
