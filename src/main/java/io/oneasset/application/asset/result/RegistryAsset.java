package io.oneasset.application.asset.result;

import io.oneasset.domain.asset.model.Asset;
import java.time.LocalDateTime;

public record RegistryAsset(
    String id,
    String key,
    String originalFileName,
    String contentType,
    long sizeBytes,
    String status,
    String deliveryUrl,
    LocalDateTime createdAt) {

  public static RegistryAsset from(Asset asset) {
    return new RegistryAsset(
        asset.getId().toString(),
        asset.getStorageKey(),
        asset.getOriginalFileName(),
        asset.getContentType(),
        asset.getSizeBytes(),
        asset.getStatus().name(),
        null,
        asset.getCreatedAt());
  }
}
