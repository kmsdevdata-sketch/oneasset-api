# API Errors

## 역할

에러 코드, HTTP status, 사용자 메시지 정책을 기록한다.

## 공통 포맷

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "type": "/problems/domain/error-name",
    "title": "Human readable title",
    "status": 400,
    "instance": "/request/path"
  }
}
```

OneAsset 에러 응답은 RFC 9457 Problem Details 형식을 참고한다.

| 필드 | 설명 |
| --- | --- |
| `code` | 클라이언트 분기 처리용 내부 코드 |
| `type` | 에러 종류 식별 URI. 요청 URI가 아니다. |
| `title` | 에러 종류의 고정 요약 |
| `status` | HTTP status 숫자 |
| `instance` | 에러가 발생한 실제 요청 URI |

MVP1 초기 응답에는 `detail`을 포함하지 않는다.

## 구현 정책

- 도메인 또는 계층별 enum이 `ErrorCode` 인터페이스를 구현한다.
- `ErrorCode`는 `code`, `title`, `status`, `type`을 제공한다.
- `BaseException`은 `ErrorCode`를 보관한다.
- `GlobalExceptionHandler`는 `BaseException`을 처리하고 request URI를 `instance`로 채운다.
- Validation, Security, DB, AWS 연동 예외는 실제 API 구현 중 필요해질 때 별도 handler를 추가한다.

## 에러 코드 초안

| HTTP status | code | type | 의미 |
| --- | --- | --- | --- |
| 400 | `VALIDATION_FAILED` | `/problems/validation-failed` | 요청 값 검증 실패 |
| 401 | `UNAUTHORIZED` | `/problems/auth/unauthorized` | 인증 실패 |
| 403 | `FORBIDDEN` | `/problems/auth/forbidden` | 권한 없음 |
| 404 | `USER_NOT_FOUND` | `/problems/users/not-found` | User 없음 |
| 404 | `PROJECT_NOT_FOUND` | `/problems/projects/not-found` | Project 없음 |
| 404 | `API_KEY_NOT_FOUND` | `/problems/api-keys/not-found` | API Key 없음 |
| 401 | `API_KEY_INVALID` | `/problems/api-keys/invalid` | API Key 검증 실패 |
| 401 | `API_KEY_REVOKED` | `/problems/api-keys/revoked` | 폐기된 API Key |
| 404 | `ASSET_NOT_FOUND` | `/problems/assets/not-found` | Asset 없음 |
| 409 | `DUPLICATE_RESOURCE` | `/problems/duplicate-resource` | unique 제약 충돌 |
| 500 | `INTERNAL_SERVER_ERROR` | `/problems/internal-server-error` | 서버 내부 오류 |

## 업데이트 시점

- 새로운 예외 타입이나 에러 코드가 추가될 때
- HTTP status 매핑이 바뀔 때
