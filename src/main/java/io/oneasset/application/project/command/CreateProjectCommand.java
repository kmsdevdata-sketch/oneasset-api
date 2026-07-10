package io.oneasset.application.project.command;

import static io.oneasset.domain.common.DomainValidator.requireText;

public record CreateProjectCommand(String name) {

  public CreateProjectCommand {
    name = requireText(name, "name");
  }
}
