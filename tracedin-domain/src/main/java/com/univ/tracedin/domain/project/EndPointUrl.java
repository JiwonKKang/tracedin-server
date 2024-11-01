package com.univ.tracedin.domain.project;

import java.util.regex.Pattern;

public record EndPointUrl(String value) {

    private static final Pattern ENDPOINT_PATTERN =
            Pattern.compile("^(https?://)([a-zA-Z0-9.-]+)(:[0-9]+)?(/[a-zA-Z0-9._-]+)*$");

    public static EndPointUrl from(String url) {
        if (url == null || !ENDPOINT_PATTERN.matcher(url).matches()) {
            throw new IllegalArgumentException("Invalid endpoint URL format: " + url);
        }
        return new EndPointUrl(url);
    }
}
