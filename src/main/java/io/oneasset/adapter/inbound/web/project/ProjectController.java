package io.oneasset.adapter.inbound.web.project;

import io.oneasset.adapter.inbound.auth.JwtCurrentUserExtractor;
import io.oneasset.adapter.inbound.response.ApiResponse;
import io.oneasset.adapter.inbound.web.project.request.CreateProjectRequest;
import io.oneasset.adapter.inbound.web.project.response.ProjectResponse;
import io.oneasset.application.project.provided.ProjectUseCase;
import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.application.user.provided.UserSyncUseCase;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.model.User;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectUseCase projectUseCase;
  private final UserSyncUseCase userSyncUseCase;
  private final JwtCurrentUserExtractor jwtCurrentUserExtractor;

  @PostMapping
  public ApiResponse<ProjectResponse> create(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateProjectRequest request) {
    User user = currentUser(jwt);
    Project project = projectUseCase.create(user.getId(), request.toCommand());

    return ApiResponse.ok(ProjectResponse.from(project));
  }

  @GetMapping
  public ApiResponse<List<ProjectResponse>> list(@AuthenticationPrincipal Jwt jwt) {
    User user = currentUser(jwt);
    List<ProjectResponse> projects =
        projectUseCase.findAll(user.getId()).stream().map(ProjectResponse::from).toList();

    return ApiResponse.ok(projects);
  }

  @GetMapping("/{projectId}")
  public ApiResponse<ProjectResponse> detail(
      @AuthenticationPrincipal Jwt jwt, @PathVariable String projectId) {
    User user = currentUser(jwt);
    Project project = projectUseCase.findById(user.getId(), ProjectId.fromString(projectId));

    return ApiResponse.ok(ProjectResponse.from(project));
  }

  private User currentUser(Jwt jwt) {
    CurrentUser currentUser = jwtCurrentUserExtractor.extract(jwt);
    return userSyncUseCase.findOrCreate(currentUser);
  }
}
