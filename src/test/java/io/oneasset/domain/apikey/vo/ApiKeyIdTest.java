package io.oneasset.domain.apikey.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ApiKeyIdTest {

  @Test
  void createsApiKeyIdFromUuidString() {
    UUID value = UUID.randomUUID();

    ApiKeyId apiKeyId = ApiKeyId.fromString(value.toString());

    assertThat(apiKeyId.value()).isEqualTo(value);
    assertThat(apiKeyId.toString()).isEqualTo(value.toString());
  }

  @Test
  void rejectsInvalidUuidString() {
    assertThatThrownBy(() -> ApiKeyId.fromString("not-a-uuid"))
        .isInstanceOf(RuntimeException.class);
  }
}
