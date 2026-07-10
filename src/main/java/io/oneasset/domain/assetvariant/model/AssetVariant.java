package io.oneasset.domain.assetvariant.model;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.assetvariant.vo.AssetVariantId;
import io.oneasset.domain.assetvariant.vo.AssetVariantType;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.AssetVariantErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public final class AssetVariant {

  private final AssetVariantId id;
  private final AssetId assetId;
  private final AssetVariantType type;
  private final String contentType;
  private final long sizeBytes;
  private final String bucket;
  private final String storageKey;
  private final Integer width;
  private final Integer height;
  private final LocalDateTime createdAt;

  private AssetVariant(
      AssetVariantId id,
      AssetId assetId,
      AssetVariantType type,
      String contentType,
      long sizeBytes,
      String bucket,
      String storageKey,
      Integer width,
      Integer height,
      LocalDateTime createdAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.assetId = Objects.requireNonNull(assetId, "assetId must not be null");
    this.type = Objects.requireNonNull(type, "type must not be null");
    this.contentType = requireText(contentType, "contentType");
    this.sizeBytes = requirePositive(sizeBytes, "sizeBytes");
    this.bucket = requireText(bucket, "bucket");
    this.storageKey = requireText(storageKey, "storageKey");
    this.width = requirePositiveIfPresent(width, "width");
    this.height = requirePositiveIfPresent(height, "height");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
  }

  public static AssetVariant create(
      AssetId assetId,
      AssetVariantType type,
      String contentType,
      long sizeBytes,
      String bucket,
      String storageKey,
      Integer width,
      Integer height) {
    return new AssetVariant(
        AssetVariantId.newId(),
        assetId,
        type,
        contentType,
        sizeBytes,
        bucket,
        storageKey,
        width,
        height,
        LocalDateTime.now());
  }

  public static AssetVariant reconstitute(
      AssetVariantId id,
      AssetId assetId,
      AssetVariantType type,
      String contentType,
      long sizeBytes,
      String bucket,
      String storageKey,
      Integer width,
      Integer height,
      LocalDateTime createdAt) {
    return new AssetVariant(
        id, assetId, type, contentType, sizeBytes, bucket, storageKey, width, height, createdAt);
  }

  private static long requirePositive(long value, String fieldName) {
    if (value <= 0) {
      throw new BaseException(
          AssetVariantErrorCode.INVALID_ASSET_VARIANT_SIZE, fieldName + " must be positive");
    }
    return value;
  }

  private static Integer requirePositiveIfPresent(Integer value, String fieldName) {
    if (value != null && value <= 0) {
      throw new BaseException(
          AssetVariantErrorCode.INVALID_ASSET_VARIANT_SIZE, fieldName + " must be positive");
    }
    return value;
  }
}
