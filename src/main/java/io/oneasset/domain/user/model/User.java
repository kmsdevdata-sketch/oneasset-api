package io.oneasset.domain.user.model;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.domain.user.vo.UserId;
import io.oneasset.domain.user.vo.UserRole;
import io.oneasset.domain.user.vo.UserStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public final class User {

  private final UserId id;
  private final String cognitoSub;
  private final LocalDateTime createdAt;

  private String email;
  private String name;
  private UserRole role;
  private UserStatus status;
  private LocalDateTime updatedAt;

  private User(
      UserId id,
      String cognitoSub,
      String email,
      String name,
      UserRole role,
      UserStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.cognitoSub = requireText(cognitoSub, "cognitoSub");
    this.email = requireText(email, "email");
    this.name = requireText(name, "name");
    this.role = Objects.requireNonNull(role, "role must not be null");
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");

    if (updatedAt.isBefore(createdAt)) {
      throw new IllegalArgumentException("updatedAt must not be before createdAt");
    }
  }

  public static User createFromCognito(String cognitoSub, String email, String name) {
    LocalDateTime now = LocalDateTime.now();
    return new User(
        UserId.newId(), cognitoSub, email, name, UserRole.USER, UserStatus.ACTIVE, now, now);
  }

  public static User reconstitute(
      UserId id,
      String cognitoSub,
      String email,
      String name,
      UserRole role,
      UserStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    return new User(id, cognitoSub, email, name, role, status, createdAt, updatedAt);
  }

  public void syncProfile(String email, String name) {
    String nextEmail = requireText(email, "email");
    String nextName = requireText(name, "name");

    if (this.email.equals(nextEmail) && this.name.equals(nextName)) {
      return;
    }

    this.email = nextEmail;
    this.name = nextName;
    this.updatedAt = LocalDateTime.now();
  }

  public void withdraw() {
    ensureActive("Only active users can withdraw");
    this.status = UserStatus.WITHDRAWN;
    this.updatedAt = LocalDateTime.now();
  }

  public void ban() {
    ensureActive("Only active users can be banned");
    this.status = UserStatus.BANNED;
    this.updatedAt = LocalDateTime.now();
  }

  public boolean isActive() {
    return this.status == UserStatus.ACTIVE;
  }

  public boolean isAdmin() {
    return this.role == UserRole.ADMIN;
  }

  private void ensureActive(String message) {
    if (!isActive()) {
      throw new IllegalStateException(message);
    }
  }
}
