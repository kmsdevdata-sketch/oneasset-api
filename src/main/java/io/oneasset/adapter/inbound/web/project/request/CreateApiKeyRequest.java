package io.oneasset.adapter.inbound.web.project.request;

import io.oneasset.application.apikey.command.CreateApiKeyCommand;
import jakarta.validation.constraints.NotNull;

public record CreateApiKeyRequest(@NotNull String name) {

  public CreateApiKeyCommand toCommand(String projectId) {
    return new CreateApiKeyCommand(name, projectId);
  }
}
