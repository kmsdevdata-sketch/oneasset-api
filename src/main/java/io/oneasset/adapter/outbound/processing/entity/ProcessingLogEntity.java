package io.oneasset.adapter.outbound.processing.entity;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.processing.model.ProcessingLog;
import io.oneasset.domain.processing.vo.ProcessingLogId;
import io.oneasset.domain.processing.vo.ProcessingStatus;
import io.oneasset.domain.processing.vo.ProcessingStep;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processing_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessingLogEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "asset_id", nullable = false)
  private UUID assetId;

  @Enumerated(EnumType.STRING)
  @Column(name = "step", nullable = false)
  private ProcessingStep step;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ProcessingStatus status;

  @Column(name = "message")
  private String message;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  private ProcessingLogEntity(
      UUID id,
      UUID assetId,
      ProcessingStep step,
      ProcessingStatus status,
      String message,
      LocalDateTime createdAt) {
    this.id = id;
    this.assetId = assetId;
    this.step = step;
    this.status = status;
    this.message = message;
    this.createdAt = createdAt;
  }

  public static ProcessingLogEntity from(ProcessingLog processingLog) {
    return new ProcessingLogEntity(
        processingLog.getId().value(),
        processingLog.getAssetId().value(),
        processingLog.getStep(),
        processingLog.getStatus(),
        processingLog.getMessage(),
        processingLog.getCreatedAt());
  }

  public ProcessingLog toDomain() {
    return ProcessingLog.reconstitute(
        ProcessingLogId.of(id), AssetId.of(assetId), step, status, message, createdAt);
  }
}
