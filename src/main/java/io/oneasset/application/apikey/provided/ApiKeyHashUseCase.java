package io.oneasset.application.apikey.provided;

import io.oneasset.domain.apikey.vo.ApiKeyHash;

public interface ApiKeyHashUseCase {

    ApiKeyHash createHash(String rawKey,String serverSecret);
}
