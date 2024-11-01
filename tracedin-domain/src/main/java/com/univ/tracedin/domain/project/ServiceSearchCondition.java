package com.univ.tracedin.domain.project;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import io.micrometer.common.util.StringUtils;

@Getter
@SuperBuilder
@Builder
public class ServiceSearchCondition {
    protected ProjectKey projectKey;
    protected String serviceName;

    protected ServiceSearchCondition(ProjectKey projectKey, String serviceName) {
        this.projectKey = projectKey;
        this.serviceName = serviceName;
    }

    protected ServiceSearchCondition() {}

    public boolean hasServiceName() {
        return StringUtils.isNotBlank(serviceName);
    }
}
