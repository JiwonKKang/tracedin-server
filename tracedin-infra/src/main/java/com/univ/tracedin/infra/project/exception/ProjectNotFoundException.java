package com.univ.tracedin.infra.project.exception;

import com.univ.tracedin.common.exception.InfraException;
import com.univ.tracedin.domain.project.exception.ProjectErrorCode;

public final class ProjectNotFoundException extends InfraException {

    public static final ProjectNotFoundException EXCEPTION = new ProjectNotFoundException();

    private ProjectNotFoundException() {
        super(ProjectErrorCode.PROJECT_NOT_FOUND);
    }
}
