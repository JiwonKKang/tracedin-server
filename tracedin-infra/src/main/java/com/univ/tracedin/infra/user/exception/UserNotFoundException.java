package com.univ.tracedin.infra.user.exception;

import com.univ.tracedin.common.exception.InfraException;
import com.univ.tracedin.domain.user.exception.UserErrorCode;

public final class UserNotFoundException extends InfraException {

    public static final UserNotFoundException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
