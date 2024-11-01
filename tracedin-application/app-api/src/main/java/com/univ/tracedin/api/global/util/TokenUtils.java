package com.univ.tracedin.api.global.util;

import static com.univ.tracedin.domain.auth.AuthConstants.*;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.univ.tracedin.domain.auth.Tokens;

public final class TokenUtils {

    public static HttpHeaders createTokenHeaders(Tokens tokens) {
        final HttpHeaders headers = new HttpHeaders();
        final ResponseCookie cookie =
                createHttpOnlyCookie(
                        REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken(), REFRESH_TOKEN_TTL);
        headers.set(HttpHeaders.AUTHORIZATION, tokens.accessToken());
        headers.set(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    private TokenUtils() {}

    private static ResponseCookie createHttpOnlyCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value).httpOnly(true).path("/").maxAge(maxAge).build();
    }
}
