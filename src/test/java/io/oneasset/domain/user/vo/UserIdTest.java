package io.oneasset.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserIdTest {

  @Test
  void createsUserIdFromUuidString() {
    UUID value = UUID.randomUUID();

    UserId userId = UserId.fromString(value.toString());

    assertThat(userId.value()).isEqualTo(value);
    assertThat(userId.toString()).isEqualTo(value.toString());
  }

  @Test
  void rejectsInvalidUuidString() {
    assertThatThrownBy(() -> UserId.fromString("not-a-uuid")).isInstanceOf(RuntimeException.class);
  }
}
