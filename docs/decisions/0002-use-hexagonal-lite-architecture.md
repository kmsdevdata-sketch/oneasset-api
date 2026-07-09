# Use Hexagonal Lite Architecture

Status: accepted

Date: 2026-07-09

Decision makers: OneAsset backend owner

Consulted: Codex

Informed: Future OneAsset contributors

## Context and Problem Statement

OneAsset API는 Cognito, PostgreSQL, S3, SQS, Lambda, CloudFront 같은 외부 시스템과 연결된다.

도메인 규칙이 외부 기술 세부사항에 섞이면 학습과 변경이 어려워진다. 반대로 MVP 초반부터 완전한 포트/어댑터 구조를 과하게 적용하면 구현 속도가 느려진다.

## Decision Drivers

- 도메인 모델을 Spring/JPA/AWS SDK에서 분리한다.
- MVP1에서는 패키지와 추상화를 과하게 늘리지 않는다.
- 외부 시스템 경계는 명확히 둔다.
- 팀원이 파일 위치를 예측하기 쉽게 한다.

## Considered Options

- 전통적인 3-layer 구조
- 완전한 Hexagonal Architecture
- Hexagonal Lite 구조

## Decision Outcome

Chosen option: `Hexagonal Lite`, because 도메인과 외부 시스템 경계를 분리하면서도 MVP 단계의 생산성을 유지할 수 있다.

## Consequences

- Good: domain은 순수 Java에 가깝게 유지된다.
- Good: JPA Entity와 도메인 모델을 분리할 수 있다.
- Good: S3, Cognito, DB 같은 외부 구현을 adapter에 둘 수 있다.
- Bad: 3-layer보다 파일 수가 늘어난다.
- Neutral: port 패키지는 필요해지는 시점에 도메인별로 얇게 도입한다.

## Confirmation

- `adapter -> application -> domain` 의존 방향을 유지한다.
- domain에서 Spring, JPA, AWS SDK import를 금지한다.
- persistence adapter가 JPA Entity와 도메인 모델 변환을 담당한다.

## More Information

- 관련 문서: `docs/architecture/layer-boundaries.md`
