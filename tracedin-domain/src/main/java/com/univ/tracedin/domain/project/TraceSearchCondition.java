package com.univ.tracedin.domain.project;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import io.micrometer.common.util.StringUtils;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TraceSearchCondition extends ServiceSearchCondition {
    private EndPointUrl endPointUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

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
        return StringUtils.isNotBlank(endPointUrl.value());
    }
}
