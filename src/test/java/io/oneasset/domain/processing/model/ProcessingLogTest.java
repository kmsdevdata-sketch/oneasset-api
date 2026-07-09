package io.oneasset.domain.processing.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.processing.vo.ProcessingLogId;
import io.oneasset.domain.processing.vo.ProcessingStatus;
import io.oneasset.domain.processing.vo.ProcessingStep;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ProcessingLogTest {

  @Test
  void createsProcessingLog() {
    AssetId assetId = AssetId.newId();

    ProcessingLog log =
        ProcessingLog.create(
            assetId, ProcessingStep.WEBP, ProcessingStatus.SUCCESS, "created webp");

    assertThat(log.getId()).isNotNull();
    assertThat(log.getAssetId()).isEqualTo(assetId);
    assertThat(log.getStep()).isEqualTo(ProcessingStep.WEBP);
    assertThat(log.getStatus()).isEqualTo(ProcessingStatus.SUCCESS);
    assertThat(log.getMessage()).isEqualTo("created webp");
    assertThat(log.isSuccess()).isTrue();
    assertThat(log.isFailed()).isFalse();
  }

  @Test
  void reconstitutesPersistedProcessingLog() {
    ProcessingLogId id = ProcessingLogId.newId();
    AssetId assetId = AssetId.newId();
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 12, 0);

    ProcessingLog log =
        ProcessingLog.reconstitute(
            id, assetId, ProcessingStep.THUMBNAIL, ProcessingStatus.FAILED, null, createdAt);

    assertThat(log.getId()).isEqualTo(id);
    assertThat(log.getAssetId()).isEqualTo(assetId);
    assertThat(log.getStep()).isEqualTo(ProcessingStep.THUMBNAIL);
    assertThat(log.getStatus()).isEqualTo(ProcessingStatus.FAILED);
    assertThat(log.getMessage()).isNull();
    assertThat(log.getCreatedAt()).isEqualTo(createdAt);
    assertThat(log.isSuccess()).isFalse();
    assertThat(log.isFailed()).isTrue();
  }
}
