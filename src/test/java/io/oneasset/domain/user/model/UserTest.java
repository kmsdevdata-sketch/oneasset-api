package io.oneasset.domain.user.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.oneasset.domain.user.vo.UserId;
import io.oneasset.domain.user.vo.UserRole;
import io.oneasset.domain.user.vo.UserStatus;
import java.time.LocalDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void createsLocalUserFromVerifiedCognitoClaims() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");

    assertThat(user.getId()).isNotNull();
    assertThat(user.getCognitoSub()).isEqualTo("cognito-sub-1");
    assertThat(user.getEmail()).isEqualTo("user@example.com");
    assertThat(user.getName()).isEqualTo("Minseo");
    assertThat(user.getRole()).isEqualTo(UserRole.USER);
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(user.getCreatedAt()).isNotNull();
    assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
  }

  @Test
  void rejectsInvalidCognitoClaims() {
    assertDomainFailure(() -> User.createFromCognito(" ", "user@example.com", "Minseo"));
    assertDomainFailure(() -> User.createFromCognito("cognito-sub-1", " ", "Minseo"));
    assertDomainFailure(() -> User.createFromCognito("cognito-sub-1", "user@example.com", " "));
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
            UserRole.ADMIN,
            UserStatus.ACTIVE,
            createdAt,
            updatedAt);

    assertThat(user.getId()).isEqualTo(id);
    assertThat(user.getCognitoSub()).isEqualTo("cognito-sub-1");
    assertThat(user.getEmail()).isEqualTo("user@example.com");
    assertThat(user.getName()).isEqualTo("Minseo");
    assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    assertThat(user.isAdmin()).isTrue();
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  void rejectsPersistedUserWhenUpdatedAtIsBeforeCreatedAt() {
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 1, 30);
    LocalDateTime updatedAt = LocalDateTime.of(2026, 7, 9, 1, 0);

    assertDomainFailure(
        () ->
            User.reconstitute(
                UserId.newId(),
                "cognito-sub-1",
                "user@example.com",
                "Minseo",
                UserRole.USER,
                UserStatus.ACTIVE,
                createdAt,
                updatedAt));
  }

  @Test
  void syncsProfileWhenCognitoClaimsChange() {
    User user = User.createFromCognito("cognito-sub-1", "old@example.com", "Old");
    LocalDateTime beforeSync = user.getUpdatedAt();

    user.syncProfile("new@example.com", "New");

    assertThat(user.getEmail()).isEqualTo("new@example.com");
    assertThat(user.getName()).isEqualTo("New");
    assertThat(user.getUpdatedAt()).isAfterOrEqualTo(beforeSync);
  }

  @Test
  void doesNotTouchUpdatedAtWhenProfileIsNotChanged() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    LocalDateTime beforeSync = user.getUpdatedAt();

    user.syncProfile("user@example.com", "Minseo");

    assertThat(user.getUpdatedAt()).isEqualTo(beforeSync);
  }

  @Test
  void rejectsInvalidProfileSyncInput() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");

    assertDomainFailure(() -> user.syncProfile(" ", "Minseo"));
    assertDomainFailure(() -> user.syncProfile("user@example.com", " "));
  }

  @Test
  void withdrawsActiveUser() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");

    user.withdraw();

    assertThat(user.getStatus()).isEqualTo(UserStatus.WITHDRAWN);
    assertThat(user.isActive()).isFalse();
  }

  @Test
  void bansActiveUser() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");

    user.ban();

    assertThat(user.getStatus()).isEqualTo(UserStatus.BANNED);
    assertThat(user.isActive()).isFalse();
  }

  @Test
  void doesNotAllowTerminalUserToChangeStateAgain() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    user.withdraw();

    assertDomainFailure(user::ban);
    assertDomainFailure(user::withdraw);
  }

  private static void assertDomainFailure(ThrowingCallable action) {
    assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
  }
}
