# API Key Domain

## 역할

프로젝트별 API Key의 발급, 검증, 폐기, 보안 정책을 기록한다.

## 핵심 판단

API Key는 Developer API 인증 수단이다.

원문 API Key는 생성 시 한 번만 반환하고 DB에는 저장하지 않는다.

도메인 모델은 raw key를 알지 않는다. key 생성기 또는 application service가 raw key를 만들고, server secret을 사용해 hash를 계산한 뒤 `ApiKeyPrefix`, `ApiKeyHash`를 도메인 모델에 전달한다.

hash 생성에는 server secret이 필요하므로 `ApiKey` entity 내부 책임으로 두지 않는다.

## 도메인 속성 초안

| 속성 | 의미 | 제약 |
| --- | --- | --- |
| `id` | API Key ID | PK |
| `projectId` | 소속 Project | FK |
| `name` | 사용자가 붙인 이름 | NOT NULL |
| `keyPrefix` | UI 식별용 prefix | NOT NULL |
| `keyHash` | raw key + server secret hash | UNIQUE, NOT NULL |
| `status` | `ACTIVE`, `REVOKED` | NOT NULL |
| `createdAt` | 생성 시각 | NOT NULL |
| `lastUsedAt` | 마지막 사용 시각 | nullable |
| `revokedAt` | 폐기 시각 | nullable |

## 값 객체

| 이름 | 감싸는 값 | 사용 이유 |
| --- | --- | --- |
| `ApiKeyId` | UUID | 내부 API Key ID |
| `ApiKeyPrefix` | String | raw key 노출 없이 UI에서 식별하기 위한 값 |
| `ApiKeyHash` | String | raw key 검증용 저장 값 |

## 보안 규칙

- raw key는 생성 응답에서만 반환한다.
- DB에는 `key_hash`, `key_prefix`만 저장한다.
- API Key 검증은 raw key를 같은 방식으로 hash한 뒤 `key_hash`로 조회한다.
- 폐기된 key는 인증에 사용할 수 없다.

## 책임 메서드

- `create(projectId, name, prefix, hash)`
- `reconstitute(...)`
- `rename(name)`
- `markUsed()`
- `revoke()`
- `isActive()`
- `isRevoked()`

## 업데이트 시점

- API Key 발급/폐기 API가 구현될 때
- hash 알고리즘이나 secret 관리 방식이 바뀔 때
- Developer API 인증 방식이 바뀔 때
