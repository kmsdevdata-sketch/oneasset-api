package io.oneasset.application.apikey.required;

import io.oneasset.domain.apikey.model.ApiKey;

public interface ApiKeyPersistencePort {

  ApiKey save(ApiKey apiKey);
}
