# OneAsset API

[![Java 21](https://img.shields.io/badge/Java-21-007396)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F)](https://spring.io/projects/spring-boot)
[![AWS](https://img.shields.io/badge/AWS-ECS%20on%20EC2%20%7C%20S3%20%7C%20SQS%20%7C%20Lambda-FF9900)](https://aws.amazon.com/)
[![k6](https://img.shields.io/badge/Load%20Test-k6-7D64FF)](https://k6.io/)

[English](./README.en.md) · [API Docs](./docs/api/README.md) · [Load Test Report](./load-tests/README.md)

OneAsset API는 개발자가 이미지 에셋을 프로젝트 단위로 업로드하고, 원본 저장과 변환 처리를 클라우드 파이프라인에 위임한 뒤, 안정적인 delivery URL로 사용할 수 있게 하는 에셋 관리 백엔드입니다.

대시보드에서는 프로젝트와 API 키를 관리하고, 외부 서비스는 발급받은 API 키로 `/v1/assets` API를 호출합니다. 업로드된 원본은 private S3에 저장되고, SQS와 Lambda를 통해 WebP variant가 비동기로 생성되며, 처리 완료 결과는 내부 callback API를 통해 Spring Boot 애플리케이션에 반영됩니다.

## 핵심 요약

| 구분 | 내용 |
| --- | --- |
| 목적 | 개발자용 이미지 에셋 업로드, 조회, 삭제, delivery URL 제공 |
| 인증 | Dashboard API는 Cognito JWT, Developer API는 hashed API key |
| 저장 | 원본과 variant를 private S3에 저장 |
| 후처리 | SQS queue와 Lambda sharp processor로 WebP variant 생성 |
| 배포 | Cloudflare, ALB, ECS on EC2, RDS PostgreSQL, CloudFront |
| 검증 | k6 부하 테스트와 CloudWatch 지표로 read path와 upload pipeline 확인 |

## Product

<details open>
<summary><strong>대시보드에서 프로젝트 API 키를 발급하고 관리합니다</strong></summary>

<br>

<img src="./portfolio-assets/readme/dashboard-api-page.png" alt="OneAsset API key dashboard" width="100%">

API 키는 원문을 1회만 노출하고, 서버에는 hash와 prefix만 저장합니다. 외부 개발자는 이 키를 `X-OneAsset-Api-Key` 헤더에 실어 Developer API를 호출합니다.

</details>

<details>
<summary><strong>프로젝트별 에셋 목록과 상세 정보를 확인합니다</strong></summary>

<br>

<img src="./portfolio-assets/readme/asset-list-page.png" alt="OneAsset asset list" width="100%">

<br><br>

<img src="./portfolio-assets/readme/asset-detail-page.png" alt="OneAsset asset detail" width="100%">

대시보드는 프로젝트 기준으로 에셋 목록과 상세 정보를 조회합니다. API 응답에는 사용자가 지정한 key, 실제 storage key, 처리 상태, delivery URL이 함께 내려갑니다.

</details>

## Architecture

<details open>
<summary><strong>AWS Runtime Architecture</strong></summary>

<br>

<img src="./portfolio-assets/readme/aws-architecture.png" alt="OneAsset AWS runtime architecture" width="100%">

현재 운영 경로는 `Cloudflare -> ALB -> ECS on EC2 -> Spring Boot`입니다. Spring Boot 애플리케이션은 RDS PostgreSQL에 메타데이터를 저장하고, S3에 원본을 저장한 뒤 SQS에 후처리 메시지를 발행합니다. Lambda는 SQS 메시지를 소비해 variant를 생성하고, 내부 callback API로 처리 결과를 반영합니다.

CloudFront는 OAC로 보호된 S3 origin을 통해 에셋 delivery를 담당합니다. S3 public access는 차단하고, CloudFront를 통해서만 객체를 읽을 수 있게 구성했습니다.

</details>

<details>
<summary><strong>Asset Processing Sequence</strong></summary>

<br>

<img src="./portfolio-assets/readme/asset-processing-sequence.png" alt="OneAsset asset processing sequence" width="100%">

1. 외부 개발자가 `POST /v1/assets`로 multipart 파일을 업로드합니다.
2. API 서버가 원본 파일을 S3에 저장합니다.
3. Asset 메타데이터를 `PROCESSING` 상태로 저장합니다.
4. SQS에 후처리 메시지를 발행합니다.
5. Lambda가 원본 이미지를 읽고 WebP variant를 생성합니다.
6. Lambda가 variant를 S3에 저장합니다.
7. Lambda가 `/internal/assets/{assetId}/variants` callback API를 호출합니다.
8. API 서버가 `AssetVariant`를 저장하고 Asset 상태를 `READY`로 전이합니다.

</details>

<details>
<summary><strong>Backend Architecture</strong></summary>

<br>

<img src="./portfolio-assets/readme/backend-architecture.png" alt="OneAsset backend architecture" width="100%">

백엔드는 과도한 UseCase 분리보다는 현재 도메인 크기에 맞춘 lightweight port-adapter 구조로 구성했습니다. Controller는 요청 인증과 command 조립을 담당하고, Application Service는 도메인 상태 전이와 외부 port 호출 순서를 조율합니다. S3, SQS, JPA 같은 기술 구현은 outbound adapter에 둬서 핵심 흐름이 특정 SDK에 직접 묶이지 않도록 했습니다.

</details>

<details>
<summary><strong>Data Model</strong></summary>

<br>

<img src="./portfolio-assets/readme/erd.png" alt="OneAsset ERD" width="100%">

핵심 모델은 `projects`, `api_keys`, `assets`, `asset_variants`입니다. `assets.storage_key`와 `asset_variants.storage_key`는 각각 unique constraint를 두어 S3 object와 DB metadata가 중복 매핑되지 않게 했습니다.

</details>

## API Surface

| Surface | 인증 방식 | 주요 용도 |
| --- | --- | --- |
| Dashboard API | Cognito JWT | 사용자, 프로젝트, API 키, 프로젝트 에셋 관리 |
| Developer API | `X-OneAsset-Api-Key` | 외부 서비스의 에셋 업로드, 조회, 삭제 |
| Internal API | `X-OneAsset-Processor-Callback-Token` | Lambda processor의 variant 완료 callback |

<details open>
<summary><strong>Developer Asset API</strong></summary>

```text
POST   /v1/assets
GET    /v1/assets
GET    /v1/assets?key={assetKey}
DELETE /v1/assets?key={assetKey}
```

Upload request:

```text
Content-Type: multipart/form-data
X-OneAsset-Api-Key: {raw_api_key}

file: File
key: test/profile.png
fileName: profile.png optional
```

Response:

```json
{
  "success": true,
  "data": {
    "assetId": "491b26d8-6ea1-4c00-9659-4d27c42895c8",
    "key": "test/profile.png",
    "storageKey": "projects/{projectId}/test/profile.png",
    "originalFileName": "profile.png",
    "contentType": "image/jpeg",
    "sizeBytes": 28760,
    "status": "PROCESSING",
    "deliveryUrl": "https://{cloudfront-domain}/projects/{projectId}/test/profile.png",
    "createdAt": "2026-07-21T16:16:30"
  }
}
```

</details>

<details>
<summary><strong>Asset Key Policy</strong></summary>

클라이언트는 프로젝트 내부에서 사용할 key만 지정합니다.

```text
test/profile.png
```

서버는 이 값을 프로젝트 scope 안으로 매핑합니다.

```text
projects/{projectId}/test/profile.png
```

생성된 variant는 원본 파일과 같은 디렉터리의 `variants` 하위 경로에 저장합니다.

```text
projects/{projectId}/test/variants/profile-w512.webp
```

이 방식은 사용자가 다루는 key를 단순하게 유지하면서, 실제 S3 layout은 프로젝트 단위로 격리할 수 있게 합니다.

</details>

## Deployment & Operations

<details open>
<summary><strong>CI/CD Pipeline</strong></summary>

<br>

<img src="./portfolio-assets/readme/cicd-pipeline.png" alt="OneAsset CI/CD pipeline" width="100%">

`main` 브랜치에 변경사항이 반영되면 GitHub Actions가 테스트와 `bootJar` 빌드를 수행합니다. 이후 `linux/amd64` Docker 이미지를 빌드해 ECR에 push하고, 기존 ECS task definition에서 image URI만 교체한 새 revision을 등록한 뒤 ECS service를 업데이트합니다.

```text
main push
-> test / bootJar
-> Docker buildx linux/amd64
-> ECR push
-> ECS task definition revision
-> ECS service update
-> wait service stable
```

</details>

<details>
<summary><strong>Runtime Configuration</strong></summary>

| 구성 | 현재 사용 |
| --- | --- |
| DNS / TLS edge | Cloudflare |
| Load Balancer | Application Load Balancer |
| Compute | ECS on EC2, Auto Scaling Group, t3.small |
| Database | Amazon RDS PostgreSQL |
| Object Storage | Amazon S3 private bucket |
| Async Queue | Amazon SQS + DLQ |
| Processor | AWS Lambda, Node.js, sharp |
| Delivery | CloudFront with Origin Access Control |

운영 중 확인한 이슈는 별도 문서와 CloudWatch 지표로 남겼습니다. 특히 ECS task subnet, ALB enabled AZ, NAT outbound, Lambda timeout, callback token, RDS security group 같은 배포형 문제를 실제로 겪고 정리했습니다.

</details>

## Load Test

README에는 포트폴리오에서 바로 읽히는 핵심 결과만 정리했습니다. 스크립트별 목적, 실행 명령, 해석 기준은 [Load Test Report](./load-tests/README.md)에 별도로 정리했습니다.

<details open>
<summary><strong>Read Path Stress Test</strong></summary>

읽기 API 기준 stress test를 수행해 `Cloudflare -> ALB -> ECS -> Spring Boot -> RDS` 경로의 기본 처리 안정성을 확인했습니다. 동일 프로젝트의 asset list/detail 조회를 반복 호출해, 인증 이후의 일반적인 dashboard/developer read path가 어느 정도의 요청을 실패 없이 처리하는지 기준선을 잡았습니다.

| 항목 | 결과 |
| --- | --- |
| 총 요청 수 | 34,708 |
| 평균 처리량 | 128.47 req/s |
| 실패율 | 0% |
| p95 | 414.77 ms |
| p99 | 723.49 ms |
| 최대 응답 시간 | 2.08 s |

이 테스트는 전체 비동기 처리량이 아니라 API read path의 기준선을 잡기 위한 테스트입니다. 따라서 SQS/Lambda 처리량을 증명하는 테스트가 아니라, ALB-ECS-Spring-RDS 경로의 기본 응답 안정성을 확인하는 성격입니다.

</details>

<details open>
<summary><strong>Upload Burst & Async Processing Test</strong></summary>

<br>

<img src="./portfolio-assets/readme/load-test-upload-burst.png" alt="OneAsset upload burst CloudWatch dashboard" width="100%">

업로드 burst 테스트에서는 API 서버가 이미지 변환을 직접 붙잡지 않고, SQS와 Lambda로 후처리를 분리했는지 확인했습니다. 업로드 요청은 S3 원본 저장, Asset `PROCESSING` 저장, SQS 메시지 발행까지 담당하고, 이후 Lambda가 variant를 생성한 뒤 callback으로 `READY` 전이를 완료합니다.

| 조건 | 결과 |
| --- | --- |
| Uploads / VUs | 300 uploads / 30 VUs |
| Upload success | 100% |
| HTTP failure | 0% |
| Upload API p95 | 1.08 s |
| READY sample success | 100% |
| READY sample p95 | 15.03 s |
| SQS visible backlog peak | about 275 |
| Lambda invocations | 300 |
| DLQ | 0 |

추가로 `1000 uploads / 100 VUs` burst도 수행했습니다. 업로드 성공률과 READY 전이는 100%였지만, API p95가 4.49s, READY sample p95가 30.10s까지 상승했습니다. 이를 통해 테스트 당시의 실행 용량인 `t3.small EC2 1대 + ECS task 1개` 기준에서는 순간 업로드 부하를 SQS/Lambda 파이프라인이 실패 없이 흡수하지만, 사용자 체감 latency는 증가한다는 한계를 확인했습니다.

결론적으로 현재 구성은 순간 업로드 부하를 실패 없이 받아낼 수 있었지만, 강한 burst에서는 API latency와 READY 도달 시간이 함께 증가했습니다. 이 결과는 다음 단계에서 ECS task 수, Lambda concurrency, SQS backlog alarm, RDS connection 정책을 조정해야 하는 근거로 사용합니다.

</details>

## Tech Stack

| 영역 | 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Security | Spring Security, Cognito JWT, hashed API key |
| Persistence | PostgreSQL, JPA, Flyway |
| Storage | Amazon S3 |
| Async Processing | Amazon SQS, AWS Lambda, sharp |
| Delivery | CloudFront, OAC |
| Runtime | Docker, ECS on EC2, ALB |
| CI/CD | GitHub Actions, ECR, ECS task definition revision |
| Test / Quality | JUnit, Spring Security Test, k6, Spotless, SpotBugs, JaCoCo |

## Local Development

<details>
<summary><strong>Environment Variables</strong></summary>

```text
APP_PORT=8080

POSTGRES_HOST=postgres
POSTGRES_PORT=5432
POSTGRES_DB=oneasset
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

COGNITO_ISSUER_URI={cognito_issuer_uri}
AWS_REGION=ap-northeast-2

ONEASSET_ASSET_BUCKET={bucket_name}
ONEASSET_DELIVERY_BASE_URL={cloudfront_base_url}
ONEASSET_ASSET_PROCESSING_QUEUE_URL={sqs_queue_url}
ONEASSET_PROCESSOR_CALLBACK_TOKEN={callback_token}
APP_CORS_ALLOWED_ORIGINS=http://localhost:5174
```

</details>

<details>
<summary><strong>Run</strong></summary>

```bash
docker compose up --build
```

```bash
./gradlew test
./gradlew compileJava
./gradlew spotlessCheck
```

</details>

## Next Improvements

- RDS security group을 dedicated ECS task security group 기준으로 정리
- ECS/Lambda secret을 ECS environment variable에서 Secrets Manager 또는 Parameter Store로 이동
- SQS DLQ redrive와 CloudWatch alarm 추가
- Terraform 또는 CloudFormation 기반 IaC 정리
- API 사용량 제한, quota, rate limit 정책 추가
- CloudFront custom domain과 origin TLS 정책 정리
