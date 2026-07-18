package io.oneasset.adapter.outbound.asset.request;

import io.oneasset.application.asset.command.StoreAssetCommand;
import java.io.InputStream;

public record AssetStoreRequest(
    InputStream inputStream, String storageKey, String contentType, long sizeBytes) {
  public static AssetStoreRequest from(StoreAssetCommand command) {
    return new AssetStoreRequest(
        command.inputStream(), command.storageKey(), command.contentType(), command.sizeBytes());
  }
}
