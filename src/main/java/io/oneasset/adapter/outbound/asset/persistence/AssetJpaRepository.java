package io.oneasset.adapter.outbound.asset.persistence;

import io.oneasset.adapter.outbound.asset.entity.AssetEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetJpaRepository extends JpaRepository<AssetEntity, UUID> {

  Optional<AssetEntity> findByIdAndDeletedAtIsNull(UUID id);

  Optional<AssetEntity> findByStorageKeyAndDeletedAtIsNull(String storageKey);

  List<AssetEntity> findAllByProjectIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID projectId);

  Optional<AssetEntity> findByStorageKeyAndProjectIdAndDeletedAtIsNull(
      String storageKey, UUID projectId);
}
