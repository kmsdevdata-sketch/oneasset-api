package io.oneasset.adapter.outbound.apikey.entity;

import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.apikey.vo.ApiKeyHash;
import io.oneasset.domain.apikey.vo.ApiKeyId;
import io.oneasset.domain.apikey.vo.ApiKeyPrefix;
import io.oneasset.domain.apikey.vo.ApiKeyStatus;
import io.oneasset.domain.project.vo.ProjectId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "api_keys")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiKeyEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "project_id", nullable = false)
  private UUID projectId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "key_prefix", nullable = false)
  private String keyPrefix;

  @Column(name = "key_hash", nullable = false)
  private String keyHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ApiKeyStatus status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_used_at")
  private LocalDateTime lastUsedAt;

  @Column(name = "revoked_at")
  private LocalDateTime revokedAt;

  private ApiKeyEntity(
      UUID id,
      UUID projectId,
      String name,
      String keyPrefix,
      String keyHash,
      ApiKeyStatus status,
      LocalDateTime createdAt,
      LocalDateTime lastUsedAt,
      LocalDateTime revokedAt) {
    this.id = id;
    this.projectId = projectId;
    this.name = name;
    this.keyPrefix = keyPrefix;
    this.keyHash = keyHash;
    this.status = status;
    this.createdAt = createdAt;
    this.lastUsedAt = lastUsedAt;
    this.revokedAt = revokedAt;
  }

  public static ApiKeyEntity from(ApiKey apiKey) {
    return new ApiKeyEntity(
        apiKey.getId().value(),
        apiKey.getProjectId().value(),
        apiKey.getName(),
        apiKey.getPrefix().value(),
        apiKey.getHash().value(),
        apiKey.getStatus(),
        apiKey.getCreatedAt(),
        apiKey.getLastUsedAt(),
        apiKey.getRevokedAt());
  }

  public ApiKey toDomain() {
    return ApiKey.reconstitute(
        ApiKeyId.of(id),
        ProjectId.of(projectId),
        name,
        ApiKeyPrefix.of(keyPrefix),
        ApiKeyHash.of(keyHash),
        status,
        createdAt,
        lastUsedAt,
        revokedAt);
  }
}
