package io.oneasset.adapter.outbound.assetvariant;

import io.oneasset.adapter.outbound.assetvariant.entity.AssetVariantEntity;
import io.oneasset.adapter.outbound.assetvariant.persistence.AssetVariantJpaRepository;
import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.assetvariant.model.AssetVariant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class AssetVariantPersistenceAdapter {

  private final AssetVariantJpaRepository assetVariantJpaRepository;

  public void save(AssetVariant assetVariant) {
    assetVariantJpaRepository.save(AssetVariantEntity.from(assetVariant));
  }

  @Transactional(readOnly = true)
  public List<AssetVariant> findAllByAssetId(AssetId assetId) {
    return assetVariantJpaRepository.findAllByAssetId(assetId.value()).stream()
        .map(AssetVariantEntity::toDomain)
        .toList();
  }

  @Transactional(readOnly = true)
  public Optional<AssetVariant> findByStorageKey(String storageKey) {
    return assetVariantJpaRepository.findByStorageKey(storageKey).map(AssetVariantEntity::toDomain);
  }
}
