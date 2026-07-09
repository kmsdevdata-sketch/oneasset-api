# Database Schema

## 역할

OneAsset DB 테이블과 컬럼을 기록한다.

## MVP1 테이블

### users

| 컬럼 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | uuid | PK | OneAsset user id |
| `cognito_sub` | varchar | UNIQUE, NOT NULL | Cognito subject |
| `email` | varchar | NOT NULL | email claim |
| `name` | varchar | NOT NULL | name claim |
| `status` | varchar | NOT NULL | `ACTIVE`, `WITHDRAWN`, `BANNED` |
| `created_at` | timestamptz | NOT NULL | 생성 시각 |
| `updated_at` | timestamptz | NOT NULL | 수정 시각 |

### projects

| 컬럼 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | uuid | PK | Project id |
| `name` | varchar | NOT NULL | 프로젝트 이름 |
| `slug` | varchar | UNIQUE, NOT NULL | slug |
| `created_at` | timestamptz | NOT NULL | 생성 시각 |
| `updated_at` | timestamptz | NOT NULL | 수정 시각 |
| `deleted_at` | timestamptz | nullable | soft delete |

### project_members

| 컬럼 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | uuid | PK | membership id |
| `project_id` | uuid | FK, NOT NULL | Project |
| `user_id` | uuid | FK, NOT NULL | User |
| `role` | varchar | NOT NULL | owner/member |
| `created_at` | timestamptz | NOT NULL | 생성 시각 |

### api_keys

| 컬럼 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | uuid | PK | API Key id |
| `project_id` | uuid | FK, NOT NULL | Project |
| `name` | varchar | NOT NULL | 표시 이름 |
| `key_prefix` | varchar | NOT NULL | prefix |
| `key_hash` | varchar | UNIQUE, NOT NULL | hash |
| `status` | varchar | NOT NULL | `ACTIVE`, `REVOKED` |
| `created_at` | timestamptz | NOT NULL | 생성 시각 |
| `last_used_at` | timestamptz | nullable | 마지막 사용 |
| `revoked_at` | timestamptz | nullable | 폐기 시각 |

### assets

| 컬럼 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | uuid | PK | Asset id |
| `project_id` | uuid | FK, NOT NULL | Project |
| `uploaded_by` | uuid | FK, nullable | 업로드 사용자 |
| `original_file_name` | varchar | NOT NULL | 원본 파일명 |
| `content_type` | varchar | NOT NULL | MIME type |
| `size_bytes` | bigint | NOT NULL | 파일 크기 |
| `bucket` | varchar | NOT NULL | S3 bucket |
| `storage_key` | varchar | UNIQUE, NOT NULL | S3 key |
| `status` | varchar | NOT NULL | 처리 상태 |
| `created_at` | timestamptz | NOT NULL | 생성 시각 |
| `updated_at` | timestamptz | NOT NULL | 수정 시각 |
| `deleted_at` | timestamptz | nullable | soft delete |

### asset_variants

| 컬럼 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | uuid | PK | variant id |
| `asset_id` | uuid | FK, NOT NULL | Asset |
| `type` | varchar | NOT NULL | `ORIGINAL`, `WEBP`, `THUMBNAIL` |
| `content_type` | varchar | NOT NULL | MIME type |
| `size_bytes` | bigint | NOT NULL | 파일 크기 |
| `bucket` | varchar | NOT NULL | S3 bucket |
| `storage_key` | varchar | UNIQUE, NOT NULL | S3 key |
| `width` | integer | nullable | width |
| `height` | integer | nullable | height |
| `created_at` | timestamptz | NOT NULL | 생성 시각 |

### processing_logs

| 컬럼 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | uuid | PK | log id |
| `asset_id` | uuid | FK, NOT NULL | Asset |
| `step` | varchar | NOT NULL | `UPLOAD`, `WEBP`, `THUMBNAIL`, `COMPLETE` |
| `status` | varchar | NOT NULL | `SUCCESS`, `FAILED` |
| `message` | text | nullable | 처리 메시지 |
| `created_at` | timestamptz | NOT NULL | 생성 시각 |

## 업데이트 시점

- Flyway 마이그레이션을 추가하기 전
- 마이그레이션 작성 후 실제 컬럼/제약과 문서를 대조할 때
