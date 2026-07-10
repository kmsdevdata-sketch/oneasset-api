package io.oneasset.application.user.service;

import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.application.user.provided.UserSyncUseCase;
import io.oneasset.application.user.required.UserPersistencePort;
import io.oneasset.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSyncService implements UserSyncUseCase {

  private final UserPersistencePort userPersistencePort;

  @Override
  @Transactional
  public User findOrCreate(CurrentUser currentUser) {
    return userPersistencePort
        .findByCognitoSub(currentUser.cognitoSub())
        .orElseGet(() -> userPersistencePort.save(User.createFromCognito(
            currentUser.cognitoSub(), currentUser.email(), currentUser.name())));
  }
}
