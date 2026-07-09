package io.oneasset.adapter.outbound.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.outbound.project.entity.ProjectEntity;
import io.oneasset.adapter.outbound.project.persistence.ProjectJpaRepository;
import io.oneasset.domain.project.model.Project;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProjectPersistenceAdapterTest {

  private final ProjectJpaRepository projectJpaRepository = mock(ProjectJpaRepository.class);
  private final ProjectPersistenceAdapter adapter =
      new ProjectPersistenceAdapter(projectJpaRepository);

  @Test
  void savesProjectEntityConvertedFromDomain() {
    Project project = Project.create("My Blog", "my-blog");

    adapter.save(project);

    verify(projectJpaRepository).save(any(ProjectEntity.class));
  }

  @Test
  void findsActiveProjectById() {
    Project project = Project.create("My Blog", "my-blog");
    when(projectJpaRepository.findByIdAndDeletedAtIsNull(project.getId().value()))
        .thenReturn(Optional.of(ProjectEntity.from(project)));

    Optional<Project> found = adapter.findActiveById(project.getId().value());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(project.getId());
    verify(projectJpaRepository).findByIdAndDeletedAtIsNull(project.getId().value());
  }

  @Test
  void findsActiveProjectBySlug() {
    Project project = Project.create("My Blog", "my-blog");
    when(projectJpaRepository.findBySlugAndDeletedAtIsNull("my-blog"))
        .thenReturn(Optional.of(ProjectEntity.from(project)));

    Optional<Project> found = adapter.findActiveBySlug("my-blog");

    assertThat(found).isPresent();
    assertThat(found.get().getSlug()).isEqualTo("my-blog");
    verify(projectJpaRepository).findBySlugAndDeletedAtIsNull("my-blog");
  }
}
