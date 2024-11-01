package com.univ.tracedin.infra.project.exception;

import com.univ.tracedin.common.exception.InfraException;
import com.univ.tracedin.domain.project.exception.ProjectErrorCode;

public final class ProjectMemberNotFoundException extends InfraException {

    public static final ProjectMemberNotFoundException EXCEPTION =
            new ProjectMemberNotFoundException();

    private ProjectMemberNotFoundException() {
        super(ProjectErrorCode.PROJECT_MEMBER_NOT_FOUND);
    }
}
