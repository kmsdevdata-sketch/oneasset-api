package io.oneasset.adapter.outbound.assetvariant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.outbound.assetvariant.entity.AssetVariantEntity;
import io.oneasset.adapter.outbound.assetvariant.persistence.AssetVariantJpaRepository;
import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.assetvariant.model.AssetVariant;
import io.oneasset.domain.assetvariant.vo.AssetVariantType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AssetVariantPersistenceAdapterTest {

  private final AssetVariantJpaRepository assetVariantJpaRepository =
      mock(AssetVariantJpaRepository.class);
  private final AssetVariantPersistenceAdapter adapter =
      new AssetVariantPersistenceAdapter(assetVariantJpaRepository);

  @Test
  void savesAssetVariantEntityConvertedFromDomain() {
    AssetVariant variant = createVariant();
    when(assetVariantJpaRepository.save(any(AssetVariantEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AssetVariant savedVariant = adapter.save(variant);

    assertThat(savedVariant.getId()).isEqualTo(variant.getId());
    verify(assetVariantJpaRepository).save(any(AssetVariantEntity.class));
  }

  @Test
  void findsAllAssetVariantsByAssetId() {
    AssetVariant variant = createVariant();
    when(assetVariantJpaRepository.findAllByAssetId(variant.getAssetId().value()))
        .thenReturn(List.of(AssetVariantEntity.from(variant)));

    List<AssetVariant> found = adapter.findAllByAssetId(variant.getAssetId());

    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getAssetId()).isEqualTo(variant.getAssetId());
    verify(assetVariantJpaRepository).findAllByAssetId(variant.getAssetId().value());
  }

  @Test
  void findsAssetVariantByStorageKey() {
    AssetVariant variant = createVariant();
    when(assetVariantJpaRepository.findByStorageKey("assets/original.webp"))
        .thenReturn(Optional.of(AssetVariantEntity.from(variant)));

    Optional<AssetVariant> found = adapter.findByStorageKey("assets/original.webp");

    assertThat(found).isPresent();
    assertThat(found.get().getStorageKey()).isEqualTo("assets/original.webp");
    verify(assetVariantJpaRepository).findByStorageKey("assets/original.webp");
  }

  private static AssetVariant createVariant() {
    return AssetVariant.create(
        AssetId.newId(),
        AssetVariantType.WEBP,
        "image/webp",
        512,
        "oneasset-dev",
        "assets/original.webp",
        100,
        100);
  }
}
