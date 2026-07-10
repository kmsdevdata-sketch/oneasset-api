package io.oneasset.domain.apikey.model;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.domain.apikey.vo.ApiKeyHash;
import io.oneasset.domain.apikey.vo.ApiKeyId;
import io.oneasset.domain.apikey.vo.ApiKeyPrefix;
import io.oneasset.domain.apikey.vo.ApiKeyStatus;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.ApiKeyErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public final class ApiKey {

  private final ApiKeyId id;
  private final ProjectId projectId;
  private final ApiKeyPrefix prefix;
  private final ApiKeyHash hash;
  private final LocalDateTime createdAt;

  private String name;
  private ApiKeyStatus status;
  private LocalDateTime lastUsedAt;
  private LocalDateTime revokedAt;

  private ApiKey(
      ApiKeyId id,
      ProjectId projectId,
      String name,
      ApiKeyPrefix prefix,
      ApiKeyHash hash,
      ApiKeyStatus status,
      LocalDateTime createdAt,
      LocalDateTime lastUsedAt,
      LocalDateTime revokedAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
    this.name = requireText(name, "name");
    this.prefix = Objects.requireNonNull(prefix, "prefix must not be null");
    this.hash = Objects.requireNonNull(hash, "hash must not be null");
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    this.lastUsedAt = lastUsedAt;
    this.revokedAt = revokedAt;

    validateTimestamps();
  }

  public static ApiKey create(
      ProjectId projectId, String name, ApiKeyPrefix prefix, ApiKeyHash hash) {
    return new ApiKey(
        ApiKeyId.newId(),
        projectId,
        name,
        prefix,
        hash,
        ApiKeyStatus.ACTIVE,
        LocalDateTime.now(),
        null,
        null);
  }

  public static ApiKey reconstitute(
      ApiKeyId id,
      ProjectId projectId,
      String name,
      ApiKeyPrefix prefix,
      ApiKeyHash hash,
      ApiKeyStatus status,
      LocalDateTime createdAt,
      LocalDateTime lastUsedAt,
      LocalDateTime revokedAt) {
    return new ApiKey(id, projectId, name, prefix, hash, status, createdAt, lastUsedAt, revokedAt);
  }

  public void rename(String name) {
    ensureActive();
    String nextName = requireText(name, "name");

    if (this.name.equals(nextName)) {
      return;
    }

    this.name = nextName;
  }

  public void markUsed() {
    ensureActive();
    this.lastUsedAt = LocalDateTime.now();
  }

  public void revoke() {
    ensureActive();
    this.status = ApiKeyStatus.REVOKED;
    this.revokedAt = LocalDateTime.now();
  }

  public boolean isActive() {
    return status == ApiKeyStatus.ACTIVE;
  }

  public boolean isRevoked() {
    return status == ApiKeyStatus.REVOKED;
  }

  private void ensureActive() {
    if (!isActive()) {
      throw new BaseException(ApiKeyErrorCode.REVOKED_API_KEY_CANNOT_BE_CHANGED);
    }
  }

  private void validateTimestamps() {
    if (lastUsedAt != null && lastUsedAt.isBefore(createdAt)) {
      throw new BaseException(
          ApiKeyErrorCode.INVALID_API_KEY_AUDIT_TIME, "lastUsedAt must not be before createdAt");
    }

    if (revokedAt != null && revokedAt.isBefore(createdAt)) {
      throw new BaseException(
          ApiKeyErrorCode.INVALID_API_KEY_AUDIT_TIME, "revokedAt must not be before createdAt");
    }

    if (status == ApiKeyStatus.ACTIVE && revokedAt != null) {
      throw new BaseException(
          ApiKeyErrorCode.INVALID_API_KEY_STATUS, "active API key must not have revokedAt");
    }

    if (status == ApiKeyStatus.REVOKED && revokedAt == null) {
      throw new BaseException(
          ApiKeyErrorCode.INVALID_API_KEY_STATUS, "revoked API key must have revokedAt");
    }
  }
}
