package io.oneasset.adapter.inbound.v1.developer.response;

import io.oneasset.application.asset.result.RegistryAsset;
import java.time.LocalDateTime;

public record AssetResponse(
    String assetId,
    String key,
    String originalFileName,
    String contentType,
    long sizeBytes,
    String status,
    String deliveryUrl,
    LocalDateTime createdAt) {
  public static AssetResponse from(RegistryAsset asset) {
    return new AssetResponse(
        asset.id(),
        asset.key(),
        asset.originalFileName(),
        asset.contentType(),
        asset.sizeBytes(),
        asset.status(),
        asset.deliveryUrl(),
        asset.createdAt());
  }
}
