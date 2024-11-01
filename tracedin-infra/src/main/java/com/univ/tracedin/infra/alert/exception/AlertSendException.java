package com.univ.tracedin.infra.alert.exception;

import com.univ.tracedin.common.exception.InfraException;
import com.univ.tracedin.domain.alert.exception.AlertErrorCode;

public final class AlertSendException extends InfraException {

    public static final AlertSendException EXCEPTION = new AlertSendException();

    private AlertSendException() {
        super(AlertErrorCode.ALERT_SEND_FAIL);
    }
}
