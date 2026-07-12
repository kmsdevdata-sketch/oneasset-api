package io.oneasset.exception.code;

import io.oneasset.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCode {
  REQUIRED_JWT_CLAIM_MISSING(
      "AUTH_REQUIRED_JWT_CLAIM_MISSING_401",
      "Required JWT claim is missing",
      HttpStatus.UNAUTHORIZED,
      "/errors/auth/required-jwt-claim-missing"),
  INVALID_API_KEY(
      "AUTH_INVALID_API_KEY_401",
      "Invalid API key",
      HttpStatus.UNAUTHORIZED,
      "/errors/auth/invalid-api-key");

  private final String code;
  private final String title;
  private final HttpStatus status;
  private final String type;

  AuthErrorCode(String code, String title, HttpStatus status, String type) {
    this.code = code;
    this.title = title;
    this.status = status;
    this.type = type;
  }
}
