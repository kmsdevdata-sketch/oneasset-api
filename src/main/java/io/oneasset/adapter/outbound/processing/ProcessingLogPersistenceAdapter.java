package io.oneasset.adapter.outbound.processing;

import io.oneasset.adapter.outbound.processing.entity.ProcessingLogEntity;
import io.oneasset.adapter.outbound.processing.persistence.ProcessingLogJpaRepository;
import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.processing.model.ProcessingLog;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class ProcessingLogPersistenceAdapter {

  private final ProcessingLogJpaRepository processingLogJpaRepository;

  public void save(ProcessingLog processingLog) {
    processingLogJpaRepository.save(ProcessingLogEntity.from(processingLog));
  }

  @Transactional(readOnly = true)
  public List<ProcessingLog> findAllByAssetId(AssetId assetId) {
    return processingLogJpaRepository.findAllByAssetIdOrderByCreatedAtAsc(assetId.value()).stream()
        .map(ProcessingLogEntity::toDomain)
        .toList();
  }
}
