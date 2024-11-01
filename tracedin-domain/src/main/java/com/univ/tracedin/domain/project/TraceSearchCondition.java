package com.univ.tracedin.domain.project;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.micrometer.common.util.StringUtils;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TraceSearchCondition extends ServiceSearchCondition {
    private EndPointUrl endPointUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder
    public TraceSearchCondition(
            ProjectKey projectKey,
            String serviceName,
            EndPointUrl endPointUrl,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        super(projectKey, serviceName);
        this.endPointUrl = endPointUrl;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getEpochMillisStartTime() {
        return startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public long getEpochMillisEndTime() {
        return endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public boolean hasTimeRange() {
        return startTime != null && endTime != null;
    }

    public boolean hasEndPointUrl() {
        if (endPointUrl == null) {
            return false;
        }
        return StringUtils.isNotBlank(endPointUrl.value());
    }
}
