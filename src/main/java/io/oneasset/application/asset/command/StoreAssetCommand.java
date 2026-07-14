package io.oneasset.application.asset.command;

import static io.oneasset.domain.common.DomainValidator.requireText;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.AssetErrorCode;
import java.io.InputStream;
import java.util.Objects;

public record StoreAssetCommand(
    InputStream inputStream, String storageKey, String contentType, long sizeBytes) {
  public StoreAssetCommand {
    inputStream = Objects.requireNonNull(inputStream, "inputStream must not be null");
    storageKey = requireText(storageKey, "storageKey");
    contentType = requireText(contentType, "contentType");
    if (sizeBytes <= 0) {
      throw new BaseException(AssetErrorCode.INVALID_ASSET_SIZE, "sizeBytes must be positive");
    }
  }

  public static StoreAssetCommand from(
      InputStream inputStream, String storageKey, String contentType, long sizeByte) {
    return new StoreAssetCommand(inputStream, storageKey, contentType, sizeByte);
  }
}
