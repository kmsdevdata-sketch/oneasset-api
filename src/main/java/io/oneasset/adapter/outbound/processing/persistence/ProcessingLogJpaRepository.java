package io.oneasset.adapter.outbound.processing.persistence;

import io.oneasset.adapter.outbound.processing.entity.ProcessingLogEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingLogJpaRepository extends JpaRepository<ProcessingLogEntity, UUID> {

  List<ProcessingLogEntity> findAllByAssetIdOrderByCreatedAtAsc(UUID assetId);
}
