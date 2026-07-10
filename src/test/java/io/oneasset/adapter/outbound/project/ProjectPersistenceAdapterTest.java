package io.oneasset.adapter.outbound.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.outbound.project.entity.ProjectEntity;
import io.oneasset.adapter.outbound.project.persistence.ProjectJpaRepository;
import io.oneasset.adapter.outbound.projectmember.entity.ProjectMemberEntity;
import io.oneasset.adapter.outbound.projectmember.persistence.ProjectMemberJpaRepository;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.projectmember.model.ProjectMember;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProjectPersistenceAdapterTest {

  private final ProjectJpaRepository projectJpaRepository = mock(ProjectJpaRepository.class);
  private final ProjectMemberJpaRepository projectMemberJpaRepository =
      mock(ProjectMemberJpaRepository.class);
  private final ProjectPersistenceAdapter adapter =
      new ProjectPersistenceAdapter(projectJpaRepository, projectMemberJpaRepository);

  @Test
  void savesProjectEntityConvertedFromDomain() {
    Project project = Project.create("My Blog", "my-blog");
    when(projectJpaRepository.save(any(ProjectEntity.class)))
        .thenReturn(ProjectEntity.from(project));

    Project saved = adapter.save(project);

    assertThat(saved.getId()).isEqualTo(project.getId());
    verify(projectJpaRepository).save(any(ProjectEntity.class));
  }

  @Test
  void savesProjectMemberEntityConvertedFromDomain() {
    ProjectMember member =
        ProjectMember.createOwner(Project.create("My Blog", "my-blog").getId(), UserId.newId());

    adapter.save(member);

    verify(projectMemberJpaRepository).save(any(ProjectMemberEntity.class));
  }

  @Test
  void findsActiveProjectById() {
    Project project = Project.create("My Blog", "my-blog");
    when(projectJpaRepository.findByIdAndDeletedAtIsNull(project.getId().value()))
        .thenReturn(Optional.of(ProjectEntity.from(project)));

    Optional<Project> found = adapter.findById(project.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(project.getId());
    verify(projectJpaRepository).findByIdAndDeletedAtIsNull(project.getId().value());
  }

  @Test
  void findsActiveProjectBySlug() {
    Project project = Project.create("My Blog", "my-blog");
    when(projectJpaRepository.findBySlugAndDeletedAtIsNull("my-blog"))
        .thenReturn(Optional.of(ProjectEntity.from(project)));

    Optional<Project> found = adapter.findBySlug("my-blog");

    assertThat(found).isPresent();
    assertThat(found.get().getSlug()).isEqualTo("my-blog");
    verify(projectJpaRepository).findBySlugAndDeletedAtIsNull("my-blog");
  }

  @Test
  void findsProjectsByIds() {
    Project project = Project.create("My Blog", "my-blog");
    when(projectJpaRepository.findAllByIdInAndDeletedAtIsNull(
            List.of(project.getId().value())))
        .thenReturn(List.of(ProjectEntity.from(project)));

    List<Project> found = adapter.findAllByIds(List.of(project.getId()));

    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getId()).isEqualTo(project.getId());
    verify(projectJpaRepository)
        .findAllByIdInAndDeletedAtIsNull(List.of(project.getId().value()));
  }

  @Test
  void findsProjectMemberByProjectIdAndUserId() {
    Project project = Project.create("My Blog", "my-blog");
    UserId userId = UserId.newId();
    ProjectMember member = ProjectMember.createOwner(project.getId(), userId);
    when(projectMemberJpaRepository.findByProjectIdAndUserId(
            project.getId().value(), userId.value()))
        .thenReturn(Optional.of(ProjectMemberEntity.from(member)));

    Optional<ProjectMember> found = adapter.findMember(project.getId(), userId);

    assertThat(found).isPresent();
    assertThat(found.get().getProjectId()).isEqualTo(project.getId());
    verify(projectMemberJpaRepository)
        .findByProjectIdAndUserId(project.getId().value(), userId.value());
  }

  @Test
  void findsAllProjectMembersByUserId() {
    UserId userId = UserId.newId();
    ProjectMember member =
        ProjectMember.createMember(Project.create("My Blog", "my-blog").getId(), userId);
    when(projectMemberJpaRepository.findAllByUserId(userId.value()))
        .thenReturn(List.of(ProjectMemberEntity.from(member)));

    List<ProjectMember> found = adapter.findAllMembersByUserId(userId);

    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getUserId()).isEqualTo(userId);
    verify(projectMemberJpaRepository).findAllByUserId(userId.value());
  }
}
