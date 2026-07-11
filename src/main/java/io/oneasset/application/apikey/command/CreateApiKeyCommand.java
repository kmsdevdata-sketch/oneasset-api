package io.oneasset.application.apikey.command;

import static io.oneasset.domain.common.DomainValidator.requireText;

public record CreateApiKeyCommand(String name, String projectId) {

  public CreateApiKeyCommand {
    name = requireText(name, "name");
    projectId = requireText(projectId, "projectId");
  }
}
