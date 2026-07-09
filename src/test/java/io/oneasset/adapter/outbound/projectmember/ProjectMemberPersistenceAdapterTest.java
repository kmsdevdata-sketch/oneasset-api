package io.oneasset.adapter.outbound.projectmember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.outbound.projectmember.entity.ProjectMemberEntity;
import io.oneasset.adapter.outbound.projectmember.persistence.ProjectMemberJpaRepository;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.projectmember.model.ProjectMember;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProjectMemberPersistenceAdapterTest {

  private final ProjectMemberJpaRepository projectMemberJpaRepository =
      mock(ProjectMemberJpaRepository.class);
  private final ProjectMemberPersistenceAdapter adapter =
      new ProjectMemberPersistenceAdapter(projectMemberJpaRepository);

  @Test
  void savesProjectMemberEntityConvertedFromDomain() {
    ProjectMember member = ProjectMember.createOwner(ProjectId.newId(), UserId.newId());

    adapter.save(member);

    verify(projectMemberJpaRepository).save(any(ProjectMemberEntity.class));
  }

  @Test
  void findsProjectMemberByProjectIdAndUserId() {
    ProjectId projectId = ProjectId.newId();
    UserId userId = UserId.newId();
    ProjectMember member = ProjectMember.createOwner(projectId, userId);
    when(projectMemberJpaRepository.findByProjectIdAndUserId(projectId.value(), userId.value()))
        .thenReturn(Optional.of(ProjectMemberEntity.from(member)));

    Optional<ProjectMember> found = adapter.findByProjectIdAndUserId(projectId, userId);

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(member.getId());
    verify(projectMemberJpaRepository).findByProjectIdAndUserId(projectId.value(), userId.value());
  }

  @Test
  void findsAllProjectMembersByProjectId() {
    ProjectId projectId = ProjectId.newId();
    ProjectMember member = ProjectMember.createOwner(projectId, UserId.newId());
    when(projectMemberJpaRepository.findAllByProjectId(projectId.value()))
        .thenReturn(List.of(ProjectMemberEntity.from(member)));

    List<ProjectMember> found = adapter.findAllByProjectId(projectId);

    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getProjectId()).isEqualTo(projectId);
    verify(projectMemberJpaRepository).findAllByProjectId(projectId.value());
  }

  @Test
  void findsAllProjectMembersByUserId() {
    UserId userId = UserId.newId();
    ProjectMember member = ProjectMember.createMember(ProjectId.newId(), userId);
    when(projectMemberJpaRepository.findAllByUserId(userId.value()))
        .thenReturn(List.of(ProjectMemberEntity.from(member)));

    List<ProjectMember> found = adapter.findAllByUserId(userId);

    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getUserId()).isEqualTo(userId);
    verify(projectMemberJpaRepository).findAllByUserId(userId.value());
  }
}
