package io.oneasset.application.user.command;

public record CurrentUser(String cognitoSub, String email, String name) {}
