package io.oneasset.adapter.inbound.web.user.response;

import io.oneasset.domain.user.model.User;
import io.oneasset.domain.user.vo.UserId;
import io.oneasset.domain.user.vo.UserRole;
import io.oneasset.domain.user.vo.UserStatus;

public record UserMeResponse(
    UserId id, String email, String name, UserRole role, UserStatus status) {
  public static UserMeResponse from(User user) {
    return new UserMeResponse(
        user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getStatus());
  }
}
