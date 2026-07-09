package io.oneasset.domain.projectmember.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProjectMemberIdTest {

  @Test
  void createsProjectMemberIdFromUuidString() {
    UUID value = UUID.randomUUID();

    ProjectMemberId projectMemberId = ProjectMemberId.fromString(value.toString());

    assertThat(projectMemberId.value()).isEqualTo(value);
    assertThat(projectMemberId.toString()).isEqualTo(value.toString());
  }

  @Test
  void rejectsInvalidUuidString() {
    assertThatThrownBy(() -> ProjectMemberId.fromString("not-a-uuid"))
        .isInstanceOf(RuntimeException.class);
  }
}
