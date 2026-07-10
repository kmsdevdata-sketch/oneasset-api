# Dashboard API

## 역할

Cognito JWT로 보호되는 Dashboard API 계약을 기록한다.

## 인증

```http
Authorization: Bearer {cognitoJwt}
```

## User API

### GET /api/me

인증된 Cognito 사용자를 OneAsset local User로 동기화한 뒤 사용자 정보를 반환한다.

Response:

```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "김민서",
    "role": "USER",
    "status": "ACTIVE"
  }
}
```

## Project API

### POST /api/projects

Request:

```json
{
  "name": "My Blog"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "My Blog",
    "slug": "my-blog",
    "createdAt": "2026-07-08T12:30:00"
  }
}
```

### GET /api/projects

인증 사용자가 속한 프로젝트 목록을 반환한다.

Response:

```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "name": "My Blog",
      "slug": "my-blog",
      "createdAt": "2026-07-08T12:30:00"
    }
  ]
}
```

### GET /api/projects/{projectId}

프로젝트 상세 정보를 반환한다.

Response:

```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "My Blog",
    "slug": "my-blog",
    "createdAt": "2026-07-08T12:30:00"
  }
}
```

## API Key API

### POST /projects/{projectId}/api-keys

API Key 원문은 이 응답에서만 반환한다.

### GET /projects/{projectId}/api-keys

목록 응답에는 raw key를 포함하지 않는다.

### DELETE /projects/{projectId}/api-keys/{apiKeyId}

API Key를 `REVOKED` 상태로 변경한다.

## Asset Browser API

### GET /projects/{projectId}/assets

프로젝트 Asset 목록을 반환한다.

### GET /projects/{projectId}/assets/{assetId}

Asset 상세와 variants를 반환한다.

### DELETE /projects/{projectId}/assets/{assetId}

Asset 삭제 결과를 반환한다.

## 업데이트 시점

- Controller, DTO, error code가 바뀔 때
- 인증 방식이나 권한 정책이 바뀔 때
