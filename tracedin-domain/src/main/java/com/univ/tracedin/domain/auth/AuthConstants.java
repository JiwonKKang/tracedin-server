package com.univ.tracedin.domain.auth;

import java.time.Duration;

public final class AuthConstants {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    public static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    private AuthConstants() {}
}
