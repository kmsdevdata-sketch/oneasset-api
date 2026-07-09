package io.oneasset.adapter.inbound.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.oneasset.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResult(
        String code,
        String type,
        String title,
        int status,
        String detail,
        String instance
) {
    public static ErrorResult of(
            ErrorCode errorCode,
            String detail,
            String instance
    ) {
        return new ErrorResult(
                errorCode.getCode(),
                errorCode.getType(),
                errorCode.getTitle(),
                errorCode.getStatus().value(),
                detail,
                instance
        );
    }
}
