package io.oneasset.application.asset.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.oneasset.application.asset.command.RegisterAssetCommand;
import io.oneasset.application.asset.command.StoreAssetCommand;
import io.oneasset.application.asset.required.AssetPersistencePort;
import io.oneasset.application.asset.required.AssetStoragePort;
import io.oneasset.application.asset.result.RegistryAsset;
import io.oneasset.domain.asset.model.Asset;
import io.oneasset.domain.asset.vo.AssetStatus;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.exception.BaseException;
import io.oneasset.exception.code.CommonErrorCode;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

class AssetServiceTest {

  private final AssetPersistencePort assetPersistencePort = mock(AssetPersistencePort.class);
  private final AssetStoragePort assetStoragePort = mock(AssetStoragePort.class);
  private final AssetService assetService = new AssetService(
      assetPersistencePort, assetStoragePort, "test-bucket", "https://cdn.oneasset.test/");

  @Test
  void registersDeveloperAssetWithProjectScopedRequestedKey() {
    ProjectId projectId = ProjectId.newId();
    when(assetPersistencePort.register(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RegistryAsset asset = assetService.register(new RegisterAssetCommand(
        projectId.toString(),
        "users/123/profile.png",
        null,
        inputStream(),
        "avatar.png",
        "image/png",
        1024));

    assertThat(asset.key()).isEqualTo("projects/" + projectId + "/users/123/profile.png");
    assertThat(asset.originalFileName()).isEqualTo("avatar.png");
    assertThat(asset.status()).isEqualTo(AssetStatus.UPLOADED.name());
    assertThat(asset.deliveryUrl())
        .isEqualTo("https://cdn.oneasset.test/projects/" + projectId + "/users/123/profile.png");

    ArgumentCaptor<StoreAssetCommand> storeCommandCaptor =
        ArgumentCaptor.forClass(StoreAssetCommand.class);
    ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
    InOrder inOrder = inOrder(assetStoragePort, assetPersistencePort);
    inOrder.verify(assetStoragePort).store(storeCommandCaptor.capture());
    inOrder.verify(assetPersistencePort).register(assetCaptor.capture());

    StoreAssetCommand storeCommand = storeCommandCaptor.getValue();
    assertThat(storeCommand.storageKey())
        .isEqualTo("projects/" + projectId + "/users/123/profile.png");
    assertThat(storeCommand.contentType()).isEqualTo("image/png");
    assertThat(storeCommand.sizeBytes()).isEqualTo(1024);

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
        projectId.toString(), null, null, inputStream(), "profile.png", "image/png", 2048));

    assertThat(asset.key()).startsWith("projects/" + projectId + "/assets/");
    assertThat(asset.key()).endsWith(".png");
    assertThat(asset.originalFileName()).isEqualTo("profile.png");
  }

  @Test
  void findsAssetWithProjectScopedRequestedKey() {
    ProjectId projectId = ProjectId.newId();
    String storageKey = "projects/" + projectId + "/users/123/profile.png";
    Asset savedAsset =
        Asset.create(projectId, null, "profile.png", "image/png", 2048, "test-bucket", storageKey);
    when(assetPersistencePort.findActiveByStorageKeyAndProjectId(storageKey, projectId))
        .thenReturn(Optional.of(savedAsset));

    RegistryAsset asset = assetService.findByKeyAndProjectId("users/123/profile.png", projectId);

    assertThat(asset.key()).isEqualTo(storageKey);
    assertThat(asset.deliveryUrl()).isEqualTo("https://cdn.oneasset.test/" + storageKey);
  }

  @Test
  void findsAllAssetsByProjectIdWithDeliveryUrls() {
    ProjectId projectId = ProjectId.newId();
    Asset firstAsset = Asset.create(
        projectId,
        null,
        "profile.png",
        "image/png",
        2048,
        "test-bucket",
        "projects/" + projectId + "/users/123/profile.png");
    Asset secondAsset = Asset.create(
        projectId,
        null,
        "banner.jpg",
        "image/jpeg",
        4096,
        "test-bucket",
        "projects/" + projectId + "/users/123/banner.jpg");
    when(assetPersistencePort.findAllActiveByProjectId(projectId))
        .thenReturn(List.of(firstAsset, secondAsset));

    List<RegistryAsset> assets = assetService.findAllByProjectId(projectId);

    assertThat(assets).hasSize(2);
    assertThat(assets.getFirst().key()).isEqualTo(firstAsset.getStorageKey());
    assertThat(assets.getFirst().deliveryUrl())
        .isEqualTo("https://cdn.oneasset.test/" + firstAsset.getStorageKey());
    assertThat(assets.getLast().key()).isEqualTo(secondAsset.getStorageKey());
    assertThat(assets.getLast().deliveryUrl())
        .isEqualTo("https://cdn.oneasset.test/" + secondAsset.getStorageKey());
  }

  @Test
  void softDeletesAssetWithProjectScopedRequestedKey() {
    ProjectId projectId = ProjectId.newId();
    String storageKey = "projects/" + projectId + "/users/123/profile.png";
    Asset savedAsset =
        Asset.create(projectId, null, "profile.png", "image/png", 2048, "test-bucket", storageKey);
    when(assetPersistencePort.findActiveByStorageKeyAndProjectId(storageKey, projectId))
        .thenReturn(Optional.of(savedAsset));
    when(assetPersistencePort.save(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RegistryAsset asset = assetService.deleteByKeyAndProjectId("users/123/profile.png", projectId);

    assertThat(savedAsset.isDeleted()).isTrue();
    assertThat(asset.key()).isEqualTo(storageKey);
    assertThat(asset.deliveryUrl()).isEqualTo("https://cdn.oneasset.test/" + storageKey);
  }

  @Test
  void rejectsUnsafeRequestedKeySegments() {
    ProjectId projectId = ProjectId.newId();

    assertThatThrownBy(() -> assetService.register(new RegisterAssetCommand(
            projectId.toString(),
            "../profile.png",
            null,
            inputStream(),
            "profile.png",
            "image/png",
            1024)))
        .isInstanceOf(BaseException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
  }

  @Test
  void rejectsUnsafeLookupKeySegments() {
    ProjectId projectId = ProjectId.newId();

    assertThatThrownBy(() -> assetService.findByKeyAndProjectId("../profile.png", projectId))
        .isInstanceOf(BaseException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
  }

  private ByteArrayInputStream inputStream() {
    return new ByteArrayInputStream("asset".getBytes());
  }
}
