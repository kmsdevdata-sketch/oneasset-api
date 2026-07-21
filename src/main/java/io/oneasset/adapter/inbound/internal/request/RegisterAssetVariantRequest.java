package io.oneasset.adapter.inbound.internal.request;

import io.oneasset.application.asset.command.RegisterAssetVariantCommand;
import io.oneasset.domain.assetvariant.vo.AssetVariantType;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;

public record RegisterAssetVariantRequest(
    String type,
    String bucket,
    String storageKey,
    String contentType,
    long sizeBytes,
    Integer width,
    Integer height) {

  public RegisterAssetVariantCommand toCommand() {
    return new RegisterAssetVariantCommand(
        parseType(), bucket, storageKey, contentType, sizeBytes, width, height);
  }

  private AssetVariantType parseType() {
    try {
      return AssetVariantType.valueOf(type);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new BaseException(CommonErrorCode.INVALID_INPUT, "asset variant type is invalid");
    }
  }
}
