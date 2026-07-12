# OneAsset API

OneAsset is a cloud-native asset platform for developers who want to upload images, process them, and serve them through stable delivery URLs without building the storage and image-processing pipeline themselves.

The backend provides two API surfaces:

- Dashboard API: used by the OneAsset web dashboard with Cognito JWT authentication.
- Developer API: used by external applications with project-scoped API keys.

## Current Stage: MVP1

MVP1 focuses on proving the core asset delivery flow.

The intended flow is:

```text
Dashboard user creates a project
-> Dashboard user issues an API key
-> Developer app calls OneAsset with X-OneAsset-Api-Key
-> OneAsset stores original asset metadata and file
-> Image variants are processed
-> OneAsset returns a delivery URL
```

MVP1 is intentionally narrow. The goal is not to build every dashboard feature or billing feature, but to prove that project-scoped API keys can drive a real asset upload and delivery workflow.

## MVP2 Direction

MVP2 turns the basic flow into something usable by a small team or early user.

Planned focus:

- Asset list and detail views for the dashboard.
- More complete Developer API responses for status, lookup, and deletion.
- Processing failure visibility and retry-friendly state.
- API key usage tracking and safer operational behavior.
- Basic usage statistics per project.

## MVP3 Direction

MVP3 moves toward service readiness.

Planned focus:

- Stable deployment and environment separation.
- More formal observability with logs, metrics, and alerts.
- Rate limiting and abuse protection.
- Usage-based limits or billing groundwork.
- Better documentation for external developers.

## Architecture Direction

The backend follows a lightweight hexagonal structure:

```text
adapter -> application -> domain
```

Domain models hold business rules. Application services coordinate use cases and transaction boundaries. Adapters handle HTTP, persistence, and external systems such as S3 or Cognito.

## Local Development

This project is a Spring Boot API using Gradle.

Useful commands:

```bash
./gradlew compileJava
./gradlew test
./gradlew spotlessApply
```
