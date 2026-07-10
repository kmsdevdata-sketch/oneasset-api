package io.oneasset.application.project.provided;

import io.oneasset.application.project.command.CreateProjectCommand;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;

public interface ProjectUseCase {

  Project create(UserId userId, CreateProjectCommand command);

  List<Project> findAll(UserId userId);

  Project findById(UserId userId, ProjectId projectId);
}
