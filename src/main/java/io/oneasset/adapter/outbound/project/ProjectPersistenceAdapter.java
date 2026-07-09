package io.oneasset.adapter.outbound.project;

import io.oneasset.adapter.outbound.project.entity.ProjectEntity;
import io.oneasset.adapter.outbound.project.persistence.ProjectJpaRepository;
import io.oneasset.domain.project.model.Project;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class ProjectPersistenceAdapter {

  private final ProjectJpaRepository projectJpaRepository;

  public void save(Project project) {
    projectJpaRepository.save(ProjectEntity.from(project));
  }

  @Transactional(readOnly = true)
  public Optional<Project> findActiveById(UUID projectId) {
    return projectJpaRepository.findByIdAndDeletedAtIsNull(projectId).map(ProjectEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public Optional<Project> findActiveBySlug(String slug) {
    return projectJpaRepository.findBySlugAndDeletedAtIsNull(slug).map(ProjectEntity::toDomain);
  }
}
