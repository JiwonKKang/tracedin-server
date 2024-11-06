package com.univ.tracedin.domain.project;

public record EndPointUrl(String value) {

    public static EndPointUrl from(String url) {
        return new EndPointUrl(url);
    }
}
