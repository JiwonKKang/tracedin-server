package com.univ.tracedin.api.global.security;

import java.util.stream.Stream;

public class SecurityConstants {

    public static final String[] SWAGGER_URIS = {
        /* swagger v2 */
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",
        /* swagger v3 */
        "/v3/api-docs/**",
        "/swagger-ui/**"
    };

    public static final String[] SYSTEM_URIS = {
        "/error", "/error/**", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**"
    };

    public static String[] getPermittedURIs() {
        return Stream.of(SWAGGER_URIS, SYSTEM_URIS).flatMap(Stream::of).toArray(String[]::new);
    }

    private SecurityConstants() {}
}
