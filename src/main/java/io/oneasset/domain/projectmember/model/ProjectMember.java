package io.oneasset.domain.projectmember.model;

import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.projectmember.vo.ProjectMemberId;
import io.oneasset.domain.projectmember.vo.ProjectRole;
import io.oneasset.domain.user.vo.UserId;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public final class ProjectMember {

  private final ProjectMemberId id;
  private final ProjectId projectId;
  private final UserId userId;
  private final ProjectRole role;
  private final LocalDateTime createdAt;

  private ProjectMember(
      ProjectMemberId id,
      ProjectId projectId,
      UserId userId,
      ProjectRole role,
      LocalDateTime createdAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
    this.userId = Objects.requireNonNull(userId, "userId must not be null");
    this.role = Objects.requireNonNull(role, "role must not be null");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
  }

  public static ProjectMember createOwner(ProjectId projectId, UserId userId) {
    return create(projectId, userId, ProjectRole.OWNER);
  }

  public static ProjectMember createMember(ProjectId projectId, UserId userId) {
    return create(projectId, userId, ProjectRole.MEMBER);
  }

  public static ProjectMember reconstitute(
      ProjectMemberId id,
      ProjectId projectId,
      UserId userId,
      ProjectRole role,
      LocalDateTime createdAt) {
    return new ProjectMember(id, projectId, userId, role, createdAt);
  }

  public boolean isOwner() {
    return role == ProjectRole.OWNER;
  }

  public boolean isMember() {
    return role == ProjectRole.MEMBER;
  }

  private static ProjectMember create(ProjectId projectId, UserId userId, ProjectRole role) {
    return new ProjectMember(ProjectMemberId.newId(), projectId, userId, role, LocalDateTime.now());
  }
}
