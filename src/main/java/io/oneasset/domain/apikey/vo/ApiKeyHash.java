package io.oneasset.domain.apikey.vo;

import static io.oneasset.domain.common.DomainValidator.requireText;

public record ApiKeyHash(String value) {

  public ApiKeyHash {
    value = requireText(value, "apiKeyHash");
  }

  public static ApiKeyHash of(String value) {
    return new ApiKeyHash(value);
  }
}
