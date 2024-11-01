package com.univ.tracedin.domain.project;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.user.User;

@Component
@RequiredArgsConstructor
public class ProjectMemberManager {

    private final ProjectRepository projectRepository;

    public void add(Project project, User targetMember, ProjectMember.MemberRole role) {
        final ProjectMember projectMember =
                ProjectMember.create(project.getId(), targetMember.getId(), role);
        projectRepository.saveProjectMember(projectMember);
    }

    public ProjectMember read(ProjectMemberId id) {
        return projectRepository.findProjectMemberById(id);
    }

    public void remove(ProjectMember projectMember) {
        projectRepository.deleteProjectMember(projectMember);
    }

    public void changeRole(ProjectMember projectMember, ProjectMember.MemberRole role) {
        projectMember.changeRole(role);
        projectRepository.saveProjectMember(projectMember);
    }

    public List<ProjectMember> readAll(User user) {
        return projectRepository.findProjectMembersByUser(user);
    }

    public List<ProjectMember> readAll(Project project) {
        return projectRepository.findProjectMembersByProject(project);
    }

    public void removeAll(Project project) {
        readAll(project).forEach(this::remove);
    }
}
