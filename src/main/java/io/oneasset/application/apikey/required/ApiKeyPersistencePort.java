package io.oneasset.application.apikey.required;

import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.project.vo.ProjectId;
import java.util.List;

public interface ApiKeyPersistencePort {

  ApiKey save(ApiKey apiKey);

  List<ApiKey> findAllActiveByProjectId(ProjectId projectId);
}
