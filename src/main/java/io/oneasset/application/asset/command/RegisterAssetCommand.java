package io.oneasset.application.asset.command;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.AssetErrorCode;

public record RegisterAssetCommand(
    String projectId,
    String requestedKey,
    String requestedFileName,
    String multipartOriginalFileName,
    String contentType,
    long sizeBytes) {

  public RegisterAssetCommand {
    projectId = requireText(projectId, "projectId");
    contentType = requireText(contentType, "contentType");
    if (sizeBytes <= 0) {
      throw new BaseException(AssetErrorCode.INVALID_ASSET_SIZE, "sizeBytes must be positive");
    }
  }
}
