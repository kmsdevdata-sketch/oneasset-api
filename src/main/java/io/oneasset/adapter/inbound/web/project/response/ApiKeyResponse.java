package io.oneasset.adapter.inbound.web.project.response;

import io.oneasset.application.apikey.result.CreatedApiKey;
import io.oneasset.domain.apikey.model.ApiKey;
import java.time.LocalDateTime;

public record ApiKeyResponse(
    String id,
    String name,
    String apiKey,
    String prefix,
    String status,
    LocalDateTime createdAt,
    LocalDateTime lastUsedAt) {

  public static ApiKeyResponse fromCreated(CreatedApiKey createdApiKey) {
    return from(createdApiKey.apiKey(), createdApiKey.rawKey());
  }

  public static ApiKeyResponse from(ApiKey apiKey) {
    return from(apiKey, null);
  }

  private static ApiKeyResponse from(ApiKey apiKey, String rawKey) {
    return new ApiKeyResponse(
        apiKey.getId().toString(),
        apiKey.getName(),
        rawKey,
        apiKey.getPrefix().value(),
        apiKey.getStatus().name(),
        apiKey.getCreatedAt(),
        apiKey.getLastUsedAt());
  }
}
