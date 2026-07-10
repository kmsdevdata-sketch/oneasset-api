package io.oneasset.application.project.required;

import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.projectmember.model.ProjectMember;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;
import java.util.Optional;

public interface ProjectPersistencePort {

  Project save(Project project);

  void save(ProjectMember projectMember);

  Optional<Project> findById(ProjectId projectId);

  Optional<Project> findBySlug(String slug);

  List<Project> findAllByIds(List<ProjectId> projectIds);

  Optional<ProjectMember> findMember(ProjectId projectId, UserId userId);

  List<ProjectMember> findAllMembersByUserId(UserId userId);
}
