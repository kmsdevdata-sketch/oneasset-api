package io.oneasset.domain.asset.vo;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
import java.util.Objects;
import java.util.UUID;

public record AssetId(UUID value) {

  public AssetId {
    Objects.requireNonNull(value, "AssetId value must not be null");
  }

  public static AssetId newId() {
    return new AssetId(UUID.randomUUID());
  }

  public static AssetId of(UUID value) {
    return new AssetId(value);
  }

  public static AssetId fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "AssetId must not be blank");
    }
    try {
      return new AssetId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "AssetId must be UUID");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
