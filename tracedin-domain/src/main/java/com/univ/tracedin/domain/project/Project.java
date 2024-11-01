package com.univ.tracedin.domain.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    private ProjectId id;

    private ProjectInfo info;

    private ProjectKey projectKey;

    public static Project create(ProjectInfo projectInfo) {
        return builder().info(projectInfo).projectKey(ProjectKey.create(projectInfo)).build();
    }
}
