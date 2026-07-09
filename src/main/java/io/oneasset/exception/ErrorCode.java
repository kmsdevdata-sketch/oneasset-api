package io.oneasset.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
  String getCode();

  String getTitle();

  HttpStatus getStatus();

  String getType();
}
