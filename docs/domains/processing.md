# Processing Domain

## 역할

비동기 이미지 처리와 처리 로그의 의미를 기록한다.

## 핵심 판단

MVP1 비동기 처리는 Spring Boot API가 직접 큐 메시지를 발행하지 않고, S3 ObjectCreated 이벤트를 시작점으로 둔다.

```text
Spring Boot API
-> S3 original upload
-> S3 Event Notification
-> SQS
-> Lambda
-> S3 variants
-> PostgreSQL status update
```

## 상태 업데이트

| 상태 | 의미 | 변경 주체 |
| --- | --- | --- |
| `UPLOADED` | 원본 업로드와 metadata 저장 완료 | Spring Boot API |
| `PROCESSING` | Lambda가 처리 시작 | Lambda |
| `READY` | 변환본 저장과 metadata 반영 완료 | Lambda |
| `FAILED` | 처리 실패 | Lambda |

## ProcessingLog

ProcessingLog는 비동기 이미지 처리 과정에서 남기는 이벤트 이력이다.

`Asset.status`가 현재 처리 상태를 표현하고, `AssetVariant`가 생성된 변환본을 표현한다.
ProcessingLog는 상태 판단의 기준이 아니라 운영/디버깅을 위한 처리 단계별 기록으로 사용한다.

초기에는 다음 경우에만 저장을 검토한다.

- Lambda 처리 실패
- 변환본 생성 성공/실패 추적
- 운영 화면 또는 디버깅에 필요한 처리 메시지

단순 상태 조회 API는 ProcessingLog 없이 Asset status와 AssetVariant만으로도 가능하다.

## 구현 범위

현재 구현 범위는 다음까지만 포함한다.

- `processing_logs` 테이블 마이그레이션
- ProcessingLog 도메인 모델과 값 객체
- JPA Entity, Spring Data Repository, Persistence Adapter
- mock 기반 영속성 어댑터 단위 테스트

현재 구현 범위에 포함하지 않는 것은 다음과 같다.

- Dashboard API 또는 Developer API 응답 노출
- ProcessingLog 저장을 호출하는 application service
- Lambda/SQS 처리 플로우와의 실제 연동
- 실패/재시도 정책
- ProcessingLog를 기준으로 한 Asset 상태 판단

## 도메인 모델

```text
ProcessingLog
- id
- assetId
- step
- status
- message
- createdAt
```

`message`는 운영/디버깅용 설명이므로 nullable을 허용한다.

## 책임 메서드

- `create(assetId, step, status, message)`
- `reconstitute(...)`
- `isSuccess()`
- `isFailed()`

## 업데이트 시점

- Lambda 처리 방식이 구현될 때
- ProcessingLog를 API 또는 운영 화면에 노출하기로 결정할 때
- 실패/재시도 정책이 바뀔 때
