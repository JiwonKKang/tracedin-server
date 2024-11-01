package com.univ.tracedin.domain.project;

import static com.univ.tracedin.domain.project.NetworkTopology.*;
import static com.univ.tracedin.domain.project.ProjectMember.*;
import static com.univ.tracedin.domain.project.ProjectStatistic.*;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.span.Topology;
import com.univ.tracedin.domain.user.User;
import com.univ.tracedin.domain.user.UserId;
import com.univ.tracedin.domain.user.UserReader;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final UserReader userReader;
    private final ProjectReader projectReader;
    private final ProjectAppender projectAppender;
    private final ProjectDeleter projectDeleter;
    private final ProjectValidator projectValidator;
    private final ProjectMemberManager projectMemberManager;
    private final NetworkTopologyAnalyer networkTopologyAnalyer;
    private final ProjectAnalyzer projectAnalyzer;

    public ProjectKey create(UserId creatorId, ProjectInfo projectInfo) {
        final User user = userReader.read(creatorId);
        return projectAppender.append(user, projectInfo);
    }

    public List<Project> getProjectList(UserId userId) {
        final User user = userReader.read(userId);
        return projectReader.readAll(user);
    }

    public List<Node> getServiceNodeList(ProjectKey projectKey) {
        final Project project = projectReader.readByKey(projectKey);
        return projectReader.readServiceNods(project);
    }

    public List<EndPointUrl> getServiceEndpoints(ServiceSearchCondition cond) {
        projectValidator.validate(cond.getProjectKey());
        return projectAnalyzer.getEndpoints(cond);
    }

    public Topology getNetworkTopology(ProjectKey projectKey) {
        final Project project = projectReader.readByKey(projectKey);
        return networkTopologyAnalyer.analyze(project);
    }

    public ProjectStatistic<?> getStatistics(
            TraceSearchCondition cond, StatisticsType statisticsType) {
        projectValidator.validate(cond.getProjectKey());
        return projectAnalyzer.analyze(cond, statisticsType);
    }

    public void addMember(ProjectId projectId, String targetMemberEmail, MemberRole role) {
        final Project project = projectReader.read(projectId);
        final User targetUser = userReader.read(targetMemberEmail);
        projectMemberManager.add(project, targetUser, role);
    }

    public void removeMember(ProjectMemberId projectMemberId) {
        final ProjectMember projectMember = projectMemberManager.read(projectMemberId);
        projectMemberManager.remove(projectMember);
    }

    public void changeRole(ProjectMemberId projectMemberId, MemberRole role) {
        final ProjectMember projectMember = projectMemberManager.read(projectMemberId);
        projectMemberManager.changeRole(projectMember, role);
    }

    public void deleteProject(ProjectId projectId) {
        final Project project = projectReader.read(projectId);
        projectMemberManager.removeAll(project);
        projectDeleter.delete(project);
    }
}
