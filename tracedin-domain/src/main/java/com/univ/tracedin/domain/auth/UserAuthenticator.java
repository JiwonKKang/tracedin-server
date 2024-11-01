package com.univ.tracedin.domain.auth;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.auth.exception.PasswordValidationException;
import com.univ.tracedin.domain.user.User;
import com.univ.tracedin.domain.user.UserReader;

@Component
@RequiredArgsConstructor
public class UserAuthenticator {

    private final UserReader userReader;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final RefreshTokenCache refreshTokenCache;

    public Tokens authenticate(LoginInfo login) {
        final User user = userReader.read(login.email());
        validatePassword(login, user);
        final Tokens tokens = tokenGenerator.generate(user.getPrincipal());
        refreshTokenCache.cache(tokens.getRefreshToken(user));
        return tokens;
    }

    private void validatePassword(LoginInfo login, User user) {
        if (passwordEncoder.matches(login.password(), user.getPassword())) {
            throw PasswordValidationException.EXCEPTION;
        }
    }
}
