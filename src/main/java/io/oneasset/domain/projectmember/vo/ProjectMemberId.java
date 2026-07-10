package io.oneasset.domain.projectmember.vo;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
import java.util.Objects;
import java.util.UUID;

public record ProjectMemberId(UUID value) {

  public ProjectMemberId {
    Objects.requireNonNull(value, "ProjectMemberId value must not be null");
  }

  public static ProjectMemberId newId() {
    return new ProjectMemberId(UUID.randomUUID());
  }

  public static ProjectMemberId of(UUID value) {
    return new ProjectMemberId(value);
  }

  public static ProjectMemberId fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "ProjectMemberId must not be blank");
    }
    try {
      return new ProjectMemberId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "ProjectMemberId must be UUID");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
