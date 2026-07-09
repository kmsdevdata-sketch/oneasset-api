# Architecture Overview

## 역할

OneAsset 전체 시스템의 현재 구조를 설명한다.

## 시스템 목적

OneAsset은 개발자가 이미지 처리 인프라를 직접 구성하지 않고, 프로젝트별 API Key로 파일을 업로드하고 CDN URL을 사용할 수 있게 하는 Cloud Native Asset Platform이다.

MVP1은 다음 흐름을 완성하는 데 집중한다.

```text
Developer App
-> OneAsset API
-> S3 original upload
-> S3 Event Notification
-> SQS
-> Lambda image processing
-> S3 variants
-> CloudFront delivery URL
```

## 주요 사용자

- Dashboard User: OneAsset 웹 대시보드에서 프로젝트, API Key, Asset을 관리한다.
- Developer App: 발급받은 API Key로 Developer API를 호출한다.

## 주요 컨테이너

| 컨테이너 | 책임 |
| --- | --- |
| Web Dashboard | Cognito 로그인, 프로젝트/API Key/Asset 관리 UI |
| Cognito User Pool | 회원가입, 로그인, 토큰 발급, 사용자 인증 원본 |
| Spring Boot API | Dashboard API, Developer API, 도메인 유스케이스, DB/S3 연동 |
| PostgreSQL | User, Project, API Key, Asset metadata 저장 |
| S3 | 원본/변환본 파일 저장 |
| CloudFront | Asset delivery URL 제공 |
| SQS | S3 ObjectCreated 이벤트 기반 처리 큐 |
| Lambda | WebP/Thumbnail 생성과 처리 결과 반영 |

## 현재 MVP1 제외 범위

- ALB
- Auto Scaling
- Fargate
- Redis / ElastiCache
- EventBridge
- WAF
- Private Asset
- Presigned URL
- SDK / CLI
- Usage Dashboard / 비용 통계

## 현재 구조 요약

```text
Web <-> Cognito
Web -> Spring Boot API with JWT
Developer App -> Spring Boot API with X-OneAsset-Api-Key
Spring Boot API -> PostgreSQL
Spring Boot API -> S3
S3 -> SQS -> Lambda
CloudFront -> S3
```

Spring Boot API는 Cognito 로그인 과정의 중간자가 아니다. Cognito가 발급한 JWT를 검증하는 Resource Server로 동작한다.

## 업데이트 시점

- 새로운 외부 시스템이 추가될 때
- 컨테이너 책임이 바뀔 때
- MVP 범위나 인프라 방향이 바뀔 때
