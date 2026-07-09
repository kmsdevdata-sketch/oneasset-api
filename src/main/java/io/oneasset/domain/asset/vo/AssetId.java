package io.oneasset.domain.asset.vo;

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
      throw new IllegalArgumentException("value must not be blank");
    }
    return new AssetId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
