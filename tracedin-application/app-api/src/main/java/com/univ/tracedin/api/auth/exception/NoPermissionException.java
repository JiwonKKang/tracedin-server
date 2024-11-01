package com.univ.tracedin.api.auth.exception;

import com.univ.tracedin.common.exception.WebException;
import com.univ.tracedin.domain.auth.exception.AuthErrorCode;

public final class NoPermissionException extends WebException {

    public static final NoPermissionException EXCEPTION = new NoPermissionException();

    private NoPermissionException() {
        super(AuthErrorCode.PERMISSION_DENIED);
    }
}
