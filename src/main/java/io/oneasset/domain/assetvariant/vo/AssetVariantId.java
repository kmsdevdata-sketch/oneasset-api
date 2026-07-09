package io.oneasset.domain.assetvariant.vo;

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
      throw new IllegalArgumentException("value must not be blank");
    }
    return new AssetVariantId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
