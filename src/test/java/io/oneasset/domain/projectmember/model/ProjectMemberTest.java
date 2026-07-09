package io.oneasset.domain.projectmember.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.projectmember.vo.ProjectMemberId;
import io.oneasset.domain.projectmember.vo.ProjectRole;
import io.oneasset.domain.user.vo.UserId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ProjectMemberTest {

  @Test
  void createsOwner() {
    ProjectId projectId = ProjectId.newId();
    UserId userId = UserId.newId();

    ProjectMember member = ProjectMember.createOwner(projectId, userId);

    assertThat(member.getId()).isNotNull();
    assertThat(member.getProjectId()).isEqualTo(projectId);
    assertThat(member.getUserId()).isEqualTo(userId);
    assertThat(member.getRole()).isEqualTo(ProjectRole.OWNER);
    assertThat(member.isOwner()).isTrue();
    assertThat(member.isMember()).isFalse();
    assertThat(member.getCreatedAt()).isNotNull();
  }

  @Test
  void createsMember() {
    ProjectMember member = ProjectMember.createMember(ProjectId.newId(), UserId.newId());

    assertThat(member.getRole()).isEqualTo(ProjectRole.MEMBER);
    assertThat(member.isOwner()).isFalse();
    assertThat(member.isMember()).isTrue();
  }

  @Test
  void reconstitutesPersistedProjectMember() {
    ProjectMemberId id = ProjectMemberId.newId();
    ProjectId projectId = ProjectId.newId();
    UserId userId = UserId.newId();
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 12, 0);

    ProjectMember member =
        ProjectMember.reconstitute(id, projectId, userId, ProjectRole.OWNER, createdAt);

    assertThat(member.getId()).isEqualTo(id);
    assertThat(member.getProjectId()).isEqualTo(projectId);
    assertThat(member.getUserId()).isEqualTo(userId);
    assertThat(member.getRole()).isEqualTo(ProjectRole.OWNER);
    assertThat(member.getCreatedAt()).isEqualTo(createdAt);
  }
}
