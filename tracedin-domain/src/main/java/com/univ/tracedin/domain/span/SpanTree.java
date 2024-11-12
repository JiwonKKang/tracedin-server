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
        return span.getParentId();
    }

    public void addChild(SpanTree child) {
        children.add(child);
    }

    public SpanEvent findExceptionCause() {
        final SpanEvent currentCause = span.getExceptionEvent();

        SpanEvent causeException = null;
        for (SpanTree child : children) {
            final SpanEvent childCause = child.findExceptionCause();
            if (childCause != null) {
                causeException = childCause;
            }
        }

        return causeException != null ? causeException : currentCause;
    }

    public boolean hasAnomalySpan() {
        boolean isAnomaly = false;

        if (span.isAnomaly()) {
            isAnomaly = true;
        } else {
            for (SpanTree child : children) {
                if (child.hasAnomalySpan()) {
                    isAnomaly = true;
                    break;
                }
            }
        }
        return isAnomaly;
    }

    public boolean hasErrorSpan() {
        boolean isError = false;

        if (span.isError()) {
            isError = true;
        } else {
            for (SpanTree child : children) {
                if (child.hasErrorSpan()) {
                    isError = true;
                    break;
                }
            }
        }
        return isError;
    }
}
