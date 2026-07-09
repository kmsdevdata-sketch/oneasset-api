# Layer Boundaries

## 역할

백엔드 내부 계층과 패키지 경계를 정의한다.

## 기본 방향

OneAsset API는 Hexagonal Lite 구조를 사용한다.

목표는 외부 기술 세부사항을 도메인과 유스케이스에서 분리하되, MVP 단계에서 과도한 추상화는 피하는 것이다.

## 패키지 계층

```text
io.oneasset
  adapter
    inbound
    outbound
  application
  domain
```

## 계층 책임

| 계층 | 책임 | 알 수 있는 것 | 몰라야 하는 것 |
| --- | --- | --- | --- |
| `adapter.inbound` | HTTP 요청, 인증 principal 추출, request/response 변환 | Spring MVC, Spring Security, DTO | 도메인 내부 상태 변경 세부 규칙 |
| `application` | 유스케이스 조합, 트랜잭션 경계, port 호출 | 도메인 모델, port 인터페이스 | JPA Entity, S3 SDK, Cognito SDK |
| `domain` | 비즈니스 규칙, 상태 전이, 값 객체 | 순수 Java 값과 도메인 규칙 | Spring, JPA, AWS SDK |
| `adapter.outbound` | DB, S3, Cognito, 외부 시스템 구현 | JPA, AWS SDK, 외부 API | HTTP 요청 DTO |

## Port 적용 기준

- 외부 시스템 또는 영속성 의존이 생기면 application에서 port를 바라본다.
- 도메인 모델은 repository나 storage를 직접 알지 않는다.
- MVP 초반에는 port 패키지를 도메인별로 얇게 두고, 구현이 늘어나면 `application/port/in`, `application/port/out`로 분리한다.

## 도메인 모델과 JPA Entity

- 도메인 모델은 순수 Java 객체로 둔다.
- JPA Entity는 persistence adapter에 둔다.
- JPA Entity는 DB 매핑과 ORM 요구사항을 담당한다.
- 도메인 모델은 상태 전이와 불변식을 담당한다.

## 허용 의존성

```text
adapter -> application -> domain
adapter.outbound -> application port/domain
domain -> Java standard library only
```

역방향 의존이 필요하면 port 인터페이스로 끊는다.

## 업데이트 시점

- 패키지 구조가 바뀔 때
- 계층 간 의존 규칙이 바뀔 때
- 도메인 모델과 영속성 모델 분리 수준이 바뀔 때

## 변경 전 확인할 문서

- `docs/decisions/`
- `docs/domains/README.md`
- `docs/workflows/change-checklist.md`
