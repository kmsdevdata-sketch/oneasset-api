package io.oneasset.adapter.inbound.web.project.response;

import io.oneasset.domain.apikey.model.ApiKey;

import java.time.LocalDateTime;

public record ApiKeyResponse(
    String id,
    String name,
    String apiKey,
    String prefix,
    LocalDateTime createdAt
) {

    public static ApiKeyResponse from(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getPrefix(),
                apiKey.getCreatedAt()
        );
    }
}
