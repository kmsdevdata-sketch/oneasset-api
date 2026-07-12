package io.oneasset.application.apikey.service;

import io.oneasset.application.apikey.command.CreateApiKeyCommand;
import io.oneasset.application.apikey.provided.ApiKeyAuthenticationUseCase;
import io.oneasset.application.apikey.provided.ApiKeyUseCase;
import io.oneasset.application.apikey.required.ApiKeyPersistencePort;
import io.oneasset.application.apikey.result.AuthenticatedApiKey;
import io.oneasset.application.apikey.result.CreatedApiKey;
import io.oneasset.application.project.required.ProjectPersistencePort;
import io.oneasset.domain.apikey.engine.ApiKeyGenerator;
import io.oneasset.domain.apikey.engine.GenerateApiKey;
import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.apikey.vo.ApiKeyHash;
import io.oneasset.domain.apikey.vo.ApiKeyId;
import io.oneasset.domain.apikey.vo.ApiKeyPrefix;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.vo.UserId;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.ApiKeyErrorCode;
import io.oneasset.exception.code.AuthErrorCode;
import io.oneasset.exception.code.ProjectErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiKeyService implements ApiKeyUseCase, ApiKeyAuthenticationUseCase {

  private final ApiKeyPersistencePort apiKeyPersistencePort;
  private final ProjectPersistencePort projectPersistencePort;
  private final ApiKeyGenerator apiKeyGenerator;

  @Override
  @Transactional
  public CreatedApiKey create(UserId userId, CreateApiKeyCommand command) {
    ProjectId projectId = ProjectId.fromString(command.projectId());
    ensureProjectMember(projectId, userId);

    GenerateApiKey generatedApiKey = apiKeyGenerator.generate();
    ApiKey apiKey = ApiKey.create(
        projectId,
        command.name(),
        ApiKeyPrefix.of(generatedApiKey.prefix()),
        ApiKeyHash.of(generatedApiKey.hash()));
    ApiKey savedApiKey = apiKeyPersistencePort.save(apiKey);

    return new CreatedApiKey(savedApiKey, generatedApiKey.rawKey());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ApiKey> findAll(UserId userId, String projectIdValue) {
    ProjectId projectId = ProjectId.fromString(projectIdValue);
    ensureProjectMember(projectId, userId);

    return apiKeyPersistencePort.findAllActiveByProjectId(projectId);
  }

  @Override
  @Transactional
  public ApiKey revoke(UserId userId, String projectIdValue, String apiKeyIdValue) {
    ProjectId projectId = ProjectId.fromString(projectIdValue);
    ensureProjectMember(projectId, userId);

    ApiKey apiKey = apiKeyPersistencePort
        .findActiveById(ApiKeyId.fromString(apiKeyIdValue))
        .orElseThrow(() -> new BaseException(ApiKeyErrorCode.API_KEY_NOT_FOUND));
    ensureApiKeyBelongsToProject(apiKey, projectId);

    apiKey.revoke();
    return apiKeyPersistencePort.save(apiKey);
  }

  @Override
  @Transactional(readOnly = true)
  public AuthenticatedApiKey authenticate(String rawKey) {
    if (rawKey == null || rawKey.isBlank()) {
      throw new BaseException(AuthErrorCode.INVALID_API_KEY);
    }

    String hash = apiKeyGenerator.hash(rawKey);

    ApiKey apiKey = apiKeyPersistencePort
        .findActiveByHash(ApiKeyHash.of(hash))
        .orElseThrow(() -> new BaseException(AuthErrorCode.INVALID_API_KEY));

    return AuthenticatedApiKey.from(apiKey.getProjectId().toString());
  }

  private void ensureProjectMember(ProjectId projectId, UserId userId) {
    projectPersistencePort
        .findMember(projectId, userId)
        .orElseThrow(() -> new BaseException(ProjectErrorCode.PROJECT_ACCESS_DENIED));
  }

  private void ensureApiKeyBelongsToProject(ApiKey apiKey, ProjectId projectId) {
    if (!apiKey.getProjectId().equals(projectId)) {
      throw new BaseException(ApiKeyErrorCode.API_KEY_NOT_FOUND);
    }
  }
}
