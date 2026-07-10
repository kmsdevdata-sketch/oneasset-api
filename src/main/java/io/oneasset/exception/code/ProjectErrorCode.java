package io.oneasset.exception.code;

import io.oneasset.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProjectErrorCode implements ErrorCode {
  PROJECT_NOT_FOUND(
      "PROJECT_NOT_FOUND_404",
      "Project not found",
      HttpStatus.NOT_FOUND,
      "/errors/project/not-found"),
  PROJECT_ACCESS_DENIED(
      "PROJECT_ACCESS_DENIED_403",
      "Project access denied",
      HttpStatus.FORBIDDEN,
      "/errors/project/access-denied"),
  INVALID_PROJECT_AUDIT_TIME(
      "PROJECT_INVALID_AUDIT_TIME_400",
      "Invalid project audit time",
      HttpStatus.BAD_REQUEST,
      "/errors/project/invalid-audit-time"),
  DELETED_PROJECT_CANNOT_BE_CHANGED(
      "PROJECT_DELETED_CANNOT_BE_CHANGED_409",
      "Deleted project cannot be changed",
      HttpStatus.CONFLICT,
      "/errors/project/deleted-cannot-be-changed");

  private final String code;
  private final String title;
  private final HttpStatus status;
  private final String type;

  ProjectErrorCode(String code, String title, HttpStatus status, String type) {
    this.code = code;
    this.title = title;
    this.status = status;
    this.type = type;
  }
}
