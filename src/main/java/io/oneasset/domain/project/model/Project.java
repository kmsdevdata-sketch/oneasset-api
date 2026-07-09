package io.oneasset.domain.project.model;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.domain.project.vo.ProjectId;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public final class Project {

  private final ProjectId id;
  private final String slug;
  private final LocalDateTime createdAt;

  private String name;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  private Project(
      ProjectId id,
      String name,
      String slug,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.name = requireText(name, "name");
    this.slug = requireText(slug, "slug");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    this.deletedAt = deletedAt;

    if (updatedAt.isBefore(createdAt)) {
      throw new IllegalArgumentException("updatedAt must not be before createdAt");
    }

    if (deletedAt != null && deletedAt.isBefore(createdAt)) {
      throw new IllegalArgumentException("deletedAt must not be before createdAt");
    }
  }

  public static Project create(String name, String slug) {
    LocalDateTime now = LocalDateTime.now();
    return new Project(ProjectId.newId(), name, slug, now, now, null);
  }

  public static Project reconstitute(
      ProjectId id,
      String name,
      String slug,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    return new Project(id, name, slug, createdAt, updatedAt, deletedAt);
  }

  public void rename(String name) {
    ensureNotDeleted();
    String nextName = requireText(name, "name");

    if (this.name.equals(nextName)) {
      return;
    }

    this.name = nextName;
    this.updatedAt = LocalDateTime.now();
  }

  public void delete() {
    ensureNotDeleted();
    LocalDateTime now = LocalDateTime.now();
    this.deletedAt = now;
    this.updatedAt = now;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

  private void ensureNotDeleted() {
    if (isDeleted()) {
      throw new IllegalStateException("Deleted project cannot be changed");
    }
  }
}
