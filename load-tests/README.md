# OneAsset Load Test

OneAsset MVP1 배포 환경에서 API read path와 이미지 업로드 비동기 파이프라인을 검증하기 위한 k6 테스트 기록이다.

테스트 대상 경로는 다음 두 가지로 나눴다.

```text
API read path
Cloudflare -> ALB -> ECS on EC2 -> Spring Boot -> RDS

Upload async pipeline
Cloudflare -> ALB -> ECS on EC2 -> Spring Boot -> S3 -> SQS -> Lambda -> S3 variant -> Spring callback -> RDS
```

## 실행 환경

```bash
export ONEASSET_API_BASE_URL="https://api.oneasset.tech"
export ONEASSET_API_KEY="{test raw api key}"
export ONEASSET_EXISTING_ASSET_KEY="test/profile.png"
```

주의:

- 실제 raw API key는 문서나 git에 기록하지 않는다.
- `03-upload-pipline.js`, `06-upload-burst.js` 실행 전 `load-tests/fixtures/sample-profile.jpg` 파일이 필요하다.
- 업로드 테스트는 S3/RDS에 `load-test/` prefix 데이터를 생성하므로 테스트 후 정리한다.

## 테스트 목록

| Script | 목적 | 성격 |
| --- | --- | --- |
| `01-smoke.js` | health/list API와 API key 인증 확인 | 배포 상태 확인 |
| `02-road-baseline.js` | list/detail 읽기 API baseline 확인 | 읽기 기준선 |
| `03-upload-pipline.js` | 소량 업로드 E2E 파이프라인 확인 | 기능/상태 전이 검증 |
| `04-small-spike.js` | 50 VU 목록조회 spike 확인 | 가벼운 read spike |
| `05-api-stress-rps.js` | RPS 기반 API read path stress | API 계층 부하 검증 |
| `06-upload-burst.js` | 업로드 burst와 SQS/Lambda 후처리 검증 | 비동기 파이프라인 부하 검증 |

## 1-4. 기본 검증

1-4번 테스트는 본격적인 부하 측정보다는 배포 환경, 인증 헤더, read API, 소량 업로드 파이프라인이 정상 동작하는지 확인하는 용도다.

| Script | 조건 | 결과 요약 |
| --- | --- | --- |
| `01-smoke.js` | 1 VU, 30s | 46 requests, 실패율 0%, p95 237ms |
| `02-road-baseline.js` | 최대 10 VU, 3m | 1,676 requests, 실패율 0%, p95 169ms |
| `03-upload-pipline.js` | 20 uploads, 2 VU | READY 성공률 100%, READY p95 4.22s |
| `04-small-spike.js` | 최대 50 VU, 2m30s | 2,105 requests, 실패율 0%, p95 192ms |

이 구간은 포트폴리오의 핵심 성능 지표라기보다 이후 stress/burst 테스트를 해석하기 위한 기준선으로 사용했다.

## 5. API Stress / Scale Test

### 목적

`05-api-stress-rps.js`는 API read path가 단계적으로 증가하는 요청률을 안정적으로 처리하는지 확인하기 위한 테스트다.

검증 경로:

```text
Cloudflare -> ALB -> ECS on EC2 -> Spring Boot -> RDS
```

이 테스트는 전체 시스템의 쓰기/후처리 성능이 아니라, 동일 프로젝트의 에셋 목록조회 요청을 기준으로 API read path의 안정성을 확인하는 기준선이다.

### 실행

```bash
ONEASSET_API_BASE_URL="https://api.oneasset.tech" \
ONEASSET_API_KEY="{test raw api key}" \
k6 run --summary-export load-tests/results/api-stress-rps.json load-tests/scripts/05-api-stress-rps.js
```

시나리오:

```text
50 req/s  1m
100 req/s 1m
200 req/s 1m
300 req/s 1m
0 req/s   30s
```

### 관찰 지표

k6:

- `http_req_failed`
- `http_req_duration` p95/p99
- `http_reqs`
- `dropped_iterations`

CloudWatch:

- ALB `RequestCount`
- ALB `TargetResponseTime`
- ALB `HTTPCode_ELB_5XX_Count`
- ALB `HTTPCode_Target_5XX_Count`
- ECS `CPUUtilization`, `MemoryUtilization`
- EC2 `CPUUtilization`
- RDS `DatabaseConnections`

### 결과

읽기 API 기준 stress test에서 ALB-ECS-Spring-RDS 경로는 실패 없이 안정적으로 동작했다.

실행 조건:

- 동일 프로젝트의 에셋 목록조회 요청을 단계적으로 증가시켰다.
- 전체 시스템의 쓰기/후처리 성능이 아니라 API read path의 안정성 기준선으로 사용했다.

핵심 결과:

- 총 34,708건 요청을 처리했다.
- 실패율 0%, check 성공률 100%를 기록했다.
- p95 414ms, p99 723ms를 기록했다.

요약:

| Metric | Result |
| --- | --- |
| Total requests | 34,708 |
| Failure rate | 0% |
| Check success rate | 100% |
| p95 latency | 414.77ms |
| p99 latency | 723.49ms |
| Max latency | 2.08s |
| Dropped iterations | 92 |
| Average throughput | 128.47 req/s |

해석:

- 단순 read workload 기준으로 API 계층은 안정적으로 동작했다.
- 실패율은 0%였지만, 이 테스트는 동일 key/project 목록조회 중심이므로 쓰기 부하나 비동기 후처리 성능을 대표하지 않는다.
- `dropped_iterations`가 일부 발생했으나 전체 요청 대비 작고, 응답 실패는 발생하지 않았다.

## 6. Upload Async Burst Test

### 목적

`06-upload-burst.js`는 업로드 요청이 순간적으로 증가할 때 API 서버와 비동기 처리 파이프라인이 어떻게 동작하는지 확인하기 위한 테스트다.

검증 경로:

```text
Spring upload
-> S3 original object
-> RDS asset metadata(PROCESSING)
-> SQS message
-> Lambda image processor
-> S3 WebP variant
-> Spring internal callback
-> RDS asset variant + READY transition
```

이 테스트의 핵심은 API 서버가 이미지 변환을 직접 오래 붙잡지 않고, SQS/Lambda 기반 비동기 파이프라인으로 후처리 작업을 분리했는지 확인하는 것이다.

### 실행

300 uploads / 30 VU:

```bash
ONEASSET_API_BASE_URL="https://api.oneasset.tech" \
ONEASSET_API_KEY="{test raw api key}" \
ONEASSET_UPLOAD_BURST_TOTAL=300 \
ONEASSET_UPLOAD_BURST_VUS=30 \
ONEASSET_READY_SAMPLE_EVERY=25 \
k6 run --summary-export load-tests/results/upload-burst-300x30.json load-tests/scripts/06-upload-burst.js
```

1000 uploads / 100 VU:

```bash
ONEASSET_API_BASE_URL="https://api.oneasset.tech" \
ONEASSET_API_KEY="{test raw api key}" \
ONEASSET_UPLOAD_BURST_TOTAL=1000 \
ONEASSET_UPLOAD_BURST_VUS=100 \
ONEASSET_READY_SAMPLE_EVERY=50 \
k6 run --summary-export load-tests/results/upload-burst-1000x100.json load-tests/scripts/06-upload-burst.js
```

### 관찰 지표

k6:

- `upload_success_rate`
- `ready_sample_success_rate`
- `ready_sample_duration`
- `http_req_failed`
- `http_req_duration` p95/p99

CloudWatch:

- SQS `ApproximateNumberOfMessagesVisible`
- SQS `ApproximateNumberOfMessagesNotVisible`
- SQS `ApproximateAgeOfOldestMessage`
- SQS `NumberOfMessagesSent`
- SQS `NumberOfMessagesReceived`
- SQS `NumberOfMessagesDeleted`
- Lambda `Invocations`
- Lambda `Errors`
- Lambda `Duration`
- Lambda `Throttles`
- DLQ `ApproximateNumberOfMessagesVisible`

해석 기준:

```text
SQS Visible 증가
=> 큐가 burst workload를 버퍼링하고 있음

SQS Received/Deleted 증가
=> Lambda가 메시지를 소비하고 정상 처리 후 삭제하고 있음

Lambda Errors 증가
=> 이미지 처리 또는 callback 실패 가능성

DLQ Visible > 0
=> 실패 메시지 유입. 원인 분석 필요

OldestMessageAge 지속 상승
=> Lambda 처리량이 큐 유입량을 따라가지 못함
```

### 결과 1: 300 uploads / 30 VU

300건 업로드 burst에서 Spring-S3-SQS-Lambda-Callback-RDS 파이프라인은 실패 없이 동작했다.

실행 조건:

- `ONEASSET_UPLOAD_BURST_TOTAL=300`
- `ONEASSET_UPLOAD_BURST_VUS=30`
- `ONEASSET_READY_SAMPLE_EVERY=25`

핵심 결과:

- 30명의 가상 사용자가 총 300개의 이미지 업로드를 수행했다.
- 업로드 성공률 100%, HTTP 실패율 0%, 업로드 API p95 1.08초를 기록했다.
- 전체 300개 중 25개마다 1개씩 총 12개 에셋을 샘플링해 READY 상태 전환을 확인했다.
- 샘플 READY 성공률 100%, READY 전환 p95 15.03초를 기록했다.
- CloudWatch 기준 SQS backlog는 순간적으로 약 275개까지 증가했다.
- Lambda Invocations는 300건, DLQ 유입은 0건으로 확인되었다.

요약:

| Metric | Result |
| --- | --- |
| Uploads | 300 |
| VUs | 30 |
| Upload success rate | 100% |
| HTTP failure rate | 0% |
| Upload API p95 | 1.08s |
| Upload API p99 | 1.66s |
| READY samples | 12 |
| READY sample success rate | 100% |
| READY p95 | 15.03s |
| SQS visible backlog peak | about 275 |
| Lambda invocations | 300 |
| DLQ visible messages | 0 |

해석:

- API 서버가 이미지 후처리를 직접 수행하지 않고 SQS/Lambda 비동기 파이프라인으로 작업을 분리한 구조를 확인했다.
- 순간 업로드 부하는 SQS backlog로 흡수되었고, Lambda가 실패 없이 후처리를 완료했다.

### 결과 2: 1000 uploads / 100 VU

1,000건 업로드 burst에서는 기능 실패는 없었지만, API 응답 지연과 READY 전환 지연이 뚜렷하게 증가했다.

실행 조건:

- `ONEASSET_UPLOAD_BURST_TOTAL=1000`
- `ONEASSET_UPLOAD_BURST_VUS=100`
- `ONEASSET_READY_SAMPLE_EVERY=50`

핵심 결과:

- 100명의 가상 사용자가 총 1,000개의 이미지 업로드를 수행했다.
- 업로드 성공률 100%, HTTP 실패율 0%, check 성공률 100%를 기록했다.
- 전체 1,000개 중 50개마다 1개씩 총 20개 에셋을 샘플링해 READY 상태 전환을 확인했다.
- 샘플 READY 성공률은 100%였다.
- upload API p95 응답시간은 4.49초, p99 응답시간은 6.21초까지 증가했다.
- 샘플 READY 전환 p95는 30.10초로 측정되었다.

요약:

| Metric | Result |
| --- | --- |
| Uploads | 1,000 |
| VUs | 100 |
| Upload success rate | 100% |
| HTTP failure rate | 0% |
| Check success rate | 100% |
| Upload API p95 | 4.49s |
| Upload API p99 | 6.21s |
| Max API latency | 10.71s |
| READY samples | 20 |
| READY sample success rate | 100% |
| READY p95 | 30.10s |

해석:

- SQS/Lambda 기반 비동기 파이프라인은 고동시성 업로드 상황에서도 실패 없이 작업을 처리했다.
- 다만 응답시간과 READY 전환 시간이 기준치를 넘으며, 100 VU / 1,000 uploads 수준에서 지연 증가가 관찰되었다.
- 이는 실패가 아니라 현재 인프라와 구현에서의 고부하 지연 한계 지점으로 기록한다.

## 최종 정리

이번 테스트에서 확인한 내용:

- API read path는 34,708건 요청에서 실패율 0%, p95 414ms로 안정적으로 동작했다.
- 300 uploads / 30 VU에서는 비동기 업로드 파이프라인이 실패 없이 동작했고, SQS backlog와 Lambda invocations를 통해 후처리 분리 구조를 확인했다.
- 1000 uploads / 100 VU에서는 모든 요청과 샘플 READY 전환은 성공했지만, API p95와 READY p95가 증가하며 현재 구조의 지연 한계가 드러났다.

포트폴리오에서는 1-4번을 간단한 사전 검증으로 두고, 5번과 6번을 주요 성능/구조 검증 결과로 사용한다.
