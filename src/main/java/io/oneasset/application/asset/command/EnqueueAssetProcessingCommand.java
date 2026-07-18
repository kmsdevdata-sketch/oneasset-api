package io.oneasset.application.asset.command;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.project.vo.ProjectId;

public record EnqueueAssetProcessingCommand(
    String assetId,
    String projectId,
    String bucket,
    String storageKey,
    String contentType,
    long sizeBytes) {
  public static EnqueueAssetProcessingCommand from(
      AssetId id,
      ProjectId projectId,
      String assetBucket,
      String storageKey,
      String contentType,
      long sizeBytes) {
    return new EnqueueAssetProcessingCommand(
        id.toString(), projectId.toString(), assetBucket, storageKey, contentType, sizeBytes);
  }
}
