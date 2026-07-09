package io.oneasset.domain.assetvariant.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AssetVariantIdTest {

  @Test
  void createsAssetVariantIdFromUuidString() {
    UUID value = UUID.randomUUID();

    AssetVariantId assetVariantId = AssetVariantId.fromString(value.toString());

    assertThat(assetVariantId.value()).isEqualTo(value);
    assertThat(assetVariantId.toString()).isEqualTo(value.toString());
  }

  @Test
  void rejectsInvalidUuidString() {
    assertThatThrownBy(() -> AssetVariantId.fromString("not-a-uuid"))
        .isInstanceOf(RuntimeException.class);
  }
}
