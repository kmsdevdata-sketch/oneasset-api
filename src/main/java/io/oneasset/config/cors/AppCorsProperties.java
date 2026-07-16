package io.oneasset.config.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record AppCorsProperties(
        List<String> allowedOrigins
) {
}
