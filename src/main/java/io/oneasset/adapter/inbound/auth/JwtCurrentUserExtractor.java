package io.oneasset.adapter.inbound.auth;

import io.oneasset.application.user.command.CurrentUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtCurrentUserExtractor {

  public CurrentUser extract(Jwt jwt) {
    String cognitoSub = jwt.getSubject();
    String email = jwt.getClaimAsString("email");
    String name = jwt.getClaimAsString("name");

    if (isBlank(cognitoSub)) {
      throw new IllegalArgumentException();
    }
    if (isBlank(email)) {
      throw new IllegalArgumentException();
    }
    if (isBlank(name)) {
      throw new IllegalArgumentException();
    }
    return new CurrentUser(cognitoSub, email, name);
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
