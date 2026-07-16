package io.oneasset.application.asset.provided;

import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.domain.project.vo.ProjectId;

public interface AssetUseCase {

  RegistryAsset findByKeyAndProjectId(String key, ProjectId projectId);
}
