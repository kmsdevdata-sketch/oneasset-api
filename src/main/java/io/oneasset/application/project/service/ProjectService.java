package io.oneasset.application.project.service;

import io.oneasset.application.project.command.CreateProjectCommand;
import io.oneasset.application.project.provided.ProjectUseCase;
import io.oneasset.application.project.required.ProjectPersistencePort;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.projectmember.model.ProjectMember;
import io.oneasset.domain.user.vo.UserId;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectUseCase {

  private final ProjectPersistencePort projectPersistencePort;

  @Override
  @Transactional
  public Project create(UserId userId, CreateProjectCommand command) {
    String slug = createUniqueSlug(command.name());
    Project project = projectPersistencePort.save(Project.create(command.name(), slug));

    projectPersistencePort.save(ProjectMember.createOwner(project.getId(), userId));

    return project;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Project> findAll(UserId userId) {
    List<ProjectId> projectIds = projectPersistencePort.findAllMembersByUserId(userId).stream()
        .map(ProjectMember::getProjectId)
        .toList();

    return projectPersistencePort.findAllByIds(projectIds);
  }

  @Override
  @Transactional(readOnly = true)
  public Project findById(UserId userId, ProjectId projectId) {
    projectPersistencePort
        .findMember(projectId, userId)
        .orElseThrow(() -> new IllegalArgumentException("Project member not found"));

    return projectPersistencePort
        .findById(projectId)
        .orElseThrow(() -> new IllegalArgumentException("Project not found"));
  }

  private String createUniqueSlug(String name) {
    String baseSlug = toSlug(name);
    String slug = baseSlug;
    int suffix = 2;

    while (projectPersistencePort.findBySlug(slug).isPresent()) {
      slug = baseSlug + "-" + suffix;
      suffix++;
    }

    return slug;
  }

  private String toSlug(String value) {
    String slug = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(Locale.ROOT)
        .replaceAll("[^a-z0-9]+", "-")
        .replaceAll("(^-|-$)", "");

    if (slug.isBlank()) {
      return "project";
    }

    return slug;
  }
}
