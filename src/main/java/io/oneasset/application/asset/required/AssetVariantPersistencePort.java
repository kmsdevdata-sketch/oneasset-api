package io.oneasset.application.asset.required;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.assetvariant.model.AssetVariant;
import java.util.List;
import java.util.Optional;

public interface AssetVariantPersistencePort {

  AssetVariant save(AssetVariant assetVariant);

  Optional<AssetVariant> findByStorageKey(String storageKey);

  List<AssetVariant> findAllByAssetId(AssetId assetId);
}
