package io.oneasset.application.apikey.result;

import static io.oneasset.domain.common.DomainValidator.requireText;

public record AuthenticatedApiKey(String projectId) {

  public AuthenticatedApiKey {
    projectId = requireText(projectId, "projectId");
  }

  public static AuthenticatedApiKey from(String projectId) {
    return new AuthenticatedApiKey(projectId);
  }
}
