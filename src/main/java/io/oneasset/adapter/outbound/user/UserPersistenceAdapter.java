package io.oneasset.adapter.outbound.user;

import io.oneasset.adapter.outbound.user.entity.UserEntity;
import io.oneasset.adapter.outbound.user.persistence.UserJpaRepository;
import io.oneasset.application.user.required.UserPersistencePort;
import io.oneasset.domain.user.model.User;
import io.oneasset.domain.user.vo.UserStatus;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    UserEntity savedEntity = userJpaRepository.save(UserEntity.from(user));
    return savedEntity.toDomain();
  }

  @Transactional(readOnly = true)
  public Optional<User> findActiveById(UUID userId) {
    return userJpaRepository.findByIdAndStatus(userId, UserStatus.ACTIVE).map(UserEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public Optional<User> findByCognitoSub(String cognitoSub) {
    return userJpaRepository.findByCognitoSub(cognitoSub).map(UserEntity::toDomain);
  }
}
