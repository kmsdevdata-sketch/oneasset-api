package io.oneasset.domain.processing.vo;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
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
      throw new BaseException(CommonErrorCode.INVALID_ID, "ProcessingLogId must not be blank");
    }
    try {
      return new ProcessingLogId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "ProcessingLogId must be UUID");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
