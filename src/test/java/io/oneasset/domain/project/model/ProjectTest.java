package io.oneasset.domain.project.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.oneasset.domain.project.vo.ProjectId;
import java.time.LocalDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class ProjectTest {

  @Test
  void createsProject() {
    Project project = Project.create("My Blog", "my-blog");

    assertThat(project.getId()).isNotNull();
    assertThat(project.getName()).isEqualTo("My Blog");
    assertThat(project.getSlug()).isEqualTo("my-blog");
    assertThat(project.getCreatedAt()).isNotNull();
    assertThat(project.getUpdatedAt()).isEqualTo(project.getCreatedAt());
    assertThat(project.getDeletedAt()).isNull();
    assertThat(project.isDeleted()).isFalse();
  }

  @Test
  void rejectsInvalidCreateInput() {
    assertDomainFailure(() -> Project.create(" ", "my-blog"));
    assertDomainFailure(() -> Project.create("My Blog", " "));
  }

  @Test
  void reconstitutesPersistedProject() {
    ProjectId id = ProjectId.newId();
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 10, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2026, 7, 9, 10, 30);
    LocalDateTime deletedAt = LocalDateTime.of(2026, 7, 9, 11, 0);

    Project project =
        Project.reconstitute(id, "My Blog", "my-blog", createdAt, updatedAt, deletedAt);

    assertThat(project.getId()).isEqualTo(id);
    assertThat(project.getName()).isEqualTo("My Blog");
    assertThat(project.getSlug()).isEqualTo("my-blog");
    assertThat(project.getCreatedAt()).isEqualTo(createdAt);
    assertThat(project.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(project.getDeletedAt()).isEqualTo(deletedAt);
    assertThat(project.isDeleted()).isTrue();
  }

  @Test
  void rejectsInvalidPersistedTimestamps() {
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 10, 30);
    LocalDateTime earlier = LocalDateTime.of(2026, 7, 9, 10, 0);

    assertDomainFailure(() ->
        Project.reconstitute(ProjectId.newId(), "My Blog", "my-blog", createdAt, earlier, null));
    assertDomainFailure(() -> Project.reconstitute(
        ProjectId.newId(), "My Blog", "my-blog", createdAt, createdAt, earlier));
  }

  @Test
  void renamesProject() {
    Project project = Project.create("My Blog", "my-blog");
    LocalDateTime beforeRename = project.getUpdatedAt();

    project.rename("New Blog");

    assertThat(project.getName()).isEqualTo("New Blog");
    assertThat(project.getSlug()).isEqualTo("my-blog");
    assertThat(project.getUpdatedAt()).isAfterOrEqualTo(beforeRename);
  }

  @Test
  void doesNotTouchUpdatedAtWhenNameIsNotChanged() {
    Project project = Project.create("My Blog", "my-blog");
    LocalDateTime beforeRename = project.getUpdatedAt();

    project.rename("My Blog");

    assertThat(project.getUpdatedAt()).isEqualTo(beforeRename);
  }

  @Test
  void deletesProject() {
    Project project = Project.create("My Blog", "my-blog");
    LocalDateTime beforeDelete = project.getUpdatedAt();

    project.delete();

    assertThat(project.isDeleted()).isTrue();
    assertThat(project.getDeletedAt()).isNotNull();
    assertThat(project.getUpdatedAt()).isAfterOrEqualTo(beforeDelete);
  }

  @Test
  void doesNotAllowDeletedProjectToChangeAgain() {
    Project project = Project.create("My Blog", "my-blog");
    project.delete();

    assertDomainFailure(() -> project.rename("New Blog"));
    assertDomainFailure(project::delete);
  }

  private static void assertDomainFailure(ThrowingCallable action) {
    assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
  }
}
