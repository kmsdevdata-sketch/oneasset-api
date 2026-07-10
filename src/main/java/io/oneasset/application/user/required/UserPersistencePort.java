package io.oneasset.application.user.required;

import io.oneasset.domain.user.model.User;
import java.util.Optional;

public interface UserPersistencePort {

  User save(User user);

  Optional<User> findByCognitoSub(String cognitoSub);
}
