package io.oneasset.domain.common;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;

public final class DomainValidator {

  private DomainValidator() {}

  public static String requireText(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new BaseException(CommonErrorCode.INVALID_INPUT, fieldName + " must not be blank");
    }
    return value;
  }
}
