# DTO Catalog

## 역할

프론트와 백엔드가 공유하는 DTO 필드를 기록한다.

## User

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | string | OneAsset user id |
| `email` | string | 사용자 이메일 |
| `name` | string | 사용자 표시 이름 |

## Project

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | string | Project id |
| `name` | string | Project name |
| `slug` | string | Project slug |
| `createdAt` | string | 생성 시각 |
| `updatedAt` | string | 수정 시각 |

## ApiKey

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | string | API Key id |
| `name` | string | 사용자가 붙인 이름 |
| `prefix` | string | key prefix |
| `status` | string | `ACTIVE`, `REVOKED` |
| `createdAt` | string | 생성 시각 |
| `lastUsedAt` | string/null | 마지막 사용 시각 |

생성 응답에만 `apiKey` raw value를 포함한다.

## Asset

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | string | Asset id |
| `key` | string | Developer API key path |
| `originalFileName` | string | 원본 파일명 |
| `contentType` | string | MIME type |
| `sizeBytes` | number | 파일 크기 |
| `status` | string | 처리 상태 |
| `deliveryUrl` | string/null | 전달 URL |
| `createdAt` | string | 생성 시각 |

## AssetVariant

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `type` | string | `ORIGINAL`, `WEBP`, `THUMBNAIL` |
| `contentType` | string | MIME type |
| `width` | number/null | width |
| `height` | number/null | height |
| `sizeBytes` | number | 파일 크기 |
| `deliveryUrl` | string | CloudFront URL |

## 업데이트 시점

- 응답 필드가 추가/삭제/이름 변경될 때
- enum 값이 바뀔 때
- nullable 여부가 바뀔 때
