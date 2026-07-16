# OneAsset API

OneAsset is a cloud-native asset platform for developers who want to upload images, manage asset metadata, and serve files through stable delivery URLs without building the storage and delivery pipeline themselves.

The backend has two API surfaces:

- Dashboard API: used by the OneAsset web dashboard with Cognito JWT authentication.
- Developer API: used by external applications with project-scoped API keys.

## Current Stage: MVP1

MVP1 focuses on proving the core asset upload, storage, delivery, and management flow.

Current flow:

```text
Dashboard user creates a project
-> Dashboard user issues an API key
-> Developer app uploads an asset with X-OneAsset-Api-Key
-> OneAsset stores the original file in private S3
-> OneAsset stores asset metadata in PostgreSQL
-> CloudFront serves the asset through OAC-protected S3 access
-> Developer API can query, list, and delete project-scoped assets
```

MVP1 is intentionally narrow. The goal is to prove that project-scoped API keys can drive a real asset workflow with private storage and public delivery through CloudFront.

## Asset Key Contract

OneAsset separates the user-facing asset key from the internal S3 storage key.

### User Key

The user key is the path supplied by the API caller.

Example:

```text
users/123/profile.png
```

Rules:

- Do not include a leading slash.
- Do not include empty path segments.
- Do not use `.` or `..` path segments.
- Use `/` as the logical folder separator.

### Storage Key

The storage key is the actual S3 object key.

Format:

```text
projects/{projectId}/{userKey}
```

Example:

```text
projects/8e06f161-e93d-4a17-8ae8-15f2cfeb353a/users/123/profile.png
```

The backend creates this value from the authenticated project id and the user key. Clients should not manually construct the `projects/{projectId}` prefix when uploading, querying, or deleting through key-based APIs.

### Current Response Note

In the current MVP1 response, the `key` field contains the full storage key.

Example:

```json
{
  "assetId": "asset-id",
  "key": "projects/8e06f161-e93d-4a17-8ae8-15f2cfeb353a/users/123/profile.png",
  "originalFileName": "profile.png",
  "contentType": "image/png",
  "sizeBytes": 12345,
  "status": "UPLOADED",
  "deliveryUrl": "https://dxxxxx.cloudfront.net/projects/8e06f161-e93d-4a17-8ae8-15f2cfeb353a/users/123/profile.png",
  "createdAt": "2026-07-16T14:00:00"
}
```

For UI rendering, the frontend should strip the project prefix:

```text
projects/{projectId}/users/123/profile.png
-> users/123/profile.png
```

For follow-up key-based Developer API calls, send the user key:

```text
users/123/profile.png
```

not the full storage key.

Later API cleanup may split this explicitly into `key` and `storageKey`.

## Dashboard Asset List Contract

The backend should return a flat asset list. The frontend is responsible for rendering a tree by splitting the user key with `/`.

Backend response shape:

```text
[
  asset,
  asset,
  asset
]
```

Frontend tree example:

```text
users/123/profile.png
users/123/banner.png
products/iphone/main.png
```

Render as:

```text
users
  123
    profile.png
    banner.png
products
  iphone
    main.png
```

Rationale:

- The API stays simple and flexible.
- The frontend can choose list, tree, search, or grouped views without backend changes.
- The backend remains responsible for project scoping and key validation, not presentation structure.

Dashboard APIs should use Cognito JWT authentication. The dashboard must not send raw project API keys from the browser.

## Developer API

Developer API requests use:

```text
X-OneAsset-Api-Key: {raw_api_key}
```

Current asset endpoints:

```text
POST   /v1/assets
GET    /v1/assets?key={userKey}
GET    /v1/assets
DELETE /v1/assets?key={userKey}
```

Upload uses multipart form data:

```text
file: File
key: users/123/profile.png
fileName: profile.png optional
```

Delete behavior:

```text
DELETE /v1/assets?key={userKey}
-> deletes the S3 object
-> records deletedAt in PostgreSQL
-> active detail/list queries no longer return the asset
```

CloudFront cache may continue serving a deleted object briefly until cache expiry. Explicit invalidation is a later operational feature.

## Storage And Delivery

Current storage and delivery model:

```text
S3 bucket: private origin storage
CloudFront: public delivery entrypoint
OAC: allows only the CloudFront distribution to read S3 objects
```

Direct S3 object access should be blocked. CloudFront URL access should work.

Runtime environment variables:

```text
AWS_PROFILE=oneasset-dev
AWS_REGION=ap-northeast-2
ONEASSET_ASSET_BUCKET=oneasset
ONEASSET_DELIVERY_BASE_URL=https://dxxxxx.cloudfront.net
```

## Architecture Direction

The backend follows a lightweight hexagonal structure:

```text
adapter -> application -> domain
```

Domain models hold business rules. Application services coordinate use cases and transaction boundaries. Adapters handle HTTP, persistence, and external systems such as S3, CloudFront-facing delivery configuration, Cognito, and later SQS/Lambda.

## MVP2 Direction

MVP2 turns the basic flow into something usable by a small team or early user.

Planned focus:

- Dashboard asset management APIs with Cognito JWT and project membership checks.
- Explicit `key` and `storageKey` response separation.
- SQS/Lambda processing pipeline for variants, resizing, and failure handling.
- Processing status visibility and retry-friendly state.
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

## Local Development

Useful commands:

```bash
./gradlew compileJava
./gradlew test
./gradlew spotlessApply
./gradlew spotlessCheck
```

Docker local run:

```bash
docker compose up --build
```
