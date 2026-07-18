package io.oneasset.application.asset.required;

import io.oneasset.application.asset.command.EnqueueAssetProcessingCommand;

public interface AssetProcessingQueuePort {

  void enqueue(EnqueueAssetProcessingCommand message);
}
