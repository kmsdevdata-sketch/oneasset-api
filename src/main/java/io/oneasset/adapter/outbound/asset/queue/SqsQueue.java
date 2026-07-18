package io.oneasset.adapter.outbound.asset.queue;

import io.oneasset.application.asset.command.EnqueueAssetProcessingCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class SqsQueue {

  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;
  private final String queueUrl;

  public SqsQueue(
      SqsClient sqsClient,
      ObjectMapper objectMapper,
      @Value("${oneasset.queue.asset-processing-url}") String queueUrl) {
    this.sqsClient = sqsClient;
    this.objectMapper = objectMapper;
    this.queueUrl = queueUrl;
  }

  public void enqueue(EnqueueAssetProcessingCommand command) {
    SendMessageRequest message = SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody(serializeJson(command))
        .build();

    sqsClient.sendMessage(message);
  }

  private String serializeJson(EnqueueAssetProcessingCommand command) {
    try {
      return objectMapper.writeValueAsString(command);
    } catch (JacksonException e) {
      throw new IllegalStateException("Failed to serialize asset processing message", e);
    }
  }
}
