package io.oneasset.application.asset.required;

import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.project.vo.ProjectId;
import java.util.Optional;

public interface AssetPersistencePort {

  Asset register(Asset asset);

  Optional<Asset> findActiveByStorageKeyAndProjectId(String storageKey, ProjectId projectId);
}
