package io.oneasset.adapter.outbound.asset.storage;

import io.oneasset.adapter.outbound.asset.AssetStoreRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3Storage {

  private final S3Client s3Client;

  @Value("${oneasset.storage.asset-bucket}")
  private String bucket;

  public void store(AssetStoreRequest request) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(request.storageKey())
        .contentType(request.contentType())
        .contentLength(request.sizeBytes())
        .build();

    s3Client.putObject(
        putObjectRequest, RequestBody.fromInputStream(request.inputStream(), request.sizeBytes()));
  }
}
