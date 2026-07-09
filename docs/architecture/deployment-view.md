# Deployment View

## 역할

OneAsset의 배포 단위와 인프라 연결을 기록한다.

## Local Development

로컬 개발은 Docker Compose 기반 PostgreSQL과 Spring Boot 애플리케이션으로 시작한다.

```text
compose.yaml
  postgres
  app
```

현재 로컬 검증 우선순위는 다음과 같다.

1. Spring Boot 애플리케이션 실행
2. Flyway 마이그레이션 자동 적용
3. Health check API
4. Project/API Key/Asset 핵심 흐름

## MVP1 Runtime

```text
Vercel Web
-> EC2 Public Endpoint
-> ECS on EC2 single container instance
-> Spring Boot API container
-> RDS PostgreSQL / S3 / SQS
```

MVP1에서는 비용과 운영 복잡도를 줄이기 위해 단일 EC2 Container Instance에서 시작한다.

## Storage and Processing

```text
Spring Boot API -> S3 original upload
S3 ObjectCreated -> SQS
SQS -> Lambda
Lambda -> S3 variants
Lambda -> RDS metadata update
CloudFront -> S3 origin
```

## MVP1 제외 인프라

- ALB: 초기에는 EC2 Public Endpoint로 단순화한다.
- Auto Scaling: 단일 인스턴스로 시작한다.
- Fargate: EC2 launch type 학습과 비용 통제를 우선한다.
- NAT Gateway: 비용 방지를 위해 만들지 않는다.
- Redis: MVP1 조회/검증은 PostgreSQL 인덱스와 애플리케이션 로직으로 처리한다.
- EventBridge: S3 Event Notification, SQS, Lambda 흐름으로 충분하다.

## 업데이트 시점

- 배포 대상이 바뀔 때
- AWS 리소스 구성이 바뀔 때
- Dockerfile, compose, GitHub Actions가 바뀔 때
