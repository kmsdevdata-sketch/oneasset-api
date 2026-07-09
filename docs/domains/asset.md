# Asset Domain

## 역할

Asset과 AssetVariant의 저장, 변환, 전달, 삭제 규칙을 기록한다.

## 핵심 판단

Asset은 프로젝트에 소속된 원본 파일 metadata다.

AssetVariant는 원본, WebP, Thumbnail 같은 전달 가능한 파일 변환본이다. Asset의 하위 객체로 볼 수 있지만 DB에 별도 row와 unique storage key를 가지므로 영속화 대상 child entity로 취급한다.

## 도메인 속성 초안

| 모델 | 속성 | 의미 |
| --- | --- | --- |
| Asset | `id` | Asset ID |
| Asset | `projectId` | 소속 Project |
| Asset | `uploadedBy` | 업로드한 User |
| Asset | `originalFileName` | 원본 파일명 |
| Asset | `contentType` | 원본 MIME type |
| Asset | `sizeBytes` | 원본 크기 |
| Asset | `bucket`, `storageKey` | S3 저장 위치 |
| Asset | `status` | `UPLOADED`, `PROCESSING`, `READY`, `FAILED` |
| AssetVariant | `type` | `ORIGINAL`, `WEBP`, `THUMBNAIL` |
| AssetVariant | `bucket`, `storageKey` | 변환본 저장 위치 |
| AssetVariant | `contentType`, `sizeBytes`, `width`, `height` | 전달 파일 metadata |

## URL 정책

DB에는 public URL을 저장하지 않는다.

CloudFront domain과 storage key로 응답 시 `deliveryUrl`을 계산한다.

## 업데이트 시점

- Asset 업로드/조회/삭제 API가 구현될 때
- Lambda 변환 결과 저장 방식이 바뀔 때
- CDN URL 정책이 바뀔 때
