package com.univ.tracedin.domain.span;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SpanTreeBuilder {

    public static SpanTree build(List<Span> spans) {
        final Map<SpanId, SpanTree> spanMap =
                spans.stream().collect(Collectors.toMap(Span::getId, SpanTree::init));

        spanMap.values().stream()
                .sorted(Comparator.comparing(span -> span.getSpan().getTiming().startEpochMillis()))
                .forEach(
                        current -> {
                            if (current.getSpan().hasParent()) {
                                final SpanTree parent = spanMap.get(current.getParentId());
                                parent.addChild(current);
                            }
                        });

        return spanMap.values().stream()
                .filter(spanTree -> !spanTree.getSpan().hasParent())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No root span found"));
    }

    private SpanTreeBuilder() {}
}
