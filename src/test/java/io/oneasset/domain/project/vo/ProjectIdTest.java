package io.oneasset.domain.project.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProjectIdTest {

  @Test
  void createsProjectIdFromUuidString() {
    UUID value = UUID.randomUUID();

    ProjectId projectId = ProjectId.fromString(value.toString());

    assertThat(projectId.value()).isEqualTo(value);
    assertThat(projectId.toString()).isEqualTo(value.toString());
  }

  @Test
  void rejectsInvalidUuidString() {
    assertThatThrownBy(() -> ProjectId.fromString("not-a-uuid"))
        .isInstanceOf(RuntimeException.class);
  }
}
