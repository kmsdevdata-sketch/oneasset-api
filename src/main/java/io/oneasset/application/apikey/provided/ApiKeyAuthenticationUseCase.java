package io.oneasset.application.apikey.provided;

import io.oneasset.application.apikey.result.AuthenticatedApiKey;

public interface ApiKeyAuthenticationUseCase {

  AuthenticatedApiKey authenticate(String rawKey);
}
