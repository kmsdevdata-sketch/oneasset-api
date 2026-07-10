package io.oneasset.domain.asset.model;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.asset.vo.AssetStatus;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.vo.UserId;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.AssetErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public final class Asset {

  private final AssetId id;
  private final ProjectId projectId;
  private final UserId uploadedBy;
  private final String originalFileName;
  private final String contentType;
  private final long sizeBytes;
  private final String bucket;
  private final String storageKey;
  private final LocalDateTime createdAt;

  private AssetStatus status;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  private Asset(
      AssetId id,
      ProjectId projectId,
      UserId uploadedBy,
      String originalFileName,
      String contentType,
      long sizeBytes,
      String bucket,
      String storageKey,
      AssetStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
    this.uploadedBy = uploadedBy;
    this.originalFileName = requireText(originalFileName, "originalFileName");
    this.contentType = requireText(contentType, "contentType");
    this.sizeBytes = requirePositive(sizeBytes, "sizeBytes");
    this.bucket = requireText(bucket, "bucket");
    this.storageKey = requireText(storageKey, "storageKey");
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    this.deletedAt = deletedAt;

    validateTimestamps();
  }

  public static Asset create(
      ProjectId projectId,
      UserId uploadedBy,
      String originalFileName,
      String contentType,
      long sizeBytes,
      String bucket,
      String storageKey) {
    LocalDateTime now = LocalDateTime.now();
    return new Asset(
        AssetId.newId(),
        projectId,
        uploadedBy,
        originalFileName,
        contentType,
        sizeBytes,
        bucket,
        storageKey,
        AssetStatus.UPLOADED,
        now,
        now,
        null);
  }

  public static Asset reconstitute(
      AssetId id,
      ProjectId projectId,
      UserId uploadedBy,
      String originalFileName,
      String contentType,
      long sizeBytes,
      String bucket,
      String storageKey,
      AssetStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    return new Asset(
        id,
        projectId,
        uploadedBy,
        originalFileName,
        contentType,
        sizeBytes,
        bucket,
        storageKey,
        status,
        createdAt,
        updatedAt,
        deletedAt);
  }

  public void markProcessing() {
    ensureNotDeleted();
    if (status != AssetStatus.UPLOADED) {
      throw new BaseException(
          AssetErrorCode.INVALID_ASSET_STATUS_TRANSITION,
          "Only uploaded assets can start processing");
    }
    this.status = AssetStatus.PROCESSING;
    this.updatedAt = LocalDateTime.now();
  }

  public void markReady() {
    ensureNotDeleted();
    if (status != AssetStatus.PROCESSING) {
      throw new BaseException(
          AssetErrorCode.INVALID_ASSET_STATUS_TRANSITION,
          "Only processing assets can be marked ready");
    }
    this.status = AssetStatus.READY;
    this.updatedAt = LocalDateTime.now();
  }

  public void markFailed() {
    ensureNotDeleted();
    if (status == AssetStatus.READY) {
      throw new BaseException(
          AssetErrorCode.INVALID_ASSET_STATUS_TRANSITION, "Ready asset cannot be marked failed");
    }
    this.status = AssetStatus.FAILED;
    this.updatedAt = LocalDateTime.now();
  }

  public void delete() {
    ensureNotDeleted();
    LocalDateTime now = LocalDateTime.now();
    this.deletedAt = now;
    this.updatedAt = now;
  }

  public boolean isReady() {
    return status == AssetStatus.READY;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

  private void ensureNotDeleted() {
    if (isDeleted()) {
      throw new BaseException(AssetErrorCode.DELETED_ASSET_CANNOT_BE_CHANGED);
    }
  }

  private void validateTimestamps() {
    if (updatedAt.isBefore(createdAt)) {
      throw new BaseException(
          AssetErrorCode.INVALID_ASSET_AUDIT_TIME, "updatedAt must not be before createdAt");
    }

    if (deletedAt != null && deletedAt.isBefore(createdAt)) {
      throw new BaseException(
          AssetErrorCode.INVALID_ASSET_AUDIT_TIME, "deletedAt must not be before createdAt");
    }
  }

  private static long requirePositive(long value, String fieldName) {
    if (value <= 0) {
      throw new BaseException(AssetErrorCode.INVALID_ASSET_SIZE, fieldName + " must be positive");
    }
    return value;
  }
}
