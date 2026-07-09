package io.oneasset.domain.common;

public final class DomainValidator {

  private DomainValidator() {}

  public static String requireText(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return value;
  }
}
