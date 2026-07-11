package io.oneasset.domain.apikey.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DefaultApiKeyGeneratorTest {

  @Test
  void generatesRawKeyWithPrefixAndHash() {
    DefaultApiKeyGenerator generator = new DefaultApiKeyGenerator("server-secret");

    GenerateApiKey generatedApiKey = generator.generate();

    assertThat(generatedApiKey.rawKey()).startsWith("oa_live_");
    assertThat(generatedApiKey.prefix()).isEqualTo(generatedApiKey.rawKey().substring(0, 16));
    assertThat(generatedApiKey.hash()).hasSize(64);
    assertThat(generatedApiKey.hash()).isEqualTo(generator.hash(generatedApiKey.rawKey()));
  }

  @Test
  void hashesSameRawKeyWithSameSecret() {
    DefaultApiKeyGenerator generator = new DefaultApiKeyGenerator("server-secret");

    String hash = generator.hash("oa_live_test-key");

    assertThat(hash).isEqualTo(generator.hash("oa_live_test-key"));
    assertThat(hash)
        .isNotEqualTo(new DefaultApiKeyGenerator("other-secret").hash("oa_live_test-key"));
  }

  @Test
  void rejectsBlankSecretAndRawKey() {
    assertThatThrownBy(() -> new DefaultApiKeyGenerator(" ")).isInstanceOf(RuntimeException.class);

    DefaultApiKeyGenerator generator = new DefaultApiKeyGenerator("server-secret");
    assertThatThrownBy(() -> generator.hash(" ")).isInstanceOf(RuntimeException.class);
  }
}
