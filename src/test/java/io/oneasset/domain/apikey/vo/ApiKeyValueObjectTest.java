package io.oneasset.domain.apikey.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ApiKeyValueObjectTest {

  @Test
  void createsPrefixAndHash() {
    assertThat(ApiKeyPrefix.of("oa_live_abcd").value()).isEqualTo("oa_live_abcd");
    assertThat(ApiKeyHash.of("hashed-value").value()).isEqualTo("hashed-value");
  }

  @Test
  void rejectsBlankPrefixAndHash() {
    assertThatThrownBy(() -> ApiKeyPrefix.of(" ")).isInstanceOf(RuntimeException.class);
    assertThatThrownBy(() -> ApiKeyHash.of(" ")).isInstanceOf(RuntimeException.class);
  }
}
