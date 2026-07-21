package io.oneasset.application.asset.required;

import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.project.vo.ProjectId;
import java.util.List;
import java.util.Optional;

public interface AssetPersistencePort {

  Asset register(Asset asset);

  Asset save(Asset asset);

  Optional<Asset> findActiveById(AssetId assetId);

  Optional<Asset> findActiveByStorageKeyAndProjectId(String storageKey, ProjectId projectId);

  List<Asset> findAllActiveByProjectId(ProjectId projectId);
}
