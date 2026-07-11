package io.oneasset.adapter.outbound.apikey;

import io.oneasset.adapter.outbound.apikey.entity.ApiKeyEntity;
import io.oneasset.adapter.outbound.apikey.persistence.ApiKeyJpaRepository;
import io.oneasset.application.apikey.required.ApiKeyPersistencePort;
import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.apikey.vo.ApiKeyHash;
import io.oneasset.domain.apikey.vo.ApiKeyStatus;
import io.oneasset.domain.project.vo.ProjectId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class ApiKeyPersistenceAdapter implements ApiKeyPersistencePort {

  private final ApiKeyJpaRepository apiKeyJpaRepository;

  public ApiKey save(ApiKey apiKey) {
    return apiKeyJpaRepository.save(ApiKeyEntity.from(apiKey)).toDomain();
  }

  @Transactional(readOnly = true)
  public Optional<ApiKey> findActiveById(UUID apiKeyId) {
    return apiKeyJpaRepository
        .findByIdAndStatus(apiKeyId, ApiKeyStatus.ACTIVE)
        .map(ApiKeyEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public Optional<ApiKey> findActiveByHash(ApiKeyHash hash) {
    return apiKeyJpaRepository
        .findByKeyHashAndStatus(hash.value(), ApiKeyStatus.ACTIVE)
        .map(ApiKeyEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public List<ApiKey> findAllActiveByProjectId(ProjectId projectId) {
    return apiKeyJpaRepository
        .findAllByProjectIdAndStatus(projectId.value(), ApiKeyStatus.ACTIVE)
        .stream()
        .map(ApiKeyEntity::toDomain)
        .toList();
  }
}
