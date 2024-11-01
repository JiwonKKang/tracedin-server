package com.univ.tracedin.infra.alert.exception;

import com.univ.tracedin.common.exception.InfraException;
import com.univ.tracedin.domain.alert.exception.AlertErrorCode;

public final class AlertMethodNotFoundException extends InfraException {

    public static final AlertMethodNotFoundException EXCEPTION = new AlertMethodNotFoundException();

    private AlertMethodNotFoundException() {
        super(AlertErrorCode.ALERT_METHOD_NOT_FOUND);
    }
}
