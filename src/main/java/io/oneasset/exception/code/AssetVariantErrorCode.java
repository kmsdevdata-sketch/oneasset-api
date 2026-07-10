package io.oneasset.exception.code;

import io.oneasset.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AssetVariantErrorCode implements ErrorCode {
  INVALID_ASSET_VARIANT_SIZE(
      "ASSET_VARIANT_INVALID_SIZE_400",
      "Invalid asset variant size",
      HttpStatus.BAD_REQUEST,
      "/errors/asset-variant/invalid-size");

  private final String code;
  private final String title;
  private final HttpStatus status;
  private final String type;

  AssetVariantErrorCode(String code, String title, HttpStatus status, String type) {
    this.code = code;
    this.title = title;
    this.status = status;
    this.type = type;
  }
}
