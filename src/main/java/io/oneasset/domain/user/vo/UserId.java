package io.oneasset.domain.user.vo;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
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
      throw new BaseException(CommonErrorCode.INVALID_ID, "UserId must not be blank");
    }
    try {
      return new UserId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "UserId must be UUID");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
