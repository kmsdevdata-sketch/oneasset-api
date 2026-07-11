package io.oneasset.domain.apikey.engine;

public interface ApiKeyGenerator {

  GenerateApiKey generate();

  String hash(String rawKey);
}
