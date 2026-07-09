# Time and Audit Policy

## 역할

시간 타입, 시간대, 생성/수정 시각, 감사 필드 정책을 기록한다.

## 현재 기준

| 영역 | 기준 |
| --- | --- |
| Java 도메인 모델 | `LocalDateTime`으로 시작 |
| DB | PostgreSQL `timestamptz` 후보 |
| Hibernate JDBC timezone | `Asia/Seoul` 설정 존재 |
| API 응답 | ISO-8601 문자열 |

## Audit 필드

| 필드 | 의미 | 갱신 책임 |
| --- | --- | --- |
| `createdAt` | 생성 시각 | 도메인 생성 또는 JPA 생성 시점 |
| `updatedAt` | 수정 시각 | 상태 변경/프로필 동기화 메서드 |
| `deletedAt` | soft delete 시각 | 삭제 유스케이스 |

## 아직 정해야 할 것

- DB를 `timestamptz`로 둘 때 Java 타입을 `OffsetDateTime`/`Instant`로 바꿀지 여부
- JPA Auditing을 사용할지, 도메인 메서드에서 직접 갱신할지
- API 응답을 UTC `Z`로 통일할지, 서버 시간대 기준으로 둘지

## 업데이트 시점

- 첫 JPA Entity나 마이그레이션이 작성될 때
- 시간대 또는 audit 필드 처리 방식이 바뀔 때
