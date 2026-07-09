package io.oneasset.domain.user.vo;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {

  public UserId {
    Objects.requireNonNull(value, "value must not be null");
  }

  public static UserId newId() {
    return new UserId(UUID.randomUUID());
  }

  public static UserId of(UUID value) {
    return new UserId(value);
  }

  public static UserId fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("value must not be blank");
    }
    return new UserId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
