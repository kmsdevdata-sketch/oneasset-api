package io.oneasset.application.asset.provided;

import io.oneasset.application.asset.command.RegisterAssetCommand;
import io.oneasset.application.asset.result.RegistryAsset;

public interface AssetRegisterUseCase {

  RegistryAsset register(RegisterAssetCommand command);
}
