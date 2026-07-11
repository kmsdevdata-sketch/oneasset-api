package io.oneasset.adapter.inbound.web.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.inbound.auth.JwtCurrentUserExtractor;
import io.oneasset.adapter.inbound.response.ApiResponse;
import io.oneasset.adapter.inbound.web.project.request.CreateProjectRequest;
import io.oneasset.adapter.inbound.web.project.response.ProjectResponse;
import io.oneasset.application.project.command.CreateProjectCommand;
import io.oneasset.application.project.provided.ProjectUseCase;
import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.application.user.provided.UserSyncUseCase;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.user.model.User;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class ProjectControllerTest {

  private final ProjectUseCase projectUseCase = mock(ProjectUseCase.class);
  private final UserSyncUseCase userSyncUseCase = mock(UserSyncUseCase.class);
  private final JwtCurrentUserExtractor jwtCurrentUserExtractor =
      mock(JwtCurrentUserExtractor.class);
  private final ProjectController projectController =
      new ProjectController(projectUseCase, userSyncUseCase, jwtCurrentUserExtractor);

  @Test
  void createsProjectForCurrentUser() {
    Jwt jwt = createJwt();
    CurrentUser currentUser = new CurrentUser("cognito-sub-1", "user@example.com", "Minseo");
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    Project project = Project.create("My Blog", "my-blog");
    when(jwtCurrentUserExtractor.extract(jwt)).thenReturn(currentUser);
    when(userSyncUseCase.findOrCreate(currentUser)).thenReturn(user);
    when(projectUseCase.create(user.getId(), new CreateProjectCommand("My Blog")))
        .thenReturn(project);

    ApiResponse<ProjectResponse> response =
        projectController.createProject(jwt, new CreateProjectRequest("My Blog"));

    assertThat(response.success()).isTrue();
    assertThat(response.data().slug()).isEqualTo("my-blog");
    verify(jwtCurrentUserExtractor).extract(jwt);
    verify(userSyncUseCase).findOrCreate(currentUser);
    verify(projectUseCase).create(user.getId(), new CreateProjectCommand("My Blog"));
  }

  @Test
  void returnsCurrentUserProjects() {
    Jwt jwt = createJwt();
    CurrentUser currentUser = new CurrentUser("cognito-sub-1", "user@example.com", "Minseo");
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    Project project = Project.create("My Blog", "my-blog");
    when(jwtCurrentUserExtractor.extract(jwt)).thenReturn(currentUser);
    when(userSyncUseCase.findOrCreate(currentUser)).thenReturn(user);
    when(projectUseCase.findAll(user.getId())).thenReturn(List.of(project));

    ApiResponse<List<ProjectResponse>> response = projectController.list(jwt);

    assertThat(response.success()).isTrue();
    assertThat(response.data()).hasSize(1);
    assertThat(response.data().getFirst().name()).isEqualTo("My Blog");
    verify(projectUseCase).findAll(user.getId());
  }

  @Test
  void returnsProjectDetail() {
    Jwt jwt = createJwt();
    CurrentUser currentUser = new CurrentUser("cognito-sub-1", "user@example.com", "Minseo");
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    Project project = Project.create("My Blog", "my-blog");
    when(jwtCurrentUserExtractor.extract(jwt)).thenReturn(currentUser);
    when(userSyncUseCase.findOrCreate(currentUser)).thenReturn(user);
    when(projectUseCase.findById(user.getId(), project.getId())).thenReturn(project);

    ApiResponse<ProjectResponse> response =
        projectController.detail(jwt, project.getId().toString());

    assertThat(response.success()).isTrue();
    assertThat(response.data().id()).isEqualTo(project.getId().toString());
    verify(projectUseCase).findById(user.getId(), project.getId());
  }

  private static Jwt createJwt() {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(60))
        .subject("cognito-sub-1")
        .build();
  }
}
