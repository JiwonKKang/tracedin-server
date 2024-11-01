package com.univ.tracedin.api.auth.exception;

import com.univ.tracedin.common.exception.WebException;
import com.univ.tracedin.domain.auth.exception.AuthErrorCode;

public final class AuthenticationException extends WebException {

    public static final AuthenticationException EXCEPTION = new AuthenticationException();

    private AuthenticationException() {
        super(AuthErrorCode.AUTHENTICATION_FAILED);
    }
}
