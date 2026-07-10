package io.oneasset.adapter.inbound.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.AuthErrorCode;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class JwtCurrentUserExtractorTest {

  private final JwtCurrentUserExtractor extractor = new JwtCurrentUserExtractor();

  @Test
  void extractsCurrentUserFromJwtClaims() {
    Jwt jwt = createJwt("cognito-sub-1", "user@example.com", "Minseo");

    CurrentUser currentUser = extractor.extract(jwt);

    assertThat(currentUser.cognitoSub()).isEqualTo("cognito-sub-1");
    assertThat(currentUser.email()).isEqualTo("user@example.com");
    assertThat(currentUser.name()).isEqualTo("Minseo");
  }

  @Test
  void throwsExceptionWhenRequiredClaimIsMissing() {
    Jwt jwt = createJwt("cognito-sub-1", null, "Minseo");

    assertThatThrownBy(() -> extractor.extract(jwt))
        .isInstanceOf(BaseException.class)
        .extracting("errorCode")
        .isEqualTo(AuthErrorCode.REQUIRED_JWT_CLAIM_MISSING);
  }

  private static Jwt createJwt(String subject, String email, String name) {
    Jwt.Builder builder = Jwt.withTokenValue("token")
        .header("alg", "none")
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(60))
        .subject(subject)
        .claim("name", name);

    if (email != null) {
      builder.claim("email", email);
    }

    return builder.build();
  }
}
