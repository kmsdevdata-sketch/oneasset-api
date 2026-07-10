package io.oneasset.application.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.application.project.command.CreateProjectCommand;
import io.oneasset.application.project.required.ProjectPersistencePort;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.projectmember.model.ProjectMember;
import io.oneasset.domain.user.vo.UserId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProjectServiceTest {

  private final ProjectPersistencePort projectPersistencePort = mock(ProjectPersistencePort.class);
  private final ProjectService projectService = new ProjectService(projectPersistencePort);

  @Test
  void createsProjectAndOwnerMember() {
    UserId userId = UserId.newId();
    Project savedProject = Project.create("My Blog", "my-blog");
    when(projectPersistencePort.findBySlug("my-blog")).thenReturn(Optional.empty());
    when(projectPersistencePort.save(any(Project.class))).thenReturn(savedProject);

    Project project = projectService.create(userId, new CreateProjectCommand("My Blog"));

    assertThat(project.getSlug()).isEqualTo("my-blog");
    verify(projectPersistencePort).findBySlug("my-blog");
    verify(projectPersistencePort).save(any(Project.class));
    verify(projectPersistencePort).save(any(ProjectMember.class));
  }

  @Test
  void createsUniqueSlugWhenSlugAlreadyExists() {
    UserId userId = UserId.newId();
    Project existingProject = Project.create("My Blog", "my-blog");
    Project savedProject = Project.create("My Blog", "my-blog-2");
    when(projectPersistencePort.findBySlug("my-blog")).thenReturn(Optional.of(existingProject));
    when(projectPersistencePort.findBySlug("my-blog-2")).thenReturn(Optional.empty());
    when(projectPersistencePort.save(any(Project.class))).thenReturn(savedProject);

    Project project = projectService.create(userId, new CreateProjectCommand("My Blog"));

    assertThat(project.getSlug()).isEqualTo("my-blog-2");
    verify(projectPersistencePort).findBySlug("my-blog");
    verify(projectPersistencePort).findBySlug("my-blog-2");
  }

  @Test
  void findsProjectsByUserMemberships() {
    UserId userId = UserId.newId();
    Project project = Project.create("My Blog", "my-blog");
    ProjectMember member = ProjectMember.createOwner(project.getId(), userId);
    when(projectPersistencePort.findAllMembersByUserId(userId)).thenReturn(List.of(member));
    when(projectPersistencePort.findAllByIds(List.of(project.getId())))
        .thenReturn(List.of(project));

    List<Project> projects = projectService.findAll(userId);

    assertThat(projects).hasSize(1);
    assertThat(projects.getFirst().getId()).isEqualTo(project.getId());
    verify(projectPersistencePort).findAllMembersByUserId(userId);
    verify(projectPersistencePort).findAllByIds(List.of(project.getId()));
  }

  @Test
  void findsProjectDetailWhenUserIsMember() {
    UserId userId = UserId.newId();
    Project project = Project.create("My Blog", "my-blog");
    ProjectMember member = ProjectMember.createOwner(project.getId(), userId);
    when(projectPersistencePort.findMember(project.getId(), userId))
        .thenReturn(Optional.of(member));
    when(projectPersistencePort.findById(project.getId())).thenReturn(Optional.of(project));

    Project found = projectService.findById(userId, project.getId());

    assertThat(found.getId()).isEqualTo(project.getId());
    verify(projectPersistencePort).findMember(project.getId(), userId);
    verify(projectPersistencePort).findById(project.getId());
  }
}
