package com.univ.tracedin.domain.span;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpanTree {

    private Span span;
    private List<SpanTree> children;

    public static SpanTree init(Span span) {

        return builder().span(span).children(new ArrayList<>()).build();
    }

    public SpanId getParentId() {
        SpanId spanId = new SpanId();
        return span.getParentId();
    }

    public void addChild(SpanTree child) {
        children.add(child);
    }
}
