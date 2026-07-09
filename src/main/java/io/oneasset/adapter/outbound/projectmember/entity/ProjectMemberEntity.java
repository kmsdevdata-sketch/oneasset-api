package io.oneasset.adapter.outbound.projectmember.entity;

import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.projectmember.model.ProjectMember;
import io.oneasset.domain.projectmember.vo.ProjectMemberId;
import io.oneasset.domain.projectmember.vo.ProjectRole;
import io.oneasset.domain.user.vo.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMemberEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "project_id", nullable = false)
  private UUID projectId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private ProjectRole role;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  private ProjectMemberEntity(
      UUID id, UUID projectId, UUID userId, ProjectRole role, LocalDateTime createdAt) {
    this.id = id;
    this.projectId = projectId;
    this.userId = userId;
    this.role = role;
    this.createdAt = createdAt;
  }

  public static ProjectMemberEntity from(ProjectMember projectMember) {
    return new ProjectMemberEntity(
        projectMember.getId().value(),
        projectMember.getProjectId().value(),
        projectMember.getUserId().value(),
        projectMember.getRole(),
        projectMember.getCreatedAt());
  }

  public ProjectMember toDomain() {
    return ProjectMember.reconstitute(
        ProjectMemberId.of(id), ProjectId.of(projectId), UserId.of(userId), role, createdAt);
  }
}
