package com.univ.tracedin.infra.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.univ.tracedin.domain.project.ProjectId;
import com.univ.tracedin.domain.project.ProjectMember;
import com.univ.tracedin.domain.project.ProjectMember.MemberRole;
import com.univ.tracedin.domain.project.ProjectMemberId;
import com.univ.tracedin.domain.user.UserId;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "project_members")
public class ProjectMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    public static ProjectMemberEntity from(ProjectMember projectMember) {
        return builder()
                .projectId(projectMember.getProjectId().getValue())
                .memberId(projectMember.getMemberId().getValue())
                .role(projectMember.getRole())
                .build();
    }

    public ProjectMember toDomain() {
        return ProjectMember.builder()
                .id(ProjectMemberId.from(id))
                .projectId(ProjectId.from(projectId))
                .memberId(UserId.from(memberId))
                .role(role)
                .build();
    }
}
