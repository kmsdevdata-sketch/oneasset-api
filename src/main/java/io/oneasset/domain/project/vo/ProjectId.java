package io.oneasset.domain.project.vo;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
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
      throw new BaseException(CommonErrorCode.INVALID_ID, "ProjectId must not be blank");
    }
    try {
      return new ProjectId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "ProjectId must be UUID");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
