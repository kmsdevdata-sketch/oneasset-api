package io.oneasset.adapter.outbound.apikey.persistence;

import io.oneasset.adapter.outbound.apikey.entity.ApiKeyEntity;
import io.oneasset.domain.apikey.vo.ApiKeyStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyJpaRepository extends JpaRepository<ApiKeyEntity, UUID> {

  Optional<ApiKeyEntity> findByIdAndStatus(UUID id, ApiKeyStatus status);

  Optional<ApiKeyEntity> findByKeyHashAndStatus(String keyHash, ApiKeyStatus status);

  List<ApiKeyEntity> findAllByProjectIdAndStatus(UUID projectId, ApiKeyStatus status);
}
