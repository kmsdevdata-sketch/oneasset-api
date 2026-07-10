package io.oneasset.adapter.outbound.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.adapter.outbound.user.entity.UserEntity;
import io.oneasset.adapter.outbound.user.persistence.UserJpaRepository;
import io.oneasset.domain.user.model.User;
import io.oneasset.domain.user.vo.UserStatus;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserPersistenceAdapterTest {

  private final UserJpaRepository userJpaRepository = mock(UserJpaRepository.class);
  private final UserPersistenceAdapter adapter = new UserPersistenceAdapter(userJpaRepository);

  @Test
  void savesUserEntityConvertedFromDomain() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    when(userJpaRepository.save(any(UserEntity.class))).thenReturn(UserEntity.from(user));

    User saved = adapter.save(user);

    assertThat(saved.getCognitoSub()).isEqualTo("cognito-sub-1");
    verify(userJpaRepository).save(any(UserEntity.class));
  }

  @Test
  void findsActiveUserById() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    when(userJpaRepository.findByIdAndStatus(user.getId().value(), UserStatus.ACTIVE))
        .thenReturn(Optional.of(UserEntity.from(user)));

    Optional<User> found = adapter.findActiveById(user.getId().value());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(user.getId());
    verify(userJpaRepository).findByIdAndStatus(user.getId().value(), UserStatus.ACTIVE);
  }

  @Test
  void findsUserByCognitoSub() {
    User user = User.createFromCognito("cognito-sub-1", "user@example.com", "Minseo");
    when(userJpaRepository.findByCognitoSub("cognito-sub-1"))
        .thenReturn(Optional.of(UserEntity.from(user)));

    Optional<User> found = adapter.findByCognitoSub("cognito-sub-1");

    assertThat(found).isPresent();
    assertThat(found.get().getCognitoSub()).isEqualTo("cognito-sub-1");
    verify(userJpaRepository).findByCognitoSub("cognito-sub-1");
  }
}
