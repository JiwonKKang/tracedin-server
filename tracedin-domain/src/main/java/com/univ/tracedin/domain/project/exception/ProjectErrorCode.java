package com.univ.tracedin.domain.project.exception;

import static com.univ.tracedin.common.constants.TracedInConstants.NOT_FOUND;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.common.exception.BaseErrorCode;
import com.univ.tracedin.common.exception.ErrorReason;

@RequiredArgsConstructor
public enum ProjectErrorCode implements BaseErrorCode {
    PROJECT_NOT_FOUND(NOT_FOUND, "PROJECT_404_1", "해당 ID의 프로젝트를 찾지 못했습니다."),
    PROJECT_MEMBER_NOT_FOUND(NOT_FOUND, "PROJECT_MEMBER_404_1", "해당 ID의 프로젝트 멤버를 찾지 못했습니다."),
    PROJECT_KEY_NOT_FOUND(NOT_FOUND, "PROJECT_KEY_404_1", "해당 키의 프로젝트를 찾지 못했습니다."),
    NETWORK_TOPOLOGY_BUILD_FAILED(500, "NETWORK_TOPOLOGY_500_1", "네트워크 토폴로지를 생성하는데 실패했습니다."),
    ;
    private final Integer status;
    private final String errorCode;
    private final String message;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.of(status, errorCode, message);
    }
}
