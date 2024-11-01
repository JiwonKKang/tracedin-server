package com.univ.tracedin.api.global.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.api.auth.exception.InvalidTokenException;
import com.univ.tracedin.domain.auth.RefreshToken;
import com.univ.tracedin.domain.auth.RefreshTokenCache;
import com.univ.tracedin.domain.auth.UserPrincipal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RefreshTokenCache tokenCache;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final Optional<String> refreshToken = JwtTokenizer.extractRefreshToken(request);

            if (refreshToken.isPresent()) {
                final UserPrincipal principal = JwtTokenizer.extractPrincipal(refreshToken.get());
                validateRefreshToken(principal, refreshToken.get());
                reIssueToken(principal, response);
                return;
            }

            JwtTokenizer.extractAccessToken(request)
                    .map(JwtTokenizer::extractPrincipal)
                    .ifPresent(JwtAuthenticationFilter::saveAuthentication);

        } catch (Exception e) {
            log.error(e.toString());
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    public void validateRefreshToken(UserPrincipal principal, String givenRefreshToken) {
        final RefreshToken validRefreshToken = tokenCache.get(principal.userId());
        if (validRefreshToken.notEquals(givenRefreshToken)) {
            throw InvalidTokenException.EXCEPTION;
        }
    }

    private void reIssueToken(UserPrincipal principal, HttpServletResponse response) {
        final String reIssuedAccessToken = JwtTokenizer.generateAccessToken(principal);
        final String reIssuedRefreshToken = JwtTokenizer.generateRefreshToken(principal);
        tokenCache.cache(RefreshToken.of(principal.userId(), reIssuedRefreshToken));
        JwtTokenizer.setInHeader(response, reIssuedAccessToken, reIssuedRefreshToken);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    private static void saveAuthentication(UserPrincipal principal) {
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, getAuthorities(principal));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static List<GrantedAuthority> getAuthorities(UserPrincipal principal) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + principal.role().name()));
        return authorities;
    }
}
