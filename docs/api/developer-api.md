# Developer API

## 역할

프로젝트별 API Key로 호출하는 외부 개발자 API 계약을 기록한다.

## 인증

```http
X-OneAsset-Api-Key: oa_live_xxxxxxxxxxxxxxxxx
```

## POST /v1/assets

Request:

```http
Content-Type: multipart/form-data

file: avatar.png
key: users/123/profile.png
```

`key`는 optional이다. 없으면 서버가 storage key를 생성한다.

Response:

```json
{
  "success": true,
  "data": {
    "assetId": "uuid",
    "key": "users/123/profile.png",
    "status": "PROCESSING"
  }
}
```

## GET /v1/assets/{key}/status

READY response:

```json
{
  "success": true,
  "data": {
    "status": "READY",
    "deliveryUrl": "https://cdn.oneasset.dev/users/123/profile.webp",
    "contentType": "image/webp",
    "sizeBytes": 125432
  }
}
```

PROCESSING response:

```json
{
  "success": true,
  "data": {
    "status": "PROCESSING"
  }
}
```

## DELETE /v1/assets/{key}

Response:

```json
{
  "success": true,
  "data": {
    "deleted": true
  }
}
```

## 업데이트 시점

- Developer API endpoint가 구현되거나 변경될 때
- API Key 인증 정책이 바뀔 때
- Asset 응답 DTO가 바뀔 때
