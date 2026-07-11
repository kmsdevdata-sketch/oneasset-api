package io.oneasset.application.apikey;

import io.oneasset.adapter.outbound.apikey.ApiKeyPersistenceAdapter;
import io.oneasset.application.apikey.command.CreateApiKeyCommand;
import io.oneasset.application.apikey.provided.ApiKeyHashUseCase;
import io.oneasset.application.apikey.provided.ApiKeyUseCase;
import io.oneasset.application.apikey.required.ApiKeyPersistencePort;
import io.oneasset.domain.apikey.engine.ApiKeyGenerator;
import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.apikey.vo.ApiKeyHash;
import io.oneasset.domain.user.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiKeyService implements ApiKeyUseCase , ApiKeyHashUseCase {

    private final ApiKeyPersistencePort apiKeyPersistencePort;
    private final ApiKeyGenerator apiKeyGenerator;

    @Override
    @Transactional
    public ApiKey create(UserId id, CreateApiKeyCommand command) {

        ApiKey apiKey = ApiKey.create(command.projectId(), command.name(), );
        return apiKeyPersistencePort.save(apiKey);
    }

    @Override
    public ApiKeyHash createHash(String rawKey, String serverSecret) {
        apiKeyGenerator.generate();
        return null;
    }
}
