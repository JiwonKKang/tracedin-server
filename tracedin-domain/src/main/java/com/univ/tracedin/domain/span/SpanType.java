package com.univ.tracedin.domain.span;

import lombok.Getter;

@Getter
public enum SpanType {
    UNKNOWN("unknown"),
    HTTP("http"),
    METHOD("method"),
    QUERY("query");

    private final String value;

    SpanType(String value) {
        this.value = value;
    }

    public static SpanType fromValue(String value) {
        for (SpanType type : SpanType.values()) {

            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
