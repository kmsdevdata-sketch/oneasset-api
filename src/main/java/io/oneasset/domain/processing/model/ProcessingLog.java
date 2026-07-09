package io.oneasset.domain.processing.model;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.processing.vo.ProcessingLogId;
import io.oneasset.domain.processing.vo.ProcessingStatus;
import io.oneasset.domain.processing.vo.ProcessingStep;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public final class ProcessingLog {

  private final ProcessingLogId id;
  private final AssetId assetId;
  private final ProcessingStep step;
  private final ProcessingStatus status;
  private final String message;
  private final LocalDateTime createdAt;

  private ProcessingLog(
      ProcessingLogId id,
      AssetId assetId,
      ProcessingStep step,
      ProcessingStatus status,
      String message,
      LocalDateTime createdAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.assetId = Objects.requireNonNull(assetId, "assetId must not be null");
    this.step = Objects.requireNonNull(step, "step must not be null");
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.message = message;
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
  }

  public static ProcessingLog create(
      AssetId assetId, ProcessingStep step, ProcessingStatus status, String message) {
    return new ProcessingLog(
        ProcessingLogId.newId(), assetId, step, status, message, LocalDateTime.now());
  }

  public static ProcessingLog reconstitute(
      ProcessingLogId id,
      AssetId assetId,
      ProcessingStep step,
      ProcessingStatus status,
      String message,
      LocalDateTime createdAt) {
    return new ProcessingLog(id, assetId, step, status, message, createdAt);
  }

  public boolean isSuccess() {
    return status == ProcessingStatus.SUCCESS;
  }

  public boolean isFailed() {
    return status == ProcessingStatus.FAILED;
  }
}
