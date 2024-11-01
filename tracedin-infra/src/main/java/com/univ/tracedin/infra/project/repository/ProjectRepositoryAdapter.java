package com.univ.tracedin.infra.project.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.project.NetworkTopology.Node;
import com.univ.tracedin.domain.project.NetworkTopology.NodeType;
import com.univ.tracedin.domain.project.Project;
import com.univ.tracedin.domain.project.ProjectId;
import com.univ.tracedin.domain.project.ProjectKey;
import com.univ.tracedin.domain.project.ProjectMember;
import com.univ.tracedin.domain.project.ProjectMemberId;
import com.univ.tracedin.domain.project.ProjectRepository;
import com.univ.tracedin.domain.user.User;
import com.univ.tracedin.infra.project.entity.ProjectEntity;
import com.univ.tracedin.infra.project.entity.ProjectMemberEntity;
import com.univ.tracedin.infra.project.exception.ProjectMemberNotFoundException;
import com.univ.tracedin.infra.project.exception.ProjectNotFoundException;
import com.univ.tracedin.infra.span.repository.SpanElasticSearchRepository;

@Repository
@Transactional
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMemberJpaRepository projectMemberJpaRepository;
    private final SpanElasticSearchRepository spanElasticSearchRepository;

    @Override
    public Project save(Project project) {
        return projectJpaRepository.save(ProjectEntity.from(project)).toDomain();
    }

    @Override
    public Project findById(ProjectId projectId) {
        return projectJpaRepository
                .findById(projectId.getValue())
                .map(ProjectEntity::toDomain)
                .orElseThrow(() -> ProjectNotFoundException.EXCEPTION);
    }

    @Override
    public List<Project> findAllByIds(List<ProjectId> projectIds) {
        List<Long> ids = projectIds.stream().map(ProjectId::getValue).toList();
        return projectJpaRepository.findAllInIds(ids).stream()
                .map(ProjectEntity::toDomain)
                .toList();
    }

    @Override
    public Project findByKey(ProjectKey projectKey) {
        return projectJpaRepository
                .findByProjectKey(projectKey.value())
                .map(ProjectEntity::toDomain)
                .orElseThrow(() -> ProjectNotFoundException.EXCEPTION);
    }

    @Override
    public boolean existsByKey(ProjectKey projectKey) {
        return projectJpaRepository.existsByProjectKey(projectKey.value());
    }

    @Override
    public List<Node> findServiceNodeList(ProjectKey projectKey) {
        final List<String> serviceNames =
                spanElasticSearchRepository.findServiceNames(projectKey.value());
        return serviceNames.stream()
                .map(name -> Node.of(projectKey, name, NodeType.SERVICE))
                .toList();
    }

    @Override
    public ProjectMember saveProjectMember(ProjectMember projectMember) {
        return projectMemberJpaRepository.save(ProjectMemberEntity.from(projectMember)).toDomain();
    }

    @Override
    public void deleteProjectMember(ProjectMember projectMember) {
        projectMemberJpaRepository.delete(ProjectMemberEntity.from(projectMember));
    }

    @Override
    public ProjectMember findProjectMemberById(ProjectMemberId id) {
        return projectMemberJpaRepository
                .findById(id.getValue())
                .map(ProjectMemberEntity::toDomain)
                .orElseThrow(() -> ProjectMemberNotFoundException.EXCEPTION);
    }

    @Override
    public List<ProjectMember> findProjectMembersByUser(User user) {
        return projectMemberJpaRepository.findByMemberId(user.getId().getValue()).stream()
                .map(ProjectMemberEntity::toDomain)
                .toList();
    }

    @Override
    public void delete(Project project) {
        projectJpaRepository.delete(ProjectEntity.from(project));
    }

    @Override
    public List<ProjectMember> findProjectMembersByProject(Project project) {
        return projectMemberJpaRepository.findByProjectId(project.getId().getValue()).stream()
                .map(ProjectMemberEntity::toDomain)
                .toList();
    }
}
