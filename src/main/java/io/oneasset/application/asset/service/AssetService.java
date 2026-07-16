package io.oneasset.application.asset.service;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.application.asset.command.RegisterAssetCommand;
import io.oneasset.application.asset.command.StoreAssetCommand;
import io.oneasset.application.asset.provided.AssetRegisterUseCase;
import io.oneasset.application.asset.provided.AssetUseCase;
import io.oneasset.application.asset.required.AssetPersistencePort;
import io.oneasset.application.asset.required.AssetStoragePort;
import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.AssetErrorCode;
import io.oneasset.exception.code.CommonErrorCode;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetService implements AssetRegisterUseCase, AssetUseCase {

  private final AssetPersistencePort assetPersistencePort;
  private final AssetStoragePort assetStoragePort;
  private final String assetBucket;
  private final String deliveryBaseUrl;

  public AssetService(
      AssetPersistencePort assetPersistencePort,
      AssetStoragePort assetStoragePort,
      @Value("${oneasset.storage.asset-bucket:local-oneasset-assets}") String assetBucket,
      @Value("${oneasset.storage.delivery-base-url:}") String deliveryBaseUrl) {
    this.assetPersistencePort = assetPersistencePort;
    this.assetStoragePort = assetStoragePort;
    this.assetBucket = requireText(assetBucket, "assetBucket");
    this.deliveryBaseUrl = normalizeDeliveryBaseUrl(deliveryBaseUrl);
  }

  @Override
  @Transactional
  public RegistryAsset register(RegisterAssetCommand command) {
    ProjectId projectId = ProjectId.fromString(command.projectId());
    String originalFileName = resolveOriginalFileName(command);
    String storageKey = resolveStorageKey(projectId, command.requestedKey(), originalFileName);

    assetStoragePort.store(StoreAssetCommand.from(
        command.inputStream(), storageKey, command.contentType(), command.sizeBytes()));

    Asset registryAsset = assetPersistencePort.register(Asset.create(
        projectId,
        null,
        originalFileName,
        command.contentType(),
        command.sizeBytes(),
        assetBucket,
        storageKey));

    return RegistryAsset.from(registryAsset, resolveDeliveryUrl(storageKey));
  }

  @Override
  public RegistryAsset findByKeyAndProjectId(String key, ProjectId projectId) {
    String storageKey = resolveProjectScopedStorageKey(projectId, key);
    Asset findAsset = assetPersistencePort
        .findActiveByStorageKeyAndProjectId(storageKey, projectId)
        .orElseThrow(() -> new BaseException(AssetErrorCode.ASSET_NOT_FOUND));

    return RegistryAsset.from(findAsset, resolveDeliveryUrl(findAsset.getStorageKey()));
  }

  private String resolveOriginalFileName(RegisterAssetCommand command) {
    String fileName = firstText(command.requestedFileName(), command.multipartOriginalFileName());
    if (fileName == null) {
      return "asset-" + UUID.randomUUID();
    }

    return sanitizeFileName(fileName);
  }

  private String resolveStorageKey(
      ProjectId projectId, String requestedKey, String originalFileName) {
    String normalizedRequestedKey = normalizeKey(requestedKey);
    if (normalizedRequestedKey != null) {
      return resolveProjectScopedStorageKey(projectId, normalizedRequestedKey);
    }

    return "projects/" + projectId + "/assets/" + UUID.randomUUID() + extensionOf(originalFileName);
  }

  private String resolveProjectScopedStorageKey(ProjectId projectId, String key) {
    String normalizedKey = normalizeKey(key);
    if (normalizedKey == null) {
      throw new BaseException(CommonErrorCode.INVALID_INPUT, "key must not be blank");
    }
    return "projects/" + projectId + "/" + normalizedKey;
  }

  private String firstText(String first, String second) {
    if (first != null && !first.isBlank()) {
      return first;
    }
    if (second != null && !second.isBlank()) {
      return second;
    }
    return null;
  }

  private String sanitizeFileName(String fileName) {
    String normalized = fileName.replace('\\', '/');
    int lastSeparator = normalized.lastIndexOf('/');
    String baseName = lastSeparator >= 0 ? normalized.substring(lastSeparator + 1) : normalized;
    baseName = baseName.trim();
    return baseName.isBlank() ? "asset-" + UUID.randomUUID() : baseName;
  }

  private String normalizeKey(String key) {
    if (key == null || key.isBlank()) {
      return null;
    }

    String normalized = key.trim().replace('\\', '/');
    while (normalized.startsWith("/")) {
      normalized = normalized.substring(1);
    }

    for (String segment : normalized.split("/")) {
      if (segment.isBlank() || ".".equals(segment) || "..".equals(segment)) {
        throw new BaseException(CommonErrorCode.INVALID_INPUT, "key contains invalid path segment");
      }
    }

    return normalized.isBlank() ? null : normalized;
  }

  private String extensionOf(String fileName) {
    int lastDot = fileName.lastIndexOf('.');
    if (lastDot < 0 || lastDot == fileName.length() - 1) {
      return "";
    }
    return fileName.substring(lastDot);
  }

  private String normalizeDeliveryBaseUrl(String baseUrl) {
    if (baseUrl == null || baseUrl.isBlank()) {
      return null;
    }

    String normalized = baseUrl.trim();
    while (normalized.endsWith("/")) {
      normalized = normalized.substring(0, normalized.length() - 1);
    }
    return normalized.isBlank() ? null : normalized;
  }

  private String resolveDeliveryUrl(String storageKey) {
    if (deliveryBaseUrl == null) {
      return null;
    }
    return deliveryBaseUrl + "/" + storageKey;
  }
}
