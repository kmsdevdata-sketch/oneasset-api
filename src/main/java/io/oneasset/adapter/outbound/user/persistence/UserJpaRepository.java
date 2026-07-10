package io.oneasset.adapter.outbound.user.persistence;

import io.oneasset.adapter.outbound.user.entity.UserEntity;
import io.oneasset.domain.user.vo.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByIdAndStatus(UUID id, UserStatus status);

  Optional<UserEntity> findByCognitoSub(String cognitoSub);
}
