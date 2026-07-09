package io.oneasset.domain.asset.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AssetIdTest {

  @Test
  void createsAssetIdFromUuidString() {
    UUID value = UUID.randomUUID();

    AssetId assetId = AssetId.fromString(value.toString());

    assertThat(assetId.value()).isEqualTo(value);
    assertThat(assetId.toString()).isEqualTo(value.toString());
  }

  @Test
  void rejectsInvalidUuidString() {
    assertThatThrownBy(() -> AssetId.fromString("not-a-uuid")).isInstanceOf(RuntimeException.class);
  }
}
