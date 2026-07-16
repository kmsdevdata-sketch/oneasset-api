package io.oneasset.adapter.inbound.v1.developer;

import io.oneasset.adapter.inbound.response.ApiResponse;
import io.oneasset.adapter.inbound.v1.developer.request.CreateAssetMetadataRequest;
import io.oneasset.adapter.inbound.v1.developer.response.AssetResponse;
import io.oneasset.application.apikey.provided.ApiKeyAuthenticationUseCase;
import io.oneasset.application.asset.command.RegisterAssetCommand;
import io.oneasset.application.asset.provided.AssetRegisterUseCase;
import io.oneasset.application.asset.provided.AssetUseCase;
import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.domain.project.vo.ProjectId;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/assets")
public class DeveloperAssetController {

  private final ApiKeyAuthenticationUseCase apiKeyAuthenticationUseCase;
  private final AssetRegisterUseCase assetRegisterUseCase;
  private final AssetUseCase assetUseCase;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<AssetResponse> registerAsset(
      @RequestPart("file") MultipartFile file,
      @RequestParam(value = "key", required = false) String key,
      @RequestParam(value = "fileName", required = false) String fileName,
      @RequestHeader("X-OneAsset-Api-Key") String rawKey)
      throws IOException {
    String projectId = apiKeyAuthenticationUseCase.authenticate(rawKey).projectId();

    CreateAssetMetadataRequest metadata = new CreateAssetMetadataRequest(key, fileName);
    RegistryAsset asset = assetRegisterUseCase.register(toCommand(projectId, file, metadata));

    return ApiResponse.ok(AssetResponse.from(asset));
  }

  @GetMapping(params = "key")
  public ApiResponse<AssetResponse> detail(
      @RequestHeader("X-OneAsset-Api-Key") String rawKey,
      @RequestParam(value = "key", required = true) String key) {
    String projectId = apiKeyAuthenticationUseCase.authenticate(rawKey).projectId();

    RegistryAsset asset = assetUseCase.findByKeyAndProjectId(key, ProjectId.fromString(projectId));

    return ApiResponse.ok(AssetResponse.from(asset));
  }

  @GetMapping(params = "!key")
  public ApiResponse<List<AssetResponse>> list(@RequestHeader("X-OneAsset-Api-Key") String rawKey) {
    String projectId = apiKeyAuthenticationUseCase.authenticate(rawKey).projectId();

    List<AssetResponse> assets =
        assetUseCase.findAllByProjectId(ProjectId.fromString(projectId)).stream()
            .map(AssetResponse::from)
            .toList();

    return ApiResponse.ok(assets);
  }

  @DeleteMapping
  public ApiResponse<AssetResponse> delete(
      @RequestHeader("X-OneAsset-Api-Key") String rawKey,
      @RequestParam(value = "key", required = true) String key) {
    String projectId = apiKeyAuthenticationUseCase.authenticate(rawKey).projectId();

    RegistryAsset asset =
        assetUseCase.deleteByKeyAndProjectId(key, ProjectId.fromString(projectId));

    return ApiResponse.ok(AssetResponse.from(asset));
  }

  private RegisterAssetCommand toCommand(
      String projectId, MultipartFile file, CreateAssetMetadataRequest metadata)
      throws IOException {
    return new RegisterAssetCommand(
        projectId,
        metadata == null ? null : metadata.key(),
        metadata == null ? null : metadata.fileName(),
        file.getInputStream(),
        file.getOriginalFilename(),
        file.getContentType(),
        file.getSize());
  }
}
