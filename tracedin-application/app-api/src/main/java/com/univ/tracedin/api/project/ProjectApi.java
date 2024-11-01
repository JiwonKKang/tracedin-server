package com.univ.tracedin.api.project;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.api.global.dto.Response;
import com.univ.tracedin.api.project.dto.AddMemberRequest;
import com.univ.tracedin.api.project.dto.CreateProjectRequest;
import com.univ.tracedin.api.project.dto.NodeResponse;
import com.univ.tracedin.api.project.dto.ProjectResponse;
import com.univ.tracedin.api.project.dto.ServiceSearchRequest;
import com.univ.tracedin.api.project.dto.TraceSearchRequest;
import com.univ.tracedin.domain.project.EndPointUrl;
import com.univ.tracedin.domain.project.ProjectId;
import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.project.ProjectMember.MemberRole;
import com.univ.tracedin.domain.project.ProjectMemberId;
import com.univ.tracedin.domain.project.ProjectService;
import com.univ.tracedin.domain.project.ProjectStatistic;
import com.univ.tracedin.domain.project.ProjectStatistic.StatisticsType;
import com.univ.tracedin.domain.span.Topology;
import com.univ.tracedin.domain.user.UserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectApi implements ProjectApiDocs {

    private final ProjectService projectService;

    @Override
    @PostMapping
    public Response<ProjectKey> createProject(
            @RequestBody CreateProjectRequest request, Long userId) {
        return Response.success(
                projectService.create(UserId.from(userId), request.toProjectInfo()));
    }

    @Override
    @GetMapping
    public Response<List<ProjectResponse>> projectList(Long userId) {
        final List<ProjectResponse> responses =
                projectService.getProjectList(UserId.from(userId)).stream()
                        .map(ProjectResponse::from)
                        .toList();
        return Response.success(responses);
    }

    @Override
    @DeleteMapping("/{projectId}")
    public Response<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(ProjectId.from(projectId));
        return Response.success();
    }

    @Override
    @GetMapping("/{projectKey}/service-nodes")
    public Response<List<NodeResponse>> serviceNodes(@PathVariable String projectKey) {
        final List<NodeResponse> responses =
                projectService.getServiceNodeList(ProjectKey.from(projectKey)).stream()
                        .map(NodeResponse::from)
                        .toList();
        return Response.success(responses);
    }

    @Override
    @GetMapping("/service-endpoints")
    public Response<List<String>> serviceEndpoints(ServiceSearchRequest request) {
        final List<String> response =
                projectService.getServiceEndpoints(request.toCondition()).stream()
                        .map(EndPointUrl::value)
                        .toList();
        return Response.success(response);
    }

    @Override
    @GetMapping("/{projectKey}/network-topology")
    public Response<Topology> networkTopology(@PathVariable String projectKey) {
        return Response.success(projectService.getNetworkTopology(ProjectKey.from(projectKey)));
    }

    @Override
    @GetMapping("/statistics/{statisticsType}")
    public Response<ProjectStatistic<?>> statistics(
            @PathVariable StatisticsType statisticsType, TraceSearchRequest request) {
        return Response.success(
                projectService.getStatistics(request.toCondition(), statisticsType));
    }

    @Override
    @PostMapping("/{projectId}/members")
    public Response<Void> addMember(@PathVariable Long projectId, AddMemberRequest request) {
        projectService.addMember(
                ProjectId.from(projectId), request.targetMemberEmail(), request.role());
        return Response.success();
    }

    @Override
    @DeleteMapping("/members/{projectMemberId}")
    public Response<Void> removeMember(@PathVariable Long projectMemberId) {
        projectService.removeMember(ProjectMemberId.from(projectMemberId));
        return Response.success();
    }

    @Override
    @PatchMapping("/members/{projectMemberId}")
    public Response<Void> changeRole(@PathVariable Long projectMemberId, MemberRole targetRole) {
        projectService.changeRole(ProjectMemberId.from(projectMemberId), targetRole);
        return Response.success();
    }
}
