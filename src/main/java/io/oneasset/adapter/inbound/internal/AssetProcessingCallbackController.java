package io.oneasset.adapter.inbound.internal;

import io.oneasset.adapter.inbound.internal.request.RegisterAssetVariantRequest;
import io.oneasset.adapter.inbound.response.ApiResponse;
import io.oneasset.application.asset.provided.AssetProcessingCallbackUseCase;
import io.oneasset.application.token.provided.TokenAuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class AssetProcessingCallbackController {

  private final AssetProcessingCallbackUseCase assetProcessingCallbackUseCase;
  private final TokenAuthenticationUseCase tokenAuthenticationUseCase;

  @PostMapping("/assets/{assetId}/variants")
  public ApiResponse<Void> completeVariantProcessing(
      @RequestHeader("X-OneAsset-Processor-Callback-Token") String token,
      @RequestBody RegisterAssetVariantRequest request,
      @PathVariable String assetId) {

    tokenAuthenticationUseCase.authenticateProcessorCallbackToken(token);

    assetProcessingCallbackUseCase.completeVariantProcessing(assetId, request.toCommand());
    return ApiResponse.ok();
  }
}
