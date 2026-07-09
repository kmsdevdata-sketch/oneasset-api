package io.oneasset.domain.assetvariant.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.assetvariant.vo.AssetVariantId;
import io.oneasset.domain.assetvariant.vo.AssetVariantType;
import java.time.LocalDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class AssetVariantTest {

  @Test
  void createsAssetVariant() {
    AssetId assetId = AssetId.newId();

    AssetVariant variant =
        AssetVariant.create(
            assetId,
            AssetVariantType.WEBP,
            "image/webp",
            512,
            "oneasset-variants",
            "variants/project/profile.webp",
            300,
            300);

    assertThat(variant.getId()).isNotNull();
    assertThat(variant.getAssetId()).isEqualTo(assetId);
    assertThat(variant.getType()).isEqualTo(AssetVariantType.WEBP);
    assertThat(variant.getContentType()).isEqualTo("image/webp");
    assertThat(variant.getSizeBytes()).isEqualTo(512);
    assertThat(variant.getBucket()).isEqualTo("oneasset-variants");
    assertThat(variant.getStorageKey()).isEqualTo("variants/project/profile.webp");
    assertThat(variant.getWidth()).isEqualTo(300);
    assertThat(variant.getHeight()).isEqualTo(300);
    assertThat(variant.getCreatedAt()).isNotNull();
  }

  @Test
  void reconstitutesPersistedAssetVariant() {
    AssetVariantId id = AssetVariantId.newId();
    AssetId assetId = AssetId.newId();
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 12, 0);

    AssetVariant variant =
        AssetVariant.reconstitute(
            id,
            assetId,
            AssetVariantType.ORIGINAL,
            "image/png",
            1024,
            "bucket",
            "key",
            null,
            null,
            createdAt);

    assertThat(variant.getId()).isEqualTo(id);
    assertThat(variant.getAssetId()).isEqualTo(assetId);
    assertThat(variant.getCreatedAt()).isEqualTo(createdAt);
    assertThat(variant.getWidth()).isNull();
    assertThat(variant.getHeight()).isNull();
  }

  @Test
  void rejectsInvalidAssetVariantInput() {
    assertDomainFailure(
        () ->
            AssetVariant.create(
                AssetId.newId(), AssetVariantType.WEBP, " ", 512, "bucket", "key", 300, 300));
    assertDomainFailure(
        () ->
            AssetVariant.create(
                AssetId.newId(),
                AssetVariantType.WEBP,
                "image/webp",
                0,
                "bucket",
                "key",
                300,
                300));
    assertDomainFailure(
        () ->
            AssetVariant.create(
                AssetId.newId(),
                AssetVariantType.WEBP,
                "image/webp",
                512,
                "bucket",
                "key",
                0,
                300));
  }

  private static void assertDomainFailure(ThrowingCallable action) {
    assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
  }
}
