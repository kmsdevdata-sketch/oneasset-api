package io.oneasset.adapter.inbound.v1.developer;

import io.oneasset.adapter.inbound.response.ApiResponse;
import io.oneasset.adapter.inbound.v1.developer.request.CreateAssetMetadataRequest;
import io.oneasset.adapter.inbound.v1.developer.response.AssetResponse;
import io.oneasset.application.apikey.provided.ApiKeyAuthenticationUseCase;
import io.oneasset.application.asset.command.RegisterAssetCommand;
import io.oneasset.application.asset.provided.AssetRegisterUseCase;
import io.oneasset.application.asset.result.RegistryAsset;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/assets")
public class DeveloperAssetController {

  private final ApiKeyAuthenticationUseCase apiKeyAuthenticationUseCase;
  private final AssetRegisterUseCase assetRegisterUseCase;

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
