package io.oneasset.adapter.outbound.project.entity;

import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.project.vo.ProjectId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "slug", nullable = false)
  private String slug;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  private ProjectEntity(
      UUID id,
      String name,
      String slug,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    this.id = id;
    this.name = name;
    this.slug = slug;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
  }

  public static ProjectEntity from(Project project) {
    return new ProjectEntity(
        project.getId().value(),
        project.getName(),
        project.getSlug(),
        project.getCreatedAt(),
        project.getUpdatedAt(),
        project.getDeletedAt());
  }

  public Project toDomain() {
    return Project.reconstitute(ProjectId.of(id), name, slug, createdAt, updatedAt, deletedAt);
  }
}
