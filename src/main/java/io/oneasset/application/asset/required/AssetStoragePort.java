package io.oneasset.application.asset.required;

import io.oneasset.application.asset.command.StoreAssetCommand;

public interface AssetStoragePort {

  void store(StoreAssetCommand command);
}
