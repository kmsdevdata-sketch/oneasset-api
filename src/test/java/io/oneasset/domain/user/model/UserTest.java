package io.oneasset.domain.user.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.oneasset.domain.user.vo.UserId;
import io.oneasset.domain.user.vo.UserStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void createsLocalUserFromVerifiedCognitoClaims() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");

    assertThat(user.id()).isNotNull();
    assertThat(user.cognitoSub()).isEqualTo("cognito-sub-1");
    assertThat(user.email()).isEqualTo("user@example.com");
    assertThat(user.name()).isEqualTo("Minseo");
    assertThat(user.status()).isEqualTo(UserStatus.ACTIVE);
    assertThat(user.createdAt()).isNotNull();
    assertThat(user.updatedAt()).isEqualTo(user.createdAt());
  }

  @Test
  void rejectsBlankCognitoSubject() {
    assertThatThrownBy(() -> User.createFromCognito(" ", "user@example.com", "Minseo"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void reconstitutesPersistedUser() {
    UserId id = UserId.newId();
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 1, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2026, 7, 9, 1, 30);

    User user =
        User.reconstitute(
            id,
            "cognito-sub-1",
            "user@example.com",
            "Minseo",
            UserStatus.ACTIVE,
            createdAt,
            updatedAt);

    assertThat(user.id()).isEqualTo(id);
    assertThat(user.createdAt()).isEqualTo(createdAt);
    assertThat(user.updatedAt()).isEqualTo(updatedAt);
  }

  @Test
  void syncsProfileWhenCognitoClaimsChange() {
    User user = User.createFromCognito("cognito-sub-1", "old@example.com", "Old");
    LocalDateTime beforeSync = user.updatedAt();

    user.syncProfile("new@example.com", "New");

    assertThat(user.email()).isEqualTo("new@example.com");
    assertThat(user.name()).isEqualTo("New");
    assertThat(user.updatedAt()).isAfterOrEqualTo(beforeSync);
  }

  @Test
  void withdrawsActiveUser() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");

    user.withdraw();

    assertThat(user.status()).isEqualTo(UserStatus.WITHDRAWN);
    assertThat(user.isActive()).isFalse();
  }

  @Test
  void doesNotAllowTerminalStateTransitionAgain() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    user.withdraw();

    assertThatThrownBy(user::ban).isInstanceOf(IllegalStateException.class);
  }
}
