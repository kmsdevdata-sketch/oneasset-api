package io.oneasset.adapter.outbound.assetvariant.entity;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.assetvariant.model.AssetVariant;
import io.oneasset.domain.assetvariant.vo.AssetVariantId;
import io.oneasset.domain.assetvariant.vo.AssetVariantType;
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
@Table(name = "asset_variants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetVariantEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "asset_id", nullable = false)
  private UUID assetId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private AssetVariantType type;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private long sizeBytes;

  @Column(name = "bucket", nullable = false)
  private String bucket;

  @Column(name = "storage_key", nullable = false)
  private String storageKey;

  @Column(name = "width")
  private Integer width;

  @Column(name = "height")
  private Integer height;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  private AssetVariantEntity(
      UUID id,
      UUID assetId,
      AssetVariantType type,
      String contentType,
      long sizeBytes,
      String bucket,
      String storageKey,
      Integer width,
      Integer height,
      LocalDateTime createdAt) {
    this.id = id;
    this.assetId = assetId;
    this.type = type;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    this.bucket = bucket;
    this.storageKey = storageKey;
    this.width = width;
    this.height = height;
    this.createdAt = createdAt;
  }

  public static AssetVariantEntity from(AssetVariant assetVariant) {
    return new AssetVariantEntity(
        assetVariant.getId().value(),
        assetVariant.getAssetId().value(),
        assetVariant.getType(),
        assetVariant.getContentType(),
        assetVariant.getSizeBytes(),
        assetVariant.getBucket(),
        assetVariant.getStorageKey(),
        assetVariant.getWidth(),
        assetVariant.getHeight(),
        assetVariant.getCreatedAt());
  }

  public AssetVariant toDomain() {
    return AssetVariant.reconstitute(
        AssetVariantId.of(id),
        AssetId.of(assetId),
        type,
        contentType,
        sizeBytes,
        bucket,
        storageKey,
        width,
        height,
        createdAt);
  }
}
