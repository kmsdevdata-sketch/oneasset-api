package io.oneasset.application.asset.required;

import io.oneasset.domain.asset.model.Asset;

public interface AssetPersistencePort {

  Asset register(Asset asset);
}
