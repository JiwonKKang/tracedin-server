package com.univ.tracedin.domain.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.univ.tracedin.domain.user.UserId;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember {

    private ProjectMemberId id;
    private ProjectId projectId;
    private UserId memberId;
    private MemberRole role;

    public static ProjectMember create(ProjectId projectId, UserId memberId, MemberRole role) {
        return builder().projectId(projectId).memberId(memberId).role(role).build();
    }

    public void changeRole(MemberRole role) {
        this.role = role;
    }

    public enum MemberRole {
        ADMIN,
        MEMBER
    }
}
