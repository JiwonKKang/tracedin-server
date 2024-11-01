package com.univ.tracedin.api.project.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import com.univ.tracedin.domain.project.EndPointUrl;
import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.project.TraceSearchCondition;

import io.micrometer.common.util.StringUtils;

public record TraceSearchRequest(
        @NotBlank(message = "프로젝트 키는 필수입니다.") String projectKey,
        String serviceName,
        String endPointUrl,
        LocalDateTime startTime,
        LocalDateTime endTime) {

    public TraceSearchCondition toCondition() {
        return TraceSearchCondition.builder()
                .projectKey(ProjectKey.from(projectKey))
                .serviceName(serviceName)
                .endPointUrl((endPointUrl == null) ? null : EndPointUrl.from(endPointUrl))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    // 종료 시간만 있을 수 없도록 검증
    @AssertTrue(message = "종료 시각만 지정할 수 없습니다.")
    public boolean assertValidTimeRange() {
        return startTime != null || endTime == null;
    }

    // 시작 시간이 종료 시간보다 늦을 수 없도록 검증
    @AssertTrue(message = "시작 시각이 종료 시각보다 늦을 수 없습니다.")
    public boolean assertStartTimeBeforeEndTime() {
        return startTime == null || endTime == null || !startTime.isAfter(endTime);
    }

    // 엔드포인트 URL이 있으면 서비스 이름이 있어야 함
    @AssertTrue(message = "서비스 이름이 없으면 엔드포인트 URL을 지정할 수 없습니다.")
    public boolean assertServiceNameRequiredForEndPointUrl() {
        return endPointUrl == null || StringUtils.isNotBlank(serviceName);
    }
}
