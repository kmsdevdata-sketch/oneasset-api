# API Docs

API 문서는 외부에 노출되는 HTTP 계약을 기록한다.

## 파일 역할

| 파일 | 역할 | 업데이트 기준 |
| --- | --- | --- |
| `dashboard-api.md` | JWT 기반 Dashboard API | `/me`, Project, API Key, Asset Browser API가 바뀔 때 |
| `developer-api.md` | API Key 기반 Developer API | `/v1/assets` 계열 API가 바뀔 때 |
| `dto.md` | request/response DTO 필드 | DTO 필드명, nullability, enum이 바뀔 때 |
| `errors.md` | 에러 코드와 HTTP status | 예외 매핑이 바뀔 때 |

## 작성 원칙

- 컨트롤러 구현 전 계약을 먼저 적는다.
- 코드 변경 후 실제 응답과 문서가 맞는지 확인한다.
- 프론트가 받지 않는 추측 필드는 넣지 않는다.
