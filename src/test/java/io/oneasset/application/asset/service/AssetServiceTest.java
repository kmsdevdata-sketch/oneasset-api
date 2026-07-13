package io.oneasset.application.asset.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.oneasset.application.asset.command.RegisterAssetCommand;
import io.oneasset.application.asset.required.AssetPersistencePort;
import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.asset.vo.AssetStatus;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AssetServiceTest {

  private final AssetPersistencePort assetPersistencePort = mock(AssetPersistencePort.class);
  private final AssetService assetService = new AssetService(assetPersistencePort, "test-bucket");

  @Test
  void registersDeveloperAssetWithProjectScopedRequestedKey() {
    ProjectId projectId = ProjectId.newId();
    when(assetPersistencePort.register(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RegistryAsset asset = assetService.register(new RegisterAssetCommand(
        projectId.toString(), "users/123/profile.png", null, "avatar.png", "image/png", 1024));

    assertThat(asset.key()).isEqualTo("projects/" + projectId + "/users/123/profile.png");
    assertThat(asset.originalFileName()).isEqualTo("avatar.png");
    assertThat(asset.status()).isEqualTo(AssetStatus.UPLOADED.name());
    assertThat(asset.deliveryUrl()).isNull();

    ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
    verify(assetPersistencePort).register(assetCaptor.capture());
    Asset savedAsset = assetCaptor.getValue();
    assertThat(savedAsset.getUploadedBy()).isNull();
    assertThat(savedAsset.getBucket()).isEqualTo("test-bucket");
  }

  @Test
  void generatesStorageKeyWhenClientDoesNotProvideKey() {
    ProjectId projectId = ProjectId.newId();
    when(assetPersistencePort.register(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RegistryAsset asset = assetService.register(new RegisterAssetCommand(
        projectId.toString(), null, null, "profile.png", "image/png", 2048));

    assertThat(asset.key()).startsWith("projects/" + projectId + "/assets/");
    assertThat(asset.key()).endsWith(".png");
    assertThat(asset.originalFileName()).isEqualTo("profile.png");
  }

  @Test
  void rejectsUnsafeRequestedKeySegments() {
    ProjectId projectId = ProjectId.newId();

    assertThatThrownBy(() -> assetService.register(new RegisterAssetCommand(
            projectId.toString(), "../profile.png", null, "profile.png", "image/png", 1024)))
        .isInstanceOf(BaseException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
  }
}
