# Constraints and Indexes

## 역할

DB 무결성 제약과 조회 인덱스를 기록한다.

## Unique Constraints

| 테이블 | 제약 | 이유 |
| --- | --- | --- |
| `users` | `cognito_sub` unique | Cognito 사용자와 local User 1:1 매핑 |
| `projects` | `slug` unique | 프로젝트 slug 충돌 방지 |
| `api_keys` | `key_hash` unique | raw key 검증 결과 중복 방지 |
| `assets` | `storage_key` unique | S3 object와 metadata 매핑 |
| `asset_variants` | `storage_key` unique | 변환본 object 중복 방지 |

## Foreign Keys

| 테이블 | FK |
| --- | --- |
| `project_members` | `project_id -> projects.id`, `user_id -> users.id` |
| `api_keys` | `project_id -> projects.id` |
| `assets` | `project_id -> projects.id`, `uploaded_by -> users.id` |
| `asset_variants` | `asset_id -> assets.id` |
| `processing_logs` | `asset_id -> assets.id` |

## 조회 인덱스 초안

| 인덱스 대상 | 이유 |
| --- | --- |
| `users(cognito_sub)` | Lazy Sync 조회 |
| `project_members(user_id)` | 내 프로젝트 목록 |
| `api_keys(project_id)` | 프로젝트별 API Key 목록 |
| `api_keys(key_hash)` | Developer API 인증 |
| `assets(project_id, created_at)` | Asset 목록 |
| `asset_variants(asset_id)` | Asset 상세 variants |

## 업데이트 시점

- 마이그레이션에 제약 또는 인덱스를 추가할 때
- 조회 쿼리나 repository 메서드가 바뀔 때
