package io.oneasset.application.apikey.required;

import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.apikey.vo.ApiKeyId;
import io.oneasset.domain.project.vo.ProjectId;
import java.util.List;
import java.util.Optional;

public interface ApiKeyPersistencePort {

  ApiKey save(ApiKey apiKey);

  Optional<ApiKey> findActiveById(ApiKeyId apiKeyId);

  List<ApiKey> findAllActiveByProjectId(ProjectId projectId);
}
