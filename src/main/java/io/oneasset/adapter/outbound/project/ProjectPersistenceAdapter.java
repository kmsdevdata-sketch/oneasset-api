package io.oneasset.adapter.outbound.project;

import io.oneasset.adapter.outbound.project.entity.ProjectEntity;
import io.oneasset.adapter.outbound.project.persistence.ProjectJpaRepository;
import io.oneasset.adapter.outbound.projectmember.entity.ProjectMemberEntity;
import io.oneasset.adapter.outbound.projectmember.persistence.ProjectMemberJpaRepository;
import io.oneasset.application.project.required.ProjectPersistencePort;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.projectmember.model.ProjectMember;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class ProjectPersistenceAdapter implements ProjectPersistencePort {

  private final ProjectJpaRepository projectJpaRepository;
  private final ProjectMemberJpaRepository projectMemberJpaRepository;

  @Override
  public Project save(Project project) {
    return projectJpaRepository.save(ProjectEntity.from(project)).toDomain();
  }

  @Override
  public void save(ProjectMember projectMember) {
    projectMemberJpaRepository.save(ProjectMemberEntity.from(projectMember));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Project> findById(ProjectId projectId) {
    return projectJpaRepository
        .findByIdAndDeletedAtIsNull(projectId.value())
        .map(ProjectEntity::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Project> findBySlug(String slug) {
    return projectJpaRepository.findBySlugAndDeletedAtIsNull(slug).map(ProjectEntity::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Project> findAllByIds(List<ProjectId> projectIds) {
    if (projectIds.isEmpty()) {
      return List.of();
    }

    List<java.util.UUID> ids = projectIds.stream().map(ProjectId::value).toList();
    return projectJpaRepository.findAllByIdInAndDeletedAtIsNull(ids).stream()
        .map(ProjectEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ProjectMember> findMember(ProjectId projectId, UserId userId) {
    return projectMemberJpaRepository
        .findByProjectIdAndUserId(projectId.value(), userId.value())
        .map(ProjectMemberEntity::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProjectMember> findAllMembersByUserId(UserId userId) {
    return projectMemberJpaRepository.findAllByUserId(userId.value()).stream()
        .map(ProjectMemberEntity::toDomain)
        .toList();
  }
}
