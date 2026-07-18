package io.oneasset.adapter.outbound.asset.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.oneasset.application.asset.command.EnqueueAssetProcessingCommand;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class SqsQueueTest {

  private final SqsClient sqsClient = mock(SqsClient.class);
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final SqsQueue sqsQueue = new SqsQueue(
      sqsClient,
      objectMapper,
      "https://sqs.ap-northeast-2.amazonaws.com/123456789012/oneasset-asset-processing-dev");

  @Test
  void sendsAssetProcessingCommandAsJsonMessage() throws Exception {
    EnqueueAssetProcessingCommand command = new EnqueueAssetProcessingCommand(
        "asset-id",
        "project-id",
        "test-bucket",
        "projects/project-id/users/123/profile.png",
        "image/png",
        1024);

    sqsQueue.enqueue(command);

    ArgumentCaptor<SendMessageRequest> requestCaptor =
        ArgumentCaptor.forClass(SendMessageRequest.class);
    verify(sqsClient).sendMessage(requestCaptor.capture());

    SendMessageRequest request = requestCaptor.getValue();
    assertThat(request.queueUrl())
        .isEqualTo(
            "https://sqs.ap-northeast-2.amazonaws.com/123456789012/oneasset-asset-processing-dev");

    JsonNode body = objectMapper.readTree(request.messageBody());
    assertThat(body.get("assetId").asText()).isEqualTo("asset-id");
    assertThat(body.get("projectId").asText()).isEqualTo("project-id");
    assertThat(body.get("bucket").asText()).isEqualTo("test-bucket");
    assertThat(body.get("storageKey").asText())
        .isEqualTo("projects/project-id/users/123/profile.png");
    assertThat(body.get("contentType").asText()).isEqualTo("image/png");
    assertThat(body.get("sizeBytes").asLong()).isEqualTo(1024);
  }
}
