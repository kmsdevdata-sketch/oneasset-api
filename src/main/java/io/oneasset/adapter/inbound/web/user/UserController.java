package io.oneasset.adapter.inbound.web.user;

import io.oneasset.adapter.inbound.auth.JwtCurrentUserExtractor;
import io.oneasset.adapter.inbound.response.ApiResponse;
import io.oneasset.adapter.inbound.web.user.response.UserMeResponse;
import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.application.user.provided.UserSyncUseCase;
import io.oneasset.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

  private final UserSyncUseCase userSyncUseCase;
  private final JwtCurrentUserExtractor jwtCurrentUserExtractor;

  @GetMapping("/me")
  public ApiResponse<UserMeResponse> me(@AuthenticationPrincipal Jwt jwt) {
    CurrentUser currentUser = jwtCurrentUserExtractor.extract(jwt);
    User user = userSyncUseCase.findOrCreate(currentUser);
    return ApiResponse.ok(UserMeResponse.from(user));
  }
}
