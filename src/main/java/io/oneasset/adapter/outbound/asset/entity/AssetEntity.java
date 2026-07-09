package io.oneasset.adapter.outbound.asset.entity;

import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.asset.vo.AssetStatus;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.vo.UserId;
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
@Table(name = "assets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "project_id", nullable = false)
  private UUID projectId;

  @Column(name = "uploaded_by")
  private UUID uploadedBy;

  @Column(name = "original_file_name", nullable = false)
  private String originalFileName;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private long sizeBytes;

  @Column(name = "bucket", nullable = false)
  private String bucket;

  @Column(name = "storage_key", nullable = false)
  private String storageKey;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private AssetStatus status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  private AssetEntity(
      UUID id,
      UUID projectId,
      UUID uploadedBy,
      String originalFileName,
      String contentType,
      long sizeBytes,
      String bucket,
      String storageKey,
      AssetStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt) {
    this.id = id;
    this.projectId = projectId;
    this.uploadedBy = uploadedBy;
    this.originalFileName = originalFileName;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    this.bucket = bucket;
    this.storageKey = storageKey;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
  }

  public static AssetEntity from(Asset asset) {
    return new AssetEntity(
        asset.getId().value(),
        asset.getProjectId().value(),
        asset.getUploadedBy() == null ? null : asset.getUploadedBy().value(),
        asset.getOriginalFileName(),
        asset.getContentType(),
        asset.getSizeBytes(),
        asset.getBucket(),
        asset.getStorageKey(),
        asset.getStatus(),
        asset.getCreatedAt(),
        asset.getUpdatedAt(),
        asset.getDeletedAt());
  }

  public Asset toDomain() {
    return Asset.reconstitute(
        AssetId.of(id),
        ProjectId.of(projectId),
        uploadedBy == null ? null : UserId.of(uploadedBy),
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
}
