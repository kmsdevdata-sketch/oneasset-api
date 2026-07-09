package io.oneasset.adapter.outbound.user;

import io.oneasset.adapter.outbound.user.entity.UserEntity;
import io.oneasset.adapter.outbound.user.persistence.UserJpaRepository;
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
public class UserPersistenceAdapter {

  private final UserJpaRepository userJpaRepository;

  public void save(User user) {
    userJpaRepository.save(UserEntity.from(user));
  }

  @Transactional(readOnly = true)
  public Optional<User> findActiveById(UUID userId) {
    return userJpaRepository.findByIdAndStatus(userId, UserStatus.ACTIVE).map(UserEntity::toDomain);
  }

  @Transactional(readOnly = true)
  public Optional<User> findActiveByCognitoSub(String cognitoSub) {
    return userJpaRepository
        .findByCognitoSubAndStatus(cognitoSub, UserStatus.ACTIVE)
        .map(UserEntity::toDomain);
  }
}
