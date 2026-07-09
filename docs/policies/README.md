# Policy Docs

정책 문서는 코드 여러 곳에 흩어질 수 있는 판단 기준을 한곳에 둔다.

## 파일 역할

| 파일 | 역할 | 업데이트 기준 |
| --- | --- | --- |
| `auth-and-user-sync.md` | Cognito 인증, JWT claim, User Lazy Sync | 인증 흐름이나 사용자 동기화 정책이 바뀔 때 |
| `api-response-and-errors.md` | 공통 응답, 예외, 에러 코드 | 응답 포맷이나 예외 처리 방식이 바뀔 때 |
| `time-and-audit.md` | 시간 타입, createdAt/updatedAt, 감사 필드 | 시간대나 audit 필드 정책이 바뀔 때 |
| `deletion.md` | soft delete/hard delete 기준 | 삭제 정책이 바뀔 때 |

## 작성 원칙

- 정책은 “현재 선택”과 “선택하지 않은 대안”을 구분한다.
- 되돌리기 어려운 정책은 ADR로 승격한다.
- 정책 변경 시 관련 도메인 문서와 API 문서를 같이 확인한다.
