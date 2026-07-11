package io.oneasset.config.apikey;

import io.oneasset.domain.apikey.engine.ApiKeyGenerator;
import io.oneasset.domain.apikey.engine.DefaultApiKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyGeneratorConfig {

  @Bean
  public ApiKeyGenerator apiKeyGenerator(
      @Value("${oneasset.api-key.server-secret}") String serverSecret) {
    return new DefaultApiKeyGenerator(serverSecret);
  }
}
