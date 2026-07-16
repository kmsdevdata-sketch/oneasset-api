package io.oneasset.application.asset.result;

import io.oneasset.domain.asset.model.Asset;
import java.time.LocalDateTime;

public record RegistryAsset(
    String id,
    String key,
    String storageKey,
    String originalFileName,
    String contentType,
    long sizeBytes,
    String status,
    String deliveryUrl,
    LocalDateTime createdAt) {

  public static RegistryAsset from(Asset asset) {
    return from(asset, null);
  }

  public static RegistryAsset from(Asset asset, String deliveryUrl) {
    return new RegistryAsset(
        asset.getId().toString(),
        toUserKey(asset.getStorageKey()),
        asset.getStorageKey(),
        asset.getOriginalFileName(),
        asset.getContentType(),
        asset.getSizeBytes(),
        asset.getStatus().name(),
        deliveryUrl,
        asset.getCreatedAt());
  }

  private static String toUserKey(String storageKey) {
    String projectPrefix = "projects/";
    if (!storageKey.startsWith(projectPrefix)) {
      return storageKey;
    }

    int userKeyStart = storageKey.indexOf('/', projectPrefix.length());
    if (userKeyStart < 0 || userKeyStart == storageKey.length() - 1) {
      return storageKey;
    }

    return storageKey.substring(userKeyStart + 1);
  }
}
