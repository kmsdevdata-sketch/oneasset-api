package io.oneasset.application.asset.provided;

import io.oneasset.application.asset.command.RegisterAssetVariantCommand;

public interface AssetProcessingCallbackUseCase {

  void completeVariantProcessing(String assetId, RegisterAssetVariantCommand command);
}
