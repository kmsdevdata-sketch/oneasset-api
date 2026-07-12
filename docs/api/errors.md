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
- Validation 실패는 `COMMON_INVALID_INPUT_400`으로 응답한다.
- DB 접근 예외는 `COMMON_DATA_ACCESS_ERROR_500`으로 응답한다.
- 예상하지 못한 예외는 `COMMON_INTERNAL_SERVER_ERROR_500`으로 응답한다.
- 도메인 모델과 application service에서 직접 `IllegalArgumentException`, `IllegalStateException`을 던지지 않고 `BaseException`을 사용한다.

## 에러 코드 초안

| HTTP status | code | type | 의미 |
| --- | --- | --- | --- |
| 400 | `COMMON_INVALID_INPUT_400` | `/errors/common/invalid-input` | 요청 값 또는 도메인 입력값 검증 실패 |
| 400 | `COMMON_INVALID_ID_400` | `/errors/common/invalid-id` | ID 형식 오류 |
| 401 | `AUTH_REQUIRED_JWT_CLAIM_MISSING_401` | `/errors/auth/required-jwt-claim-missing` | JWT 필수 claim 누락 |
| 401 | `AUTH_INVALID_API_KEY_401` | `/errors/auth/invalid-api-key` | Developer API Key 누락 또는 불일치 |
| 403 | `PROJECT_ACCESS_DENIED_403` | `/errors/project/access-denied` | 프로젝트 접근 권한 없음 |
| 404 | `PROJECT_NOT_FOUND_404` | `/errors/project/not-found` | Project 없음 |
| 409 | `PROJECT_DELETED_CANNOT_BE_CHANGED_409` | `/errors/project/deleted-cannot-be-changed` | 삭제된 Project 변경 시도 |
| 409 | `USER_NOT_ACTIVE_409` | `/errors/user/not-active` | 활성 상태가 아닌 User 변경 시도 |
| 409 | `API_KEY_REVOKED_CANNOT_BE_CHANGED_409` | `/errors/api-key/revoked-cannot-be-changed` | 폐기된 API Key 변경 시도 |
| 409 | `ASSET_INVALID_STATUS_TRANSITION_409` | `/errors/asset/invalid-status-transition` | Asset 상태 전이 규칙 위반 |
| 409 | `ASSET_DELETED_CANNOT_BE_CHANGED_409` | `/errors/asset/deleted-cannot-be-changed` | 삭제된 Asset 변경 시도 |
| 500 | `COMMON_DATA_ACCESS_ERROR_500` | `/errors/common/data-access-error` | DB 접근 오류 |
| 500 | `COMMON_INTERNAL_SERVER_ERROR_500` | `/errors/common/internal-server-error` | 서버 내부 오류 |

## 업데이트 시점

- 새로운 예외 타입이나 에러 코드가 추가될 때
- HTTP status 매핑이 바뀔 때
