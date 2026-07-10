package io.oneasset.application.user.provided;

import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.domain.user.model.User;

public interface UserSyncUseCase {
  User findOrCreate(CurrentUser currentUser);
}
