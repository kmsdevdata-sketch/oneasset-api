package io.oneasset.application.apikey.result;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.domain.apikey.model.ApiKey;
import java.util.Objects;

public record CreatedApiKey(ApiKey apiKey, String rawKey) {

  public CreatedApiKey {
    apiKey = Objects.requireNonNull(apiKey, "apiKey must not be null");
    rawKey = requireText(rawKey, "rawKey");
  }
}
