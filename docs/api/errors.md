# API Errors

## 역할

에러 코드, HTTP status, 사용자 메시지 정책을 기록한다.

## 공통 포맷

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human readable message"
  }
}
```

## 에러 코드 초안

| HTTP status | code | 의미 |
| --- | --- | --- |
| 400 | `VALIDATION_FAILED` | 요청 값 검증 실패 |
| 401 | `UNAUTHORIZED` | 인증 실패 |
| 403 | `FORBIDDEN` | 권한 없음 |
| 404 | `USER_NOT_FOUND` | User 없음 |
| 404 | `PROJECT_NOT_FOUND` | Project 없음 |
| 404 | `API_KEY_NOT_FOUND` | API Key 없음 |
| 401 | `API_KEY_INVALID` | API Key 검증 실패 |
| 401 | `API_KEY_REVOKED` | 폐기된 API Key |
| 404 | `ASSET_NOT_FOUND` | Asset 없음 |
| 409 | `DUPLICATE_RESOURCE` | unique 제약 충돌 |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 내부 오류 |

## 업데이트 시점

- 새로운 예외 타입이나 에러 코드가 추가될 때
- HTTP status 매핑이 바뀔 때
