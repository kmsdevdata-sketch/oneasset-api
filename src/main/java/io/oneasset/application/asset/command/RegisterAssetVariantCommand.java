package io.oneasset.application.asset.command;

import io.oneasset.domain.assetvariant.vo.AssetVariantType;

public record RegisterAssetVariantCommand(
    AssetVariantType type,
    String bucket,
    String storageKey,
    String contentType,
    long sizeBytes,
    Integer width,
    Integer height) {}
