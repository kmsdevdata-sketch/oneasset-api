package io.oneasset.application.token.service;

import io.oneasset.application.token.provided.TokenAuthenticationUseCase;
import io.oneasset.config.token.TokenProperties;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService implements TokenAuthenticationUseCase {

  private final TokenProperties tokenProperties;

  @Override
  public void authenticateProcessorCallbackToken(String token) {
    if (token == null || token.isBlank()) {
      throw new BaseException(AuthErrorCode.INVALID_PROCESSOR_CALLBACK_TOKEN);
    }

    verifyProcessorCallbackToken(token);
  }

  private void verifyProcessorCallbackToken(String token) {
    if (!token.equals(tokenProperties.callbackToken())) {
      throw new BaseException(AuthErrorCode.INVALID_PROCESSOR_CALLBACK_TOKEN);
    }
  }
}
