package io.oneasset.domain.assetvariant.vo;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
import java.util.Objects;
import java.util.UUID;

public record AssetVariantId(UUID value) {

  public AssetVariantId {
    Objects.requireNonNull(value, "AssetVariantId value must not be null");
  }

  public static AssetVariantId newId() {
    return new AssetVariantId(UUID.randomUUID());
  }

  public static AssetVariantId of(UUID value) {
    return new AssetVariantId(value);
  }

  public static AssetVariantId fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "AssetVariantId must not be blank");
    }
    try {
      return new AssetVariantId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "AssetVariantId must be UUID");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
