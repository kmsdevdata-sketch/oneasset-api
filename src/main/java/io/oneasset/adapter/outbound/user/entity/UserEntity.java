package io.oneasset.adapter.outbound.user.entity;

import io.oneasset.domain.user.model.User;
import io.oneasset.domain.user.vo.UserId;
import io.oneasset.domain.user.vo.UserRole;
import io.oneasset.domain.user.vo.UserStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "cognito_sub", nullable = false)
  private String cognitoSub;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private UserRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private UserStatus status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  private UserEntity(
      UUID id,
      String cognitoSub,
      String email,
      String name,
      UserRole role,
      UserStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.cognitoSub = cognitoSub;
    this.email = email;
    this.name = name;
    this.role = role;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static UserEntity from(User user) {
    return new UserEntity(
        user.getId().value(),
        user.getCognitoSub(),
        user.getEmail(),
        user.getName(),
        user.getRole(),
        user.getStatus(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  public User toDomain() {
    return User.reconstitute(
        UserId.of(id), cognitoSub, email, name, role, status, createdAt, updatedAt);
  }
}
