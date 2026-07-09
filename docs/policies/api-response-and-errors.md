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
    "message": "Project not found"
  }
}
```

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
- 사용자에게 노출 가능한 message와 내부 로그 message를 구분한다.

예시:

- `USER_NOT_FOUND`
- `PROJECT_NOT_FOUND`
- `API_KEY_REVOKED`
- `ASSET_NOT_FOUND`
- `VALIDATION_FAILED`

## 업데이트 시점

- 공통 응답 wrapper가 구현될 때
- 예외 처리 계층이 구현될 때
- API 계약이 바뀔 때

## 같이 확인할 문서

- `docs/api/README.md`
- `docs/architecture/layer-boundaries.md`
