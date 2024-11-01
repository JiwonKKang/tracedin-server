package com.univ.tracedin.api.project;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.univ.tracedin.api.global.dto.Response;
import com.univ.tracedin.api.project.dto.AddMemberRequest;
import com.univ.tracedin.api.project.dto.CreateProjectRequest;
import com.univ.tracedin.api.project.dto.NodeResponse;
import com.univ.tracedin.api.project.dto.ProjectResponse;
import com.univ.tracedin.api.project.dto.ServiceSearchRequest;
import com.univ.tracedin.api.project.dto.TraceSearchRequest;
import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.project.ProjectMember.MemberRole;
import com.univ.tracedin.domain.project.ProjectStatistic;
import com.univ.tracedin.domain.project.ProjectStatistic.StatisticsType;
import com.univ.tracedin.domain.span.Topology;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "프로젝트 API")
public interface ProjectApiDocs {

    @Operation(summary = "프로젝트 생성", description = "프로젝트를 생성하고 프로젝트 키를 반환합니다.")
    Response<ProjectKey> createProject(CreateProjectRequest request, Long userId);

    @Operation(summary = "프로젝트 리스트 조회", description = "사용자의 프로젝트 리스트를 조회합니다.")
    Response<List<ProjectResponse>> projectList(Long userId);

    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    Response<Void> deleteProject(Long projectId);

    @Operation(summary = "서비스 리스트 조회", description = "프로젝트의 서비스 노드 리스트를 조회합니다.")
    Response<List<NodeResponse>> serviceNodes(String projectKey);

    @Operation(summary = "서비스의 엔드포인트 리스트 조회", description = "프로젝트의 서비스의 엔드포인트 리스트를 조회합니다.")
    Response<List<String>> serviceEndpoints(ServiceSearchRequest request);

    @Operation(summary = "네트워크 토폴로지 조회", description = "프로젝트의 네트워크 토폴로지를 조회합니다.")
    Response<Topology> networkTopology(String projectKey);

    @Operation(
            summary = "프로젝트 통계 조회",
            description = "프로젝트의 통계를 조회합니다. 통계 타입(HTTP TPS, STATUS_CODE, TRACE_HIP_MAP)")
    Response<ProjectStatistic<?>> statistics(
            @PathVariable StatisticsType statisticsType, TraceSearchRequest request);

    @Operation(summary = "프로젝트 멤버 추가", description = "프로젝트에 멤버를 추가합니다.")
    Response<Void> addMember(Long projectId, AddMemberRequest request);

    @Operation(summary = "프로젝트 멤버 삭제", description = "프로젝트의 멤버를 삭제합니다.")
    Response<Void> removeMember(Long projectMemberId);

    @Operation(summary = "프로젝트 멤버 권한 변경", description = "프로젝트의 멤버 권한을 변경합니다.")
    Response<Void> changeRole(Long projectMemberId, MemberRole targetRole);
}
