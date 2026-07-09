package io.oneasset.domain.apikey.vo;

import java.util.Objects;
import java.util.UUID;

public record ApiKeyId(UUID value) {

  public ApiKeyId {
    Objects.requireNonNull(value, "ApiKeyId value must not be null");
  }

  public static ApiKeyId newId() {
    return new ApiKeyId(UUID.randomUUID());
  }

  public static ApiKeyId of(UUID value) {
    return new ApiKeyId(value);
  }

  public static ApiKeyId fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("value must not be blank");
    }
    return new ApiKeyId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
