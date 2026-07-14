package io.oneasset.adapter.outbound.asset.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.oneasset.adapter.outbound.asset.AssetStoreRequest;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class S3StorageTest {

  private final S3Client s3Client = mock(S3Client.class);
  private final S3Storage s3Storage = new S3Storage(s3Client);

  @Test
  void storesObjectWithBucketKeyAndContentMetadata() {
    ReflectionTestUtils.setField(s3Storage, "bucket", "test-bucket");
    AssetStoreRequest request = new AssetStoreRequest(
        new ByteArrayInputStream("asset".getBytes()),
        "projects/project-id/users/123/profile.png",
        "image/png",
        5);

    s3Storage.store(request);

    ArgumentCaptor<PutObjectRequest> requestCaptor =
        ArgumentCaptor.forClass(PutObjectRequest.class);
    verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

    PutObjectRequest putObjectRequest = requestCaptor.getValue();
    assertThat(putObjectRequest.bucket()).isEqualTo("test-bucket");
    assertThat(putObjectRequest.key()).isEqualTo("projects/project-id/users/123/profile.png");
    assertThat(putObjectRequest.contentType()).isEqualTo("image/png");
    assertThat(putObjectRequest.contentLength()).isEqualTo(5);
  }
}
