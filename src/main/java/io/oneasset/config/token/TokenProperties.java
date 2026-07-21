package io.oneasset.config.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oneasset.processor")
public record TokenProperties(String callbackToken) {}
