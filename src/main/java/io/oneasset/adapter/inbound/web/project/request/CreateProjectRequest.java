package io.oneasset.adapter.inbound.web.project.request;

import io.oneasset.application.project.command.CreateProjectCommand;
import jakarta.validation.constraints.NotBlank;

public record CreateProjectRequest(@NotBlank String name) {

  public CreateProjectCommand toCommand() {
    return new CreateProjectCommand(name);
  }
}
