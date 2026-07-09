package io.oneasset.adapter.outbound.project.persistence;

import io.oneasset.adapter.outbound.project.entity.ProjectEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, UUID> {

  Optional<ProjectEntity> findByIdAndDeletedAtIsNull(UUID id);

  Optional<ProjectEntity> findBySlugAndDeletedAtIsNull(String slug);
}
