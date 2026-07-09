# API Response and Error Policy

## 역할

API 공통 응답 포맷, 예외 변환, 에러 코드 정책을 기록한다.

## 성공 응답

```json
{
  "success": true,
  "data": {}
}
```

## 실패 응답

```json
{
  "success": false,
  "error": {
    "code": "PROJECT_NOT_FOUND",
    "type": "/problems/projects/not-found",
    "title": "프로젝트를 찾을 수 없습니다",
    "status": 404,
    "instance": "/projects/019..."
  }
}
```

`error`는 RFC 9457 Problem Details 형식을 참고하되, OneAsset 클라이언트 분기 처리를 위해 `code`를 추가한다.

| 필드 | 의미 | 생성 위치 |
| --- | --- | --- |
| `code` | 클라이언트 분기 처리용 내부 에러 코드 | 도메인/계층별 `ErrorCode` enum |
| `type` | 에러 종류를 식별하는 URI | 도메인/계층별 `ErrorCode` enum |
| `title` | 에러 종류의 고정 요약 | 도메인/계층별 `ErrorCode` enum |
| `status` | HTTP status 숫자 | 도메인/계층별 `ErrorCode` enum |
| `instance` | 에러가 발생한 실제 요청 URI | `GlobalExceptionHandler` |

MVP1 초기에는 `detail`을 응답에 포함하지 않는다. 요청별 상세 설명이 필요해지는 시점에 추가한다.

## 페이지네이션 응답

```json
{
  "success": true,
  "data": {
    "items": [],
    "page": 0,
    "size": 20,
    "totalElements": 120,
    "totalPages": 6
  }
}
```

## Error Code 규칙

- 대문자 snake case를 사용한다.
- 도메인 접두사를 우선한다.
- `type`은 요청 URI가 아니라 에러 종류 식별 URI로 작성한다.
- `instance`는 exception handler에서 현재 요청 URI로 동적으로 채운다.
- 사용자에게 노출 가능한 `title`과 내부 로그 message를 구분한다.
- 도메인 또는 계층별 enum이 `ErrorCode` 인터페이스를 구현한다.

예시:

- `USER_NOT_FOUND`
- `PROJECT_NOT_FOUND`
- `API_KEY_REVOKED`
- `ASSET_NOT_FOUND`
- `VALIDATION_FAILED`

`type` 예시:

- `/problems/users/not-found`
- `/problems/projects/not-found`
- `/problems/api-keys/revoked`
- `/problems/assets/not-found`

## 예외 처리 범위

현재 공통 예외 처리는 `BaseException`을 대상으로 먼저 작성한다.

`BaseException`은 `ErrorCode`를 포함하며, `GlobalExceptionHandler`가 다음 책임을 가진다.

1. `ErrorCode`에서 `code`, `type`, `title`, `status`를 가져온다.
2. 현재 HTTP request URI를 `instance`로 설정한다.
3. `ApiResponse.fail(ErrorResult.of(errorCode, instance))` 형태로 실패 응답을 만든다.

Validation, Security, DB 제약, 파일 업로드, 외부 AWS 연동 예외는 실제 API 구현 중 필요한 시점에 추가한다.

## 업데이트 시점

- 공통 응답 wrapper가 구현될 때
- 예외 처리 계층이 구현될 때
- API 계약이 바뀔 때

## 같이 확인할 문서

- `docs/api/README.md`
- `docs/architecture/layer-boundaries.md`
