package io.oneasset.adapter.outbound.asset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.outbound.asset.entity.AssetEntity;
import io.oneasset.adapter.outbound.asset.persistence.AssetJpaRepository;
import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AssetPersistenceAdapterTest {

  private final AssetJpaRepository assetJpaRepository = mock(AssetJpaRepository.class);
  private final AssetPersistenceAdapter adapter = new AssetPersistenceAdapter(assetJpaRepository);

  @Test
  void savesAssetEntityConvertedFromDomain() {
    Asset asset = createAsset();

    adapter.save(asset);

    verify(assetJpaRepository).save(any(AssetEntity.class));
  }

  @Test
  void findsActiveAssetById() {
    Asset asset = createAsset();
    when(assetJpaRepository.findByIdAndDeletedAtIsNull(asset.getId().value()))
        .thenReturn(Optional.of(AssetEntity.from(asset)));

    Optional<Asset> found = adapter.findActiveById(asset.getId().value());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(asset.getId());
    verify(assetJpaRepository).findByIdAndDeletedAtIsNull(asset.getId().value());
  }

  @Test
  void findsActiveAssetByStorageKey() {
    Asset asset = createAsset();
    when(assetJpaRepository.findByStorageKeyAndDeletedAtIsNull("assets/original.png"))
        .thenReturn(Optional.of(AssetEntity.from(asset)));

    Optional<Asset> found = adapter.findActiveByStorageKey("assets/original.png");

    assertThat(found).isPresent();
    assertThat(found.get().getStorageKey()).isEqualTo("assets/original.png");
    verify(assetJpaRepository).findByStorageKeyAndDeletedAtIsNull("assets/original.png");
  }

  @Test
  void findsAllActiveAssetsByProjectId() {
    Asset asset = createAsset();
    when(assetJpaRepository.findAllByProjectIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            asset.getProjectId().value()))
        .thenReturn(List.of(AssetEntity.from(asset)));

    List<Asset> found = adapter.findAllActiveByProjectId(asset.getProjectId());

    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getProjectId()).isEqualTo(asset.getProjectId());
    verify(assetJpaRepository)
        .findAllByProjectIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            asset.getProjectId().value());
  }

  private static Asset createAsset() {
    return Asset.create(
        ProjectId.newId(),
        UserId.newId(),
        "original.png",
        "image/png",
        1024,
        "oneasset-dev",
        "assets/original.png");
  }
}
