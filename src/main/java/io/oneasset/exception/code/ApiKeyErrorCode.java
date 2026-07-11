package io.oneasset.exception.code;

import io.oneasset.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiKeyErrorCode implements ErrorCode {
  API_KEY_NOT_FOUND(
      "API_KEY_NOT_FOUND_404",
      "API key not found",
      HttpStatus.NOT_FOUND,
      "/errors/api-key/not-found"),
  REVOKED_API_KEY_CANNOT_BE_CHANGED(
      "API_KEY_REVOKED_CANNOT_BE_CHANGED_409",
      "Revoked API key cannot be changed",
      HttpStatus.CONFLICT,
      "/errors/api-key/revoked-cannot-be-changed"),
  INVALID_API_KEY_AUDIT_TIME(
      "API_KEY_INVALID_AUDIT_TIME_400",
      "Invalid API key audit time",
      HttpStatus.BAD_REQUEST,
      "/errors/api-key/invalid-audit-time"),
  INVALID_API_KEY_STATUS(
      "API_KEY_INVALID_STATUS_400",
      "Invalid API key status",
      HttpStatus.BAD_REQUEST,
      "/errors/api-key/invalid-status");

  private final String code;
  private final String title;
  private final HttpStatus status;
  private final String type;

  ApiKeyErrorCode(String code, String title, HttpStatus status, String type) {
    this.code = code;
    this.title = title;
    this.status = status;
    this.type = type;
  }
}
