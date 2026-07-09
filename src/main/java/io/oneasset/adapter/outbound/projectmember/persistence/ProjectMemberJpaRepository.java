package io.oneasset.adapter.outbound.projectmember.persistence;

import io.oneasset.adapter.outbound.projectmember.entity.ProjectMemberEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberEntity, UUID> {

  Optional<ProjectMemberEntity> findByProjectIdAndUserId(UUID projectId, UUID userId);

  List<ProjectMemberEntity> findAllByProjectId(UUID projectId);

  List<ProjectMemberEntity> findAllByUserId(UUID userId);
}
