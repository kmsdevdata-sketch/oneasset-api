package io.oneasset.exception.code;

import io.oneasset.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AssetErrorCode implements ErrorCode {
  INVALID_ASSET_STATUS_TRANSITION(
      "ASSET_INVALID_STATUS_TRANSITION_409",
      "Invalid asset status transition",
      HttpStatus.CONFLICT,
      "/errors/asset/invalid-status-transition"),
  DELETED_ASSET_CANNOT_BE_CHANGED(
      "ASSET_DELETED_CANNOT_BE_CHANGED_409",
      "Deleted asset cannot be changed",
      HttpStatus.CONFLICT,
      "/errors/asset/deleted-cannot-be-changed"),
  INVALID_ASSET_AUDIT_TIME(
      "ASSET_INVALID_AUDIT_TIME_400",
      "Invalid asset audit time",
      HttpStatus.BAD_REQUEST,
      "/errors/asset/invalid-audit-time"),
  INVALID_ASSET_SIZE(
      "ASSET_INVALID_SIZE_400",
      "Invalid asset size",
      HttpStatus.BAD_REQUEST,
      "/errors/asset/invalid-size");

  private final String code;
  private final String title;
  private final HttpStatus status;
  private final String type;

  AssetErrorCode(String code, String title, HttpStatus status, String type) {
    this.code = code;
    this.title = title;
    this.status = status;
    this.type = type;
  }
}
