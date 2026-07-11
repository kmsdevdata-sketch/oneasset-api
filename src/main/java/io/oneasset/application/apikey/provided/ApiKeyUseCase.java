package io.oneasset.application.apikey.provided;

import io.oneasset.adapter.inbound.web.project.request.CreateApiKeyRequest;
import io.oneasset.application.apikey.command.CreateApiKeyCommand;
import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.user.vo.UserId;

public interface ApiKeyUseCase {

    ApiKey create(UserId id, CreateApiKeyCommand command);
}
