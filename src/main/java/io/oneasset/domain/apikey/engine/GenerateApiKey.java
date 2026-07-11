package io.oneasset.domain.apikey.engine;

import static io.oneasset.domain.common.DomainValidator.requireText;

public record GenerateApiKey(String rawKey, String hash, String prefix) {

  public GenerateApiKey {
    rawKey = requireText(rawKey, "rawKey");
    hash = requireText(hash, "hash");
    prefix = requireText(prefix, "prefix");
  }

  public static GenerateApiKey from(String rawKey, String hash, String prefix) {
    return new GenerateApiKey(rawKey, hash, prefix);
  }
}
