package com.univ.tracedin.api.project.dto;

import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.project.ServiceSearchCondition;

public record ServiceSearchRequest(String projectKey, String serviceName) {
    public ServiceSearchCondition toCondition() {
        return new ServiceSearchCondition(ProjectKey.from(projectKey), serviceName);
    }
}
