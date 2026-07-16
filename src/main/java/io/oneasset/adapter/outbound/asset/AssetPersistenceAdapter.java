package io.oneasset.adapter.outbound.asset;

import io.oneasset.adapter.outbound.asset.entity.AssetEntity;
import io.oneasset.adapter.outbound.asset.persistence.AssetJpaRepository;
import io.oneasset.application.asset.required.AssetPersistencePort;
import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.project.vo.ProjectId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class AssetPersistenceAdapter implements AssetPersistencePort {

  private final AssetJpaRepository assetJpaRepository;

  public Asset register(Asset asset) {
    return save(asset);
  }

  public Asset save(Asset asset) {
    return assetJpaRepository.save(AssetEntity.from(asset)).toDomain();
  }

  @Transactional(readOnly = true)
  public Optional<Asset> findActiveById(UUID assetId) {
    return assetJpaRepository.findByIdAndDeletedAtIsNull(assetId).map(AssetEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public Optional<Asset> findActiveByStorageKeyAndProjectId(
      String storageKey, ProjectId projectId) {
    return assetJpaRepository
        .findByStorageKeyAndProjectIdAndDeletedAtIsNull(storageKey, projectId.value())
        .map(AssetEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public Optional<Asset> findActiveByStorageKey(String storageKey) {
    return assetJpaRepository
        .findByStorageKeyAndDeletedAtIsNull(storageKey)
        .map(AssetEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public List<Asset> findAllActiveByProjectId(ProjectId projectId) {
    return assetJpaRepository
        .findAllByProjectIdAndDeletedAtIsNullOrderByCreatedAtDesc(projectId.value())
        .stream()
        .map(AssetEntity::toDomain)
        .toList();
  }
}
