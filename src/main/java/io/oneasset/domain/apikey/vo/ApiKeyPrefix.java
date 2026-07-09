package io.oneasset.domain.apikey.vo;

import static io.oneasset.domain.common.DomainValidator.requireText;

public record ApiKeyPrefix(String value) {

  public ApiKeyPrefix {
    value = requireText(value, "apiKeyPrefix");
  }

  public static ApiKeyPrefix of(String value) {
    return new ApiKeyPrefix(value);
  }
}
