package com.univ.tracedin.api.global.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityProperties {

    public static String secretKey;
    public static int accessTokenExpiration;
    public static String accessTokenHeader = AUTHORIZATION;
    public static String refreshTokenHeader;
    public static String bearer = "Bearer ";

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey) {
        SecurityProperties.secretKey = secretKey;
    }

    @Value("${jwt.access-token.expiration}")
    public void setAccessTokenExpiration(int accessTokenExpiration) {
        SecurityProperties.accessTokenExpiration = accessTokenExpiration;
    }

    @Value("${jwt.refresh-token.header}")
    public void setRefreshToken(String refreshToken) {
        SecurityProperties.refreshTokenHeader = refreshToken;
    }
}
