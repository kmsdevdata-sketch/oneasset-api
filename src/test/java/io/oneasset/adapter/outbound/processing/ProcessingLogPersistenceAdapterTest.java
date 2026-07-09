package io.oneasset.adapter.outbound.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.outbound.processing.entity.ProcessingLogEntity;
import io.oneasset.adapter.outbound.processing.persistence.ProcessingLogJpaRepository;
import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.processing.model.ProcessingLog;
import io.oneasset.domain.processing.vo.ProcessingStatus;
import io.oneasset.domain.processing.vo.ProcessingStep;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProcessingLogPersistenceAdapterTest {

  private final ProcessingLogJpaRepository processingLogJpaRepository =
      mock(ProcessingLogJpaRepository.class);
  private final ProcessingLogPersistenceAdapter adapter =
      new ProcessingLogPersistenceAdapter(processingLogJpaRepository);

  @Test
  void savesProcessingLogEntityConvertedFromDomain() {
    ProcessingLog log = createProcessingLog();

    adapter.save(log);

    verify(processingLogJpaRepository).save(any(ProcessingLogEntity.class));
  }

  @Test
  void findsAllProcessingLogsByAssetId() {
    ProcessingLog log = createProcessingLog();
    when(processingLogJpaRepository.findAllByAssetIdOrderByCreatedAtAsc(log.getAssetId().value()))
        .thenReturn(List.of(ProcessingLogEntity.from(log)));

    List<ProcessingLog> found = adapter.findAllByAssetId(log.getAssetId());

    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getAssetId()).isEqualTo(log.getAssetId());
    verify(processingLogJpaRepository)
        .findAllByAssetIdOrderByCreatedAtAsc(log.getAssetId().value());
  }

  private static ProcessingLog createProcessingLog() {
    return ProcessingLog.create(
        AssetId.newId(), ProcessingStep.WEBP, ProcessingStatus.SUCCESS, "webp created");
  }
}
