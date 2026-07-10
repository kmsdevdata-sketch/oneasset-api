package io.oneasset.exception.code;

import io.oneasset.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
  INVALID_USER_AUDIT_TIME(
      "USER_INVALID_AUDIT_TIME_400",
      "Invalid user audit time",
      HttpStatus.BAD_REQUEST,
      "/errors/user/invalid-audit-time"),
  USER_NOT_ACTIVE(
      "USER_NOT_ACTIVE_409", "User is not active", HttpStatus.CONFLICT, "/errors/user/not-active");

  private final String code;
  private final String title;
  private final HttpStatus status;
  private final String type;

  UserErrorCode(String code, String title, HttpStatus status, String type) {
    this.code = code;
    this.title = title;
    this.status = status;
    this.type = type;
  }
}
