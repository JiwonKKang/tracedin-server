package com.univ.tracedin.infra.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.univ.tracedin.domain.project.Project;
import com.univ.tracedin.domain.project.ProjectId;
import com.univ.tracedin.domain.project.ProjectInfo;
import com.univ.tracedin.domain.project.ProjectKey;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "projects")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String projectKey;

    public static ProjectEntity from(Project project) {
        final Long id = (project.getId() == null) ? null : project.getId().getValue();
        return builder()
                .id(id)
                .name(project.getInfo().projectName())
                .description(project.getInfo().description())
                .projectKey(project.getProjectKey().value())
                .build();
    }

    public Project toDomain() {
        return Project.builder()
                .id(ProjectId.from(id))
                .info(ProjectInfo.of(name, description))
                .projectKey(ProjectKey.from(projectKey))
                .build();
    }
}
