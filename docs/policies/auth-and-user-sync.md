# Auth and User Sync Policy

## 역할

Cognito 인증과 OneAsset 로컬 User 동기화 정책을 기록한다.

## 현재 정책

OneAsset은 Cognito User Pool을 인증 원본으로 사용한다.

Spring Boot API는 Cognito 회원가입/로그인 과정을 직접 처리하지 않는다. Dashboard API는 Cognito가 발급한 JWT를 검증하는 Resource Server로 동작한다.

## Runtime Relationship

```text
Web <-> Cognito
Web -> API with Bearer JWT
API -> validate JWT using Cognito issuer/JWK
API -> findOrCreate local User by cognito_sub
```

## Lazy Sync

MVP1에서는 Lazy Sync를 사용한다.

로컬 `users` row는 Cognito 회원가입 시점이 아니라, 인증된 사용자가 OneAsset 보호 API에 처음 접근할 때 생성한다.

```text
JWT validated
-> extract sub/email/name
-> find users by cognito_sub
-> if missing, create local User
-> if exists, optionally sync profile
```

## 정합성 기준

나쁜 기준:

```text
Cognito 가입 즉시 OneAsset DB users row가 반드시 있어야 한다.
```

MVP1 기준:

```text
OneAsset 보호 리소스를 만들거나 조회하는 인증 사용자는 반드시 local users row가 있어야 한다.
```

이 기준은 `users.cognito_sub`의 `UNIQUE NOT NULL` 제약으로 지킨다.

## Claim 정책

| 값 | 출처 | 필수 | 비고 |
| --- | --- | --- | --- |
| `cognitoSub` | JWT `sub` | Y | Cognito 사용자와 local User 매핑 키 |
| `email` | ID token/user claim | Y for MVP1 | Dashboard `/me` 계약에 포함 |
| `name` | ID token/user claim | Y for MVP1 | Dashboard `/me` 계약에 포함 |

Access token에는 `email`, `name`이 항상 있다고 가정하지 않는다. 구현 시 Dashboard API가 어떤 토큰을 받을지, 또는 userInfo/claim 설정으로 값을 확보할지 확인한다.

## Post Confirmation Trigger

현재는 사용하지 않는다.

이유:

- Lambda에서 WAS 또는 DB를 호출하는 운영 경로가 추가된다.
- 실패 시 Cognito에는 유저가 있고 local DB에는 없는 보정 로직이 필요하다.
- MVP1에서는 OneAsset을 실제로 사용하지 않은 가입자까지 DB에 저장할 필요가 낮다.

필요해지면 ADR을 새로 작성한다.

## 업데이트 시점

- Cognito User Pool/App Client 설정이 정해질 때
- Spring Security Resource Server 설정이 추가될 때
- `/me` 또는 사용자 동기화 유스케이스가 구현될 때
- Lazy Sync 정책이 바뀔 때

## 같이 확인할 문서

- `docs/domains/user.md`
- `docs/api/dashboard-api.md`
- `docs/data/schema.md`
- `docs/decisions/`
