package io.oneasset.domain.asset.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.oneasset.domain.asset.vo.AssetId;
import io.oneasset.domain.asset.vo.AssetStatus;
import io.oneasset.domain.project.vo.ProjectId;
import io.oneasset.domain.user.vo.UserId;
import java.time.LocalDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class AssetTest {

  @Test
  void createsUploadedAsset() {
    ProjectId projectId = ProjectId.newId();
    UserId uploadedBy = UserId.newId();

    Asset asset = Asset.create(
        projectId,
        uploadedBy,
        "profile.png",
        "image/png",
        1024,
        "oneasset-original",
        "original/project/profile.png");

    assertThat(asset.getId()).isNotNull();
    assertThat(asset.getProjectId()).isEqualTo(projectId);
    assertThat(asset.getUploadedBy()).isEqualTo(uploadedBy);
    assertThat(asset.getOriginalFileName()).isEqualTo("profile.png");
    assertThat(asset.getContentType()).isEqualTo("image/png");
    assertThat(asset.getSizeBytes()).isEqualTo(1024);
    assertThat(asset.getBucket()).isEqualTo("oneasset-original");
    assertThat(asset.getStorageKey()).isEqualTo("original/project/profile.png");
    assertThat(asset.getStatus()).isEqualTo(AssetStatus.UPLOADED);
    assertThat(asset.isReady()).isFalse();
    assertThat(asset.isDeleted()).isFalse();
  }

  @Test
  void rejectsInvalidCreateInput() {
    assertDomainFailure(() ->
        Asset.create(ProjectId.newId(), UserId.newId(), " ", "image/png", 1024, "bucket", "key"));
    assertDomainFailure(() -> Asset.create(
        ProjectId.newId(), UserId.newId(), "profile.png", "image/png", 0, "bucket", "key"));
  }

  @Test
  void reconstitutesPersistedAsset() {
    AssetId id = AssetId.newId();
    ProjectId projectId = ProjectId.newId();
    UserId uploadedBy = UserId.newId();
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 10, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2026, 7, 9, 10, 30);
    LocalDateTime deletedAt = LocalDateTime.of(2026, 7, 9, 11, 0);

    Asset asset = Asset.reconstitute(
        id,
        projectId,
        uploadedBy,
        "profile.png",
        "image/png",
        1024,
        "bucket",
        "key",
        AssetStatus.FAILED,
        createdAt,
        updatedAt,
        deletedAt);

    assertThat(asset.getId()).isEqualTo(id);
    assertThat(asset.getProjectId()).isEqualTo(projectId);
    assertThat(asset.getUploadedBy()).isEqualTo(uploadedBy);
    assertThat(asset.getCreatedAt()).isEqualTo(createdAt);
    assertThat(asset.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(asset.getDeletedAt()).isEqualTo(deletedAt);
    assertThat(asset.isDeleted()).isTrue();
  }

  @Test
  void movesUploadedAssetToReadyThroughProcessing() {
    Asset asset = createAsset();

    asset.markProcessing();
    asset.markReady();

    assertThat(asset.getStatus()).isEqualTo(AssetStatus.READY);
    assertThat(asset.isReady()).isTrue();
  }

  @Test
  void marksUploadedOrProcessingAssetAsFailed() {
    Asset uploaded = createAsset();
    Asset processing = createAsset();
    processing.markProcessing();

    uploaded.markFailed();
    processing.markFailed();

    assertThat(uploaded.getStatus()).isEqualTo(AssetStatus.FAILED);
    assertThat(processing.getStatus()).isEqualTo(AssetStatus.FAILED);
  }

  @Test
  void rejectsInvalidStateTransition() {
    Asset asset = createAsset();

    assertDomainFailure(asset::markReady);
    asset.markProcessing();
    asset.markReady();
    assertDomainFailure(asset::markFailed);
  }

  @Test
  void deletesAsset() {
    Asset asset = createAsset();
    LocalDateTime beforeDelete = asset.getUpdatedAt();

    asset.delete();

    assertThat(asset.isDeleted()).isTrue();
    assertThat(asset.getDeletedAt()).isNotNull();
    assertThat(asset.getUpdatedAt()).isAfterOrEqualTo(beforeDelete);
  }

  @Test
  void doesNotAllowDeletedAssetToChangeAgain() {
    Asset asset = createAsset();
    asset.delete();

    assertDomainFailure(asset::markProcessing);
    assertDomainFailure(asset::markFailed);
    assertDomainFailure(asset::delete);
  }

  private static Asset createAsset() {
    return Asset.create(
        ProjectId.newId(), UserId.newId(), "profile.png", "image/png", 1024, "bucket", "key");
  }

  private static void assertDomainFailure(ThrowingCallable action) {
    assertThatThrownBy(action).isInstanceOf(RuntimeException.class);
  }
}
