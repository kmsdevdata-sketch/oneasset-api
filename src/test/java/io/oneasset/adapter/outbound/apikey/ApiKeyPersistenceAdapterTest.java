package io.oneasset.adapter.outbound.apikey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.outbound.apikey.entity.ApiKeyEntity;
import io.oneasset.adapter.outbound.apikey.persistence.ApiKeyJpaRepository;
import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.apikey.vo.ApiKeyHash;
import io.oneasset.domain.apikey.vo.ApiKeyPrefix;
import io.oneasset.domain.apikey.vo.ApiKeyStatus;
import io.oneasset.domain.project.vo.ProjectId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ApiKeyPersistenceAdapterTest {

  private final ApiKeyJpaRepository apiKeyJpaRepository = mock(ApiKeyJpaRepository.class);
  private final ApiKeyPersistenceAdapter adapter =
      new ApiKeyPersistenceAdapter(apiKeyJpaRepository);

  @Test
  void savesApiKeyEntityConvertedFromDomain() {
    ApiKey apiKey = createApiKey();

    adapter.save(apiKey);

    verify(apiKeyJpaRepository).save(any(ApiKeyEntity.class));
  }

  @Test
  void findsActiveApiKeyById() {
    ApiKey apiKey = createApiKey();
    when(apiKeyJpaRepository.findByIdAndStatus(apiKey.getId().value(), ApiKeyStatus.ACTIVE))
        .thenReturn(Optional.of(ApiKeyEntity.from(apiKey)));

    Optional<ApiKey> found = adapter.findActiveById(apiKey.getId().value());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(apiKey.getId());
    verify(apiKeyJpaRepository).findByIdAndStatus(apiKey.getId().value(), ApiKeyStatus.ACTIVE);
  }

  @Test
  void findsActiveApiKeyByHash() {
    ApiKey apiKey = createApiKey();
    when(apiKeyJpaRepository.findByKeyHashAndStatus("hash-value", ApiKeyStatus.ACTIVE))
        .thenReturn(Optional.of(ApiKeyEntity.from(apiKey)));

    Optional<ApiKey> found = adapter.findActiveByHash(ApiKeyHash.of("hash-value"));

    assertThat(found).isPresent();
    assertThat(found.get().getHash()).isEqualTo(apiKey.getHash());
    verify(apiKeyJpaRepository).findByKeyHashAndStatus("hash-value", ApiKeyStatus.ACTIVE);
  }

  @Test
  void findsAllActiveApiKeysByProjectId() {
    ApiKey apiKey = createApiKey();
    when(apiKeyJpaRepository.findAllByProjectIdAndStatus(
            apiKey.getProjectId().value(), ApiKeyStatus.ACTIVE))
        .thenReturn(List.of(ApiKeyEntity.from(apiKey)));

    List<ApiKey> found = adapter.findAllActiveByProjectId(apiKey.getProjectId());

    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getProjectId()).isEqualTo(apiKey.getProjectId());
    verify(apiKeyJpaRepository)
        .findAllByProjectIdAndStatus(apiKey.getProjectId().value(), ApiKeyStatus.ACTIVE);
  }

  private static ApiKey createApiKey() {
    return ApiKey.create(
        ProjectId.newId(), "default key", ApiKeyPrefix.of("oa_live"), ApiKeyHash.of("hash-value"));
  }
}
