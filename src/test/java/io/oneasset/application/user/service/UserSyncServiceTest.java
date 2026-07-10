package io.oneasset.application.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.application.user.command.CurrentUser;
import io.oneasset.application.user.required.UserPersistencePort;
import io.oneasset.domain.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserSyncServiceTest {

  private final UserPersistencePort userPersistencePort = mock(UserPersistencePort.class);
  private final UserSyncService userSyncService = new UserSyncService(userPersistencePort);

  @Test
  void returnsExistingUserByCognitoSub() {
    CurrentUser currentUser = new CurrentUser("cognito-sub-1", "user@example.com", "Minseo");
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    when(userPersistencePort.findByCognitoSub("cognito-sub-1")).thenReturn(Optional.of(user));

    User found = userSyncService.findOrCreate(currentUser);

    assertThat(found.getCognitoSub()).isEqualTo("cognito-sub-1");
    verify(userPersistencePort).findByCognitoSub("cognito-sub-1");
  }

  @Test
  void createsUserWhenCognitoSubDoesNotExist() {
    CurrentUser currentUser = new CurrentUser("cognito-sub-1", "user@example.com", "Minseo");
    User savedUser = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    when(userPersistencePort.findByCognitoSub("cognito-sub-1")).thenReturn(Optional.empty());
    when(userPersistencePort.save(any(User.class))).thenReturn(savedUser);

    User found = userSyncService.findOrCreate(currentUser);

    assertThat(found.getCognitoSub()).isEqualTo("cognito-sub-1");
    verify(userPersistencePort).findByCognitoSub("cognito-sub-1");
    verify(userPersistencePort).save(any(User.class));
  }
}
