package io.oneasset.domain.user.model;

import io.oneasset.domain.user.vo.UserId;
import io.oneasset.domain.user.vo.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public final class User {

  private final UserId id;
  private final String cognitoSub;
  private final LocalDateTime createdAt;

  private String email;
  private String name;
  private UserStatus status;
  private LocalDateTime updatedAt;

  private User(
      UserId id,
      String cognitoSub,
      String email,
      String name,
      UserStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.cognitoSub = requireText(cognitoSub, "cognitoSub");
    this.email = requireText(email, "email");
    this.name = requireText(name, "name");
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");

    if (updatedAt.isBefore(createdAt)) {
      throw new IllegalArgumentException("updatedAt must not be before createdAt");
    }
  }

  public static User createFromCognito(String cognitoSub, String email, String name) {
    LocalDateTime now = LocalDateTime.now();
    return new User(UserId.newId(), cognitoSub, email, name, UserStatus.ACTIVE, now, now);
  }

  public static User reconstitute(
      UserId id,
      String cognitoSub,
      String email,
      String name,
      UserStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    return new User(id, cognitoSub, email, name, status, createdAt, updatedAt);
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

  private void ensureActive(String message) {
    if (!isActive()) {
      throw new IllegalStateException(message);
    }
  }

  private static String requireText(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return value;
  }
}
