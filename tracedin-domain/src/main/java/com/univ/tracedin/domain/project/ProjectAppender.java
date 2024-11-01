package com.univ.tracedin.domain.project;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.user.User;

@Component
@RequiredArgsConstructor
public class ProjectAppender {

    private final ProjectRepository projectRepository;
    private final ProjectMemberManager projectMemberManager;

    public ProjectKey append(User user, ProjectInfo projectInfo) {
        final Project project = Project.create(projectInfo);
        final Project saved = projectRepository.save(project);
        projectMemberManager.add(saved, user, ProjectMember.MemberRole.ADMIN);
        return project.getProjectKey();
    }
}
