package io.oneasset.adapter.inbound.auth;

import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.AuthErrorCode;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtCurrentUserExtractor {

  public CurrentUser extract(Jwt jwt) {
    String cognitoSub = jwt.getSubject();
    String email = jwt.getClaimAsString("email");
    String name = jwt.getClaimAsString("name");

    if (isBlank(cognitoSub)) {
      throw new BaseException(AuthErrorCode.REQUIRED_JWT_CLAIM_MISSING, "sub must not be blank");
    }
    if (isBlank(email)) {
      throw new BaseException(AuthErrorCode.REQUIRED_JWT_CLAIM_MISSING, "email must not be blank");
    }
    if (isBlank(name)) {
      throw new BaseException(AuthErrorCode.REQUIRED_JWT_CLAIM_MISSING, "name must not be blank");
    }
    return new CurrentUser(cognitoSub, email, name);
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
