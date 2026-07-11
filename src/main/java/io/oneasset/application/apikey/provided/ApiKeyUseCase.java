package io.oneasset.application.apikey.provided;

import io.oneasset.application.apikey.command.CreateApiKeyCommand;
import io.oneasset.application.apikey.result.CreatedApiKey;
import io.oneasset.domain.user.vo.UserId;

public interface ApiKeyUseCase {

  CreatedApiKey create(UserId userId, CreateApiKeyCommand command);
}
