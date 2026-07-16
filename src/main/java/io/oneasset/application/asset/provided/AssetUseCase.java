package io.oneasset.application.asset.provided;

import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.domain.project.vo.ProjectId;
import java.util.List;

public interface AssetUseCase {

  RegistryAsset findByKeyAndProjectId(String key, ProjectId projectId);

  List<RegistryAsset> findAllByProjectId(ProjectId projectId);

  RegistryAsset deleteByKeyAndProjectId(String key, ProjectId projectId);
}
