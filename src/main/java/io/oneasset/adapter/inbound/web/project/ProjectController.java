package io.oneasset.adapter.inbound.web.project;

import io.oneasset.adapter.inbound.auth.JwtCurrentUserExtractor;
import io.oneasset.adapter.inbound.response.ApiResponse;
import io.oneasset.adapter.inbound.v1.developer.response.AssetResponse;
import io.oneasset.adapter.inbound.web.project.request.CreateApiKeyRequest;
import io.oneasset.adapter.inbound.web.project.request.CreateProjectRequest;
import io.oneasset.adapter.inbound.web.project.response.ApiKeyResponse;
import io.oneasset.adapter.inbound.web.project.response.ProjectResponse;
import io.oneasset.application.apikey.provided.ApiKeyUseCase;
import io.oneasset.application.apikey.result.CreatedApiKey;
import io.oneasset.application.asset.provided.AssetUseCase;
import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.application.project.provided.ProjectUseCase;
import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.application.user.provided.UserSyncUseCase;
import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.model.User;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectUseCase projectUseCase;
  private final UserSyncUseCase userSyncUseCase;
  private final ApiKeyUseCase apiKeyUseCase;
  private final AssetUseCase assetUseCase;
  private final JwtCurrentUserExtractor jwtCurrentUserExtractor;

  @PostMapping
  public ApiResponse<ProjectResponse> createProject(
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

  @GetMapping("/{projectId}/api-keys")
  public ApiResponse<List<ApiKeyResponse>> listApiKeys(
      @AuthenticationPrincipal Jwt jwt, @PathVariable String projectId) {
    User user = currentUser(jwt);
    List<ApiKey> apiKeys = apiKeyUseCase.findAll(user.getId(), projectId);
    List<ApiKeyResponse> response = apiKeys.stream().map(ApiKeyResponse::from).toList();

    return ApiResponse.ok(response);
  }

  @PostMapping("/{projectId}/api-keys")
  public ApiResponse<ApiKeyResponse> createApiKey(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String projectId,
      @Valid @RequestBody CreateApiKeyRequest request) {
    User user = currentUser(jwt);

    CreatedApiKey apiKey = apiKeyUseCase.create(user.getId(), request.toCommand(projectId));

    return ApiResponse.ok(ApiKeyResponse.fromCreated(apiKey));
  }

  @DeleteMapping("/{projectId}/api-keys/{apiKeyId}")
  public ApiResponse<ApiKeyResponse> revokeApiKey(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String projectId,
      @PathVariable String apiKeyId) {
    User user = currentUser(jwt);
    ApiKey apiKey = apiKeyUseCase.revoke(user.getId(), projectId, apiKeyId);

    return ApiResponse.ok(ApiKeyResponse.from(apiKey));
  }

  @GetMapping(value = "/{projectId}/assets", params = "!key")
  public ApiResponse<List<AssetResponse>> listAssets(
      @AuthenticationPrincipal Jwt jwt, @PathVariable String projectId) {
    User user = currentUser(jwt);
    List<AssetResponse> response = assetUseCase.findAll(user.getId(), projectId).stream()
        .map(AssetResponse::from)
        .toList();

    return ApiResponse.ok(response);
  }

  @GetMapping(value = "/{projectId}/assets", params = "key")
  public ApiResponse<AssetResponse> assetDetail(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String projectId,
      @RequestParam(value = "key", required = true) String key) {
    User user = currentUser(jwt);
    RegistryAsset asset = assetUseCase.findByKey(user.getId(), projectId, key);

    return ApiResponse.ok(AssetResponse.from(asset));
  }

  @DeleteMapping("/{projectId}/assets")
  public ApiResponse<AssetResponse> deleteAsset(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String projectId,
      @RequestParam(value = "key", required = true) String key) {
    User user = currentUser(jwt);
    RegistryAsset asset = assetUseCase.deleteByKey(user.getId(), projectId, key);

    return ApiResponse.ok(AssetResponse.from(asset));
  }

  private User currentUser(Jwt jwt) {
    CurrentUser currentUser = jwtCurrentUserExtractor.extract(jwt);
    return userSyncUseCase.findOrCreate(currentUser);
  }
}
