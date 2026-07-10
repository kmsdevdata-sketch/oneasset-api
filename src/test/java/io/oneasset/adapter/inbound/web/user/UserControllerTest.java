package io.oneasset.adapter.inbound.web.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.inbound.auth.JwtCurrentUserExtractor;
import io.oneasset.adapter.inbound.response.ApiResponse;
import io.oneasset.adapter.inbound.web.user.response.UserMeResponse;
import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.application.user.provided.UserSyncUseCase;
import io.oneasset.domain.user.model.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class UserControllerTest {

  private final UserSyncUseCase userSyncUseCase = mock(UserSyncUseCase.class);
  private final JwtCurrentUserExtractor jwtCurrentUserExtractor =
      mock(JwtCurrentUserExtractor.class);
  private final UserController userController =
      new UserController(userSyncUseCase, jwtCurrentUserExtractor);

  @Test
  void returnsSyncedCurrentUser() {
    Jwt jwt = createJwt();
    CurrentUser currentUser = new CurrentUser("cognito-sub-1", "user@example.com", "Minseo");
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    when(jwtCurrentUserExtractor.extract(jwt)).thenReturn(currentUser);
    when(userSyncUseCase.findOrCreate(currentUser)).thenReturn(user);

    ApiResponse<UserMeResponse> response = userController.me(jwt);

    assertThat(response.success()).isTrue();
    assertThat(response.data().email()).isEqualTo("user@example.com");
    assertThat(response.data().name()).isEqualTo("Minseo");
    verify(jwtCurrentUserExtractor).extract(jwt);
    verify(userSyncUseCase).findOrCreate(currentUser);
  }

  private static Jwt createJwt() {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(60))
        .subject("cognito-sub-1")
        .build();
  }
}
