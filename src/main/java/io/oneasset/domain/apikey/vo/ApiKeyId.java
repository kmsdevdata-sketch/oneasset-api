package io.oneasset.domain.apikey.vo;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
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
      throw new BaseException(CommonErrorCode.INVALID_ID, "ApiKeyId must not be blank");
    }
    try {
      return new ApiKeyId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new BaseException(CommonErrorCode.INVALID_ID, "ApiKeyId must be UUID");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
