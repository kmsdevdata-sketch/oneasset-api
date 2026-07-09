# User Domain

## 역할

User 도메인의 현재 설계와 Cognito 사용자 매핑 정책을 기록한다.

## 핵심 판단

OneAsset은 회원가입/로그인 원본을 Cognito User Pool에 둔다.

`User` 도메인은 Cognito 유저 그 자체가 아니라, 인증된 Cognito 사용자가 OneAsset 리소스를 사용할 때 생성되는 로컬 사용자 모델이다.

## 생성 시점

로컬 User는 Cognito 회원가입 순간에 생성하지 않는다.

```text
Web <-> Cognito
Web -> API with JWT
API -> findOrCreateUser by cognito_sub
```

필터 또는 인증 경계에서 JWT를 검증하고 claim을 추출한 뒤, application 계층에서 `findOrCreateUser`를 수행한다.

## 도메인 속성

| 속성 | 타입 | 필수 | 의미 | 생성/변경 위치 |
| --- | --- | --- | --- | --- |
| `id` | `UserId` | Y | OneAsset 내부 사용자 ID | 로컬 User 생성 시 |
| `cognitoSub` | `String` | Y | Cognito User Pool의 사용자 식별자 | JWT claim `sub`에서 추출 |
| `email` | `String` | Y | 사용자 이메일 | Cognito claim에서 추출 |
| `name` | `String` | Y | 사용자 표시 이름 | Cognito claim에서 추출 |
| `status` | `UserStatus` | Y | OneAsset 서비스 사용 상태 | 도메인 메서드 |
| `createdAt` | `LocalDateTime` | Y | 로컬 User 생성 시각 | 도메인 생성/DB 복원 |
| `updatedAt` | `LocalDateTime` | Y | 로컬 User 수정 시각 | 도메인 메서드 |

## 값 객체

| 이름 | 감싸는 값 | 검증 규칙 | 사용 이유 |
| --- | --- | --- | --- |
| `UserId` | `UUID` | null 불가 | 내부 DB PK와 외부 Cognito 식별자를 분리한다. |

`cognitoSub`는 UUID처럼 보일 수 있어도 Cognito 전용 subject 문자열로 취급한다. 내부 `UserId`처럼 UUID 검증을 걸지 않는다.

## 상태

| 상태 | 의미 | 진입 조건 | 다음 가능 상태 |
| --- | --- | --- | --- |
| `ACTIVE` | 정상 사용자 | Lazy Sync로 로컬 User 생성 | `WITHDRAWN`, `BANNED` |
| `WITHDRAWN` | 탈퇴 사용자 | active user가 탈퇴 | 없음 |
| `BANNED` | 정지 사용자 | active user가 정지 | 없음 |

MVP1에서는 탈퇴/정지 상태 사용자의 페이지 접근 차단은 인증/인가 경계 또는 application 계층에서 처리한다. 도메인 메서드는 terminal 상태에서 다른 terminal 상태로 이동하지 못하게 막는다.

## DB 매핑 초안

| 테이블 | 컬럼 | 도메인 속성 | 제약 조건 | 비고 |
| --- | --- | --- | --- | --- |
| `users` | `id` | `id` | PK, uuid | OneAsset 내부 ID |
| `users` | `cognito_sub` | `cognitoSub` | UK, NOT NULL | Cognito 사용자와 로컬 User 매핑 |
| `users` | `email` | `email` | NOT NULL | Cognito claim 동기화 |
| `users` | `name` | `name` | NOT NULL | Cognito claim 동기화 |
| `users` | `status` | `status` | NOT NULL | enum string |
| `users` | `created_at` | `createdAt` | NOT NULL | 생성 시각 |
| `users` | `updated_at` | `updatedAt` | NOT NULL | 수정 시각 |

## 관련 정책

- `docs/policies/auth-and-user-sync.md`
- `docs/decisions/0001-use-cognito-lazy-user-sync.md`

## 업데이트 시점

- Cognito 연동 방식이 정해질 때
- `/me` 구현이 추가될 때
- User 도메인 모델 또는 JPA Entity가 바뀔 때
- User 상태 정책이 바뀔 때
