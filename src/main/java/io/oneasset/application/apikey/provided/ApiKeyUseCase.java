package io.oneasset.application.apikey.provided;

import io.oneasset.application.apikey.command.CreateApiKeyCommand;
import io.oneasset.application.apikey.result.CreatedApiKey;
import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;

public interface ApiKeyUseCase {

  CreatedApiKey create(UserId userId, CreateApiKeyCommand command);

  List<ApiKey> findAll(UserId userId, String projectId);

  ApiKey revoke(UserId userId, String projectId, String apiKeyId);
}
