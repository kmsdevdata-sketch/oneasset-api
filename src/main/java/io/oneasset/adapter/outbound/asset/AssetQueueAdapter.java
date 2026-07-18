package io.oneasset.adapter.outbound.asset;

import io.oneasset.adapter.outbound.asset.queue.SqsQueue;
import io.oneasset.application.asset.command.EnqueueAssetProcessingCommand;
import io.oneasset.application.asset.required.AssetProcessingQueuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetQueueAdapter implements AssetProcessingQueuePort {

  private final SqsQueue sqsQueue;

  @Override
  public void enqueue(EnqueueAssetProcessingCommand command) {
    sqsQueue.enqueue(command);
  }
}
