package io.oneasset.adapter.inbound.auth;

public record CurrentUser(
        String cognitoSub,
        String email,
        String name
) {
}
