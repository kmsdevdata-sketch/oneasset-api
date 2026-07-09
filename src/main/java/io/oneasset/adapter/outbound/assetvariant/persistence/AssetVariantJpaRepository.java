package io.oneasset.adapter.outbound.assetvariant.persistence;

import io.oneasset.adapter.outbound.assetvariant.entity.AssetVariantEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetVariantJpaRepository extends JpaRepository<AssetVariantEntity, UUID> {

  List<AssetVariantEntity> findAllByAssetId(UUID assetId);

  Optional<AssetVariantEntity> findByStorageKey(String storageKey);
}
