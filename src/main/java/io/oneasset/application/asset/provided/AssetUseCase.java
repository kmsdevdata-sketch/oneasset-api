package io.oneasset.application.asset.provided;

import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;

public interface AssetUseCase {

  RegistryAsset findByKeyAndProjectId(String key, ProjectId projectId);

  List<RegistryAsset> findAllByProjectId(ProjectId projectId);

  RegistryAsset deleteByKeyAndProjectId(String key, ProjectId projectId);

  RegistryAsset findByKey(UserId userId, String projectId, String key);

  List<RegistryAsset> findAll(UserId userId, String projectId);

  RegistryAsset deleteByKey(UserId userId, String projectId, String key);
}
