package io.oneasset.adapter.outbound.projectmember;

import io.oneasset.adapter.outbound.projectmember.entity.ProjectMemberEntity;
import io.oneasset.adapter.outbound.projectmember.persistence.ProjectMemberJpaRepository;
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
public class ProjectMemberPersistenceAdapter {

  private final ProjectMemberJpaRepository projectMemberJpaRepository;

  public void save(ProjectMember projectMember) {
    projectMemberJpaRepository.save(ProjectMemberEntity.from(projectMember));
  }

  @Transactional(readOnly = true)
  public Optional<ProjectMember> findByProjectIdAndUserId(ProjectId projectId, UserId userId) {
    return projectMemberJpaRepository
        .findByProjectIdAndUserId(projectId.value(), userId.value())
        .map(ProjectMemberEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public List<ProjectMember> findAllByProjectId(ProjectId projectId) {
    return projectMemberJpaRepository.findAllByProjectId(projectId.value()).stream()
        .map(ProjectMemberEntity::toDomain)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<ProjectMember> findAllByUserId(UserId userId) {
    return projectMemberJpaRepository.findAllByUserId(userId.value()).stream()
        .map(ProjectMemberEntity::toDomain)
        .toList();
  }
}
