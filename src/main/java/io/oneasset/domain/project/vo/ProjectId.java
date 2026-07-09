package io.oneasset.domain.project.vo;

import java.util.Objects;
import java.util.UUID;

public record ProjectId(UUID value) {

  public ProjectId {
    Objects.requireNonNull(value, "ProjectId value must not be null");
  }

  public static ProjectId newId() {
    return new ProjectId(UUID.randomUUID());
  }

  public static ProjectId of(UUID value) {
    return new ProjectId(value);
  }

  public static ProjectId fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("value must not be blank");
    }
    return new ProjectId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
