package io.oneasset.domain.processing.vo;

import java.util.Objects;
import java.util.UUID;

public record ProcessingLogId(UUID value) {

  public ProcessingLogId {
    Objects.requireNonNull(value, "ProcessingLogId value must not be null");
  }

  public static ProcessingLogId newId() {
    return new ProcessingLogId(UUID.randomUUID());
  }

  public static ProcessingLogId of(UUID value) {
    return new ProcessingLogId(value);
  }

  public static ProcessingLogId fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("value must not be blank");
    }
    return new ProcessingLogId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
