package com.univ.tracedin.api.auth.exception;

import com.univ.tracedin.common.exception.WebException;
import com.univ.tracedin.domain.auth.exception.AuthErrorCode;

public final class ExpiredTokenException extends WebException {

    public static final ExpiredTokenException EXCEPTION = new ExpiredTokenException();

    private ExpiredTokenException() {
        super(AuthErrorCode.EXPIRED_TOKEN);
    }
}
