package io.oneasset.adapter.inbound.web.project.response;

import io.oneasset.domain.project.model.Project;
import java.time.LocalDateTime;

public record ProjectResponse(String id, String name, String slug, LocalDateTime createdAt) {

  public static ProjectResponse from(Project project) {
    return new ProjectResponse(
        project.getId().toString(), project.getName(), project.getSlug(), project.getCreatedAt());
  }
}
