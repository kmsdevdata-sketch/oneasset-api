# Use Cognito Lazy User Sync

Status: accepted

Date: 2026-07-09

Decision makers: OneAsset backend owner

Consulted: Codex

Informed: Future OneAsset contributors

## Context and Problem Statement

OneAsset은 Cognito User Pool을 사용해 회원가입과 로그인을 처리한다.

Spring Boot API는 사용자의 Project, API Key, Asset 소유관계를 관리하기 위해 로컬 `users` row가 필요하다. 문제는 Cognito가 사용자를 만들었다고 해서 Spring Boot API에 자동으로 사용자 정보를 보내주지 않는다는 점이다.

로컬 User를 언제 생성할지 결정해야 한다.

## Decision Drivers

- MVP1 복잡도를 낮춘다.
- Cognito를 인증 원본으로 유지한다.
- OneAsset 리소스를 사용하는 사용자는 반드시 local User로 매핑한다.
- 장애 보정과 재시도 경로를 최소화한다.

## Considered Options

- Post Confirmation Lambda Trigger로 가입 확인 직후 local User 생성
- 첫 보호 API 호출 시 JWT claim으로 local User Lazy Sync
- Spring Boot API가 Cognito 회원가입을 직접 proxy

## Decision Outcome

Chosen option: `첫 보호 API 호출 시 JWT claim으로 local User Lazy Sync`, because Cognito 인증 흐름을 단순하게 유지하면서 OneAsset 리소스 접근 시점의 DB 정합성을 보장할 수 있다.

## Consequences

- Good: Cognito 가입만 하고 OneAsset을 사용하지 않은 사용자는 DB에 저장하지 않는다.
- Good: Lambda trigger, internal API, 재시도/idempotency 경로가 필요 없다.
- Good: Spring Boot API는 Resource Server 역할에 집중한다.
- Bad: Cognito에는 존재하지만 local DB에는 아직 없는 사용자가 있을 수 있다.
- Neutral: `users.cognito_sub`에 `UNIQUE NOT NULL` 제약이 필요하다.

## Confirmation

- `/me` 또는 인증 필터/유스케이스에서 `findOrCreateUser`를 수행한다.
- local User 생성은 `User.createFromCognito(cognitoSub, email, name)`을 사용한다.
- Project, API Key, Asset 생성은 local User가 존재한 뒤에만 수행한다.

## More Information

- 관련 문서: `docs/policies/auth-and-user-sync.md`
- 관련 문서: `docs/domains/user.md`
