package io.oneasset.adapter.outbound.asset;

import io.oneasset.adapter.outbound.asset.storage.S3Storage;
import io.oneasset.application.asset.command.StoreAssetCommand;
import io.oneasset.application.asset.required.AssetStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class AssetStorageAdapter implements AssetStoragePort {

  private final S3Storage s3Storage;

  @Override
  public void store(StoreAssetCommand command) {
    s3Storage.store(AssetStoreRequest.from(command));
  }
}
