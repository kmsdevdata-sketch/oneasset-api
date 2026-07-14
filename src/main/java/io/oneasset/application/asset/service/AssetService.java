package io.oneasset.application.asset.service;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.application.asset.command.RegisterAssetCommand;
import io.oneasset.application.asset.command.StoreAssetCommand;
import io.oneasset.application.asset.provided.AssetRegisterUseCase;
import io.oneasset.application.asset.required.AssetPersistencePort;
import io.oneasset.application.asset.required.AssetStoragePort;
import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetService implements AssetRegisterUseCase {

  private final AssetPersistencePort assetPersistencePort;
  private final AssetStoragePort assetStoragePort;
  private final String assetBucket;

  public AssetService(
      AssetPersistencePort assetPersistencePort,
      AssetStoragePort assetStoragePort,
      @Value("${oneasset.storage.asset-bucket:local-oneasset-assets}") String assetBucket) {
    this.assetPersistencePort = assetPersistencePort;
    this.assetStoragePort = assetStoragePort;
    this.assetBucket = requireText(assetBucket, "assetBucket");
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

    return RegistryAsset.from(registryAsset);
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
      return "projects/" + projectId + "/" + normalizedRequestedKey;
    }

    return "projects/" + projectId + "/assets/" + UUID.randomUUID() + extensionOf(originalFileName);
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
}
