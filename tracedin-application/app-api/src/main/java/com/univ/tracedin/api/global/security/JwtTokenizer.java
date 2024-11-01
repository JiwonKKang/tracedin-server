package com.univ.tracedin.api.global.security;

import static com.univ.tracedin.api.global.security.SecurityProperties.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.univ.tracedin.api.auth.exception.ExpiredTokenException;
import com.univ.tracedin.api.auth.exception.InvalidTokenException;
import com.univ.tracedin.domain.auth.TokenGenerator;
import com.univ.tracedin.domain.auth.Tokens;
import com.univ.tracedin.domain.auth.UserPrincipal;
import com.univ.tracedin.domain.user.UserId;
import com.univ.tracedin.domain.user.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenizer implements TokenGenerator {

    public static String generateAccessToken(UserPrincipal principal) {
        final Key key = getKeyFromSecretKey(secretKey);

        return Jwts.builder()
                .setSubject(String.valueOf(principal.userId()))
                .claim("role", principal.role().name())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(getTokenExpiration())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(UserPrincipal principal) {
        final Key key = getKeyFromSecretKey(secretKey);

        return Jwts.builder()
                .setSubject(String.valueOf(principal.userId()))
                .setIssuedAt(Calendar.getInstance().getTime())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static void setInHeader(
            HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader(accessTokenHeader, accessToken);
        response.setHeader(refreshTokenHeader, refreshToken);
    }

    public static Key getKeyFromSecretKey(String secretKey) {
        final byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static Date getTokenExpiration() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, accessTokenExpiration);
        return calendar.getTime();
    }

    public static Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessTokenHeader))
                .filter(accessToken -> accessToken.startsWith(bearer))
                .map(accessToken -> accessToken.replace(bearer, ""));
    }

    public static Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshTokenHeader))
                .filter(refreshToken -> refreshToken.startsWith(bearer))
                .map(refreshToken -> refreshToken.replace(bearer, ""));
    }

    public static UserPrincipal extractPrincipal(String token) {
        try {
            // JWT 파싱 및 유효성 검사
            final Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(getKeyFromSecretKey(secretKey))
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

            final long userId = Long.parseLong(claims.getSubject());
            final UserRole role = UserRole.valueOf(claims.get("role", String.class));
            return UserPrincipal.of(UserId.from(userId), role);

        } catch (ExpiredJwtException e) {
            throw ExpiredTokenException.EXCEPTION;
        } catch (Exception e) {
            throw InvalidTokenException.EXCEPTION;
        }
    }

    @Override
    public Tokens generate(UserPrincipal principal) {
        return Tokens.of(generateAccessToken(principal), generateRefreshToken(principal));
    }
}
