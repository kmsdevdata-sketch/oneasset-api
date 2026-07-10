package io.oneasset.domain.apikey.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.oneasset.domain.apikey.vo.ApiKeyHash;
import io.oneasset.domain.apikey.vo.ApiKeyId;
import io.oneasset.domain.apikey.vo.ApiKeyPrefix;
import io.oneasset.domain.apikey.vo.ApiKeyStatus;
import io.oneasset.domain.project.vo.ProjectId;
import java.time.LocalDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class ApiKeyTest {

  @Test
  void createsApiKeyWithStoredPrefixAndHashOnly() {
    ProjectId projectId = ProjectId.newId();
    ApiKeyPrefix prefix = ApiKeyPrefix.of("oa_live_abcd");
    ApiKeyHash hash = ApiKeyHash.of("hashed-value");

    ApiKey apiKey = ApiKey.create(projectId, "Production", prefix, hash);

    assertThat(apiKey.getId()).isNotNull();
    assertThat(apiKey.getProjectId()).isEqualTo(projectId);
    assertThat(apiKey.getName()).isEqualTo("Production");
    assertThat(apiKey.getPrefix()).isEqualTo(prefix);
    assertThat(apiKey.getHash()).isEqualTo(hash);
    assertThat(apiKey.getStatus()).isEqualTo(ApiKeyStatus.ACTIVE);
    assertThat(apiKey.getCreatedAt()).isNotNull();
    assertThat(apiKey.getLastUsedAt()).isNull();
    assertThat(apiKey.getRevokedAt()).isNull();
    assertThat(apiKey.isActive()).isTrue();
  }

  @Test
  void rejectsInvalidCreateInput() {
    assertDomainFailure(() -> ApiKey.create(
        ProjectId.newId(), " ", ApiKeyPrefix.of("oa_live_abcd"), ApiKeyHash.of("hash")));
  }

  @Test
  void reconstitutesPersistedApiKey() {
    ApiKeyId id = ApiKeyId.newId();
    ProjectId projectId = ProjectId.newId();
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 10, 0);
    LocalDateTime lastUsedAt = LocalDateTime.of(2026, 7, 9, 10, 30);
    LocalDateTime revokedAt = LocalDateTime.of(2026, 7, 9, 11, 0);

    ApiKey apiKey = ApiKey.reconstitute(
        id,
        projectId,
        "Production",
        ApiKeyPrefix.of("oa_live_abcd"),
        ApiKeyHash.of("hash"),
        ApiKeyStatus.REVOKED,
        createdAt,
        lastUsedAt,
        revokedAt);

    assertThat(apiKey.getId()).isEqualTo(id);
    assertThat(apiKey.getProjectId()).isEqualTo(projectId);
    assertThat(apiKey.getCreatedAt()).isEqualTo(createdAt);
    assertThat(apiKey.getLastUsedAt()).isEqualTo(lastUsedAt);
    assertThat(apiKey.getRevokedAt()).isEqualTo(revokedAt);
    assertThat(apiKey.isRevoked()).isTrue();
  }

  @Test
  void rejectsInvalidPersistedState() {
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 10, 0);
    LocalDateTime earlier = LocalDateTime.of(2026, 7, 9, 9, 0);
    LocalDateTime later = LocalDateTime.of(2026, 7, 9, 11, 0);

    assertDomainFailure(() -> reconstituteWith(ApiKeyStatus.ACTIVE, createdAt, earlier, null));
    assertDomainFailure(() -> reconstituteWith(ApiKeyStatus.ACTIVE, createdAt, null, earlier));
    assertDomainFailure(() -> reconstituteWith(ApiKeyStatus.ACTIVE, createdAt, null, later));
    assertDomainFailure(() -> reconstituteWith(ApiKeyStatus.REVOKED, createdAt, null, null));
  }

  @Test
  void renamesActiveApiKey() {
    ApiKey apiKey = createApiKey();

    apiKey.rename("Development");

    assertThat(apiKey.getName()).isEqualTo("Development");
  }

  @Test
  void marksActiveApiKeyAsUsed() {
    ApiKey apiKey = createApiKey();

    apiKey.markUsed();

    assertThat(apiKey.getLastUsedAt()).isNotNull();
  }

  @Test
  void revokesActiveApiKey() {
    ApiKey apiKey = createApiKey();

    apiKey.revoke();

    assertThat(apiKey.getStatus()).isEqualTo(ApiKeyStatus.REVOKED);
    assertThat(apiKey.getRevokedAt()).isNotNull();
    assertThat(apiKey.isActive()).isFalse();
    assertThat(apiKey.isRevoked()).isTrue();
  }

  @Test
  void doesNotAllowRevokedApiKeyToChangeAgain() {
    ApiKey apiKey = createApiKey();
    apiKey.revoke();

    assertDomainFailure(() -> apiKey.rename("Development"));
    assertDomainFailure(apiKey::markUsed);
    assertDomainFailure(apiKey::revoke);
  }

  private static ApiKey createApiKey() {
    return ApiKey.create(
        ProjectId.newId(), "Production", ApiKeyPrefix.of("oa_live_abcd"), ApiKeyHash.of("hash"));
  }

  private static ApiKey reconstituteWith(
      ApiKeyStatus status,
      LocalDateTime createdAt,
      LocalDateTime lastUsedAt,
      LocalDateTime revokedAt) {
    return ApiKey.reconstitute(
        ApiKeyId.newId(),
        ProjectId.newId(),
        "Production",
        ApiKeyPrefix.of("oa_live_abcd"),
        ApiKeyHash.of("hash"),
        status,
        createdAt,
        lastUsedAt,
        revokedAt);
  }

  private static void assertDomainFailure(ThrowingCallable action) {
    assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
  }
}
