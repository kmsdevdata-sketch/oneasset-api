package io.oneasset.adapter.inbound.response;

import io.oneasset.exception.BaseException;
import io.oneasset.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ApiResponse<Void>> handleAllExceptions(
      BaseException e, HttpServletRequest request) {
    ErrorCode errorCode = e.getErrorCode();

    String instance = request.getRequestURI();

    log.warn(
        "Business exception. code={} ,title={} ,status={} ,type={}",
        errorCode.getCode(),
        errorCode.getTitle(),
        errorCode.getStatus(),
        errorCode.getType());
    return ResponseEntity.status(errorCode.getStatus())
        .body(ApiResponse.fail(ErrorResult.of(errorCode, instance)));
  }
}
