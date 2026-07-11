package io.oneasset.adapter.inbound.web.project.request;

import io.oneasset.application.apikey.command.CreateApiKeyCommand;

public record CreateApiKeyRequest(
        String name,
        String projectId) {

    public CreateApiKeyCommand toCommand(String projectId) {
        return new CreateApiKeyCommand(name,projectId);
    }
}
