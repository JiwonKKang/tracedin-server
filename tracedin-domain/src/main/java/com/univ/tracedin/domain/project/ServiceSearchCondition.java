package com.univ.tracedin.domain.project;

import lombok.Getter;

import io.micrometer.common.util.StringUtils;

@Getter
public class ServiceSearchCondition {
    protected ProjectKey projectKey;
    protected String serviceName;

    public ServiceSearchCondition(ProjectKey projectKey, String serviceName) {
        this.projectKey = projectKey;
        this.serviceName = serviceName;
    }

    protected ServiceSearchCondition() {}

    public boolean hasServiceName() {
        return StringUtils.isNotBlank(serviceName);
    }
}
