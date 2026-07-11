package io.oneasset.application.apikey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.application.apikey.command.CreateApiKeyCommand;
import io.oneasset.application.apikey.required.ApiKeyPersistencePort;
import io.oneasset.application.apikey.result.CreatedApiKey;
import io.oneasset.application.project.required.ProjectPersistencePort;
import io.oneasset.domain.apikey.engine.ApiKeyGenerator;
import io.oneasset.domain.apikey.engine.GenerateApiKey;
import io.oneasset.domain.apikey.model.ApiKey;
import io.oneasset.domain.project.model.Project;
import io.oneasset.domain.projectmember.model.ProjectMember;
import io.oneasset.domain.user.vo.UserId;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.ProjectErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ApiKeyServiceTest {

  private final ApiKeyPersistencePort apiKeyPersistencePort = mock(ApiKeyPersistencePort.class);
  private final ProjectPersistencePort projectPersistencePort = mock(ProjectPersistencePort.class);
  private final ApiKeyGenerator apiKeyGenerator = mock(ApiKeyGenerator.class);
  private final ApiKeyService apiKeyService =
      new ApiKeyService(apiKeyPersistencePort, projectPersistencePort, apiKeyGenerator);

  @Test
  void createsApiKeyAfterProjectMembershipCheck() {
    UserId userId = UserId.newId();
    Project project = Project.create("My Blog", "my-blog");
    ProjectMember member = ProjectMember.createOwner(project.getId(), userId);
    GenerateApiKey generatedApiKey =
        GenerateApiKey.from("oa_live_raw-key", "hash-value", "oa_live_raw-key");
    when(projectPersistencePort.findMember(project.getId(), userId))
        .thenReturn(Optional.of(member));
    when(apiKeyGenerator.generate()).thenReturn(generatedApiKey);
    when(apiKeyPersistencePort.save(any(ApiKey.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CreatedApiKey createdApiKey = apiKeyService.create(
        userId, new CreateApiKeyCommand("Production", project.getId().toString()));

    assertThat(createdApiKey.rawKey()).isEqualTo("oa_live_raw-key");
    assertThat(createdApiKey.apiKey().getName()).isEqualTo("Production");
    assertThat(createdApiKey.apiKey().getProjectId()).isEqualTo(project.getId());
    assertThat(createdApiKey.apiKey().getPrefix().value()).isEqualTo("oa_live_raw-key");
    assertThat(createdApiKey.apiKey().getHash().value()).isEqualTo("hash-value");
    verify(projectPersistencePort).findMember(project.getId(), userId);
    verify(apiKeyGenerator).generate();

    ArgumentCaptor<ApiKey> apiKeyCaptor = ArgumentCaptor.forClass(ApiKey.class);
    verify(apiKeyPersistencePort).save(apiKeyCaptor.capture());
    assertThat(apiKeyCaptor.getValue().getHash().value()).isEqualTo("hash-value");
  }

  @Test
  void throwsAccessDeniedWhenUserIsNotProjectMember() {
    UserId userId = UserId.newId();
    Project project = Project.create("My Blog", "my-blog");
    when(projectPersistencePort.findMember(project.getId(), userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> apiKeyService.create(
            userId, new CreateApiKeyCommand("Production", project.getId().toString())))
        .isInstanceOf(BaseException.class)
        .extracting("errorCode")
        .isEqualTo(ProjectErrorCode.PROJECT_ACCESS_DENIED);
  }
}
