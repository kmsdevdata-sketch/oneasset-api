# Architecture Docs

아키텍처 문서는 OneAsset이 어떤 구조로 동작하고, 각 계층이 어디까지 책임지는지 설명한다.

## 파일 역할

| 파일 | 역할 | 업데이트 기준 |
| --- | --- | --- |
| `overview.md` | 시스템 목표, 주요 컨테이너, 외부 시스템 관계 | Cognito, Web, API, DB, S3, SQS, Lambda, CloudFront 관계가 바뀔 때 |
| `layer-boundaries.md` | adapter/application/domain/port 경계와 허용 의존성 | 패키지 구조나 계층 책임이 바뀔 때 |
| `runtime-flows.md` | 주요 런타임 흐름 | 로그인, Lazy Sync, 업로드, 비동기 처리 흐름이 바뀔 때 |
| `deployment-view.md` | 배포 단위와 인프라 연결 | Docker, ECS, RDS, S3, CloudFront, Lambda 배치가 바뀔 때 |
| `diagrams/` | Mermaid/C4 스타일 다이어그램 | 구조 변경을 그림으로 설명해야 할 때 |

## 작성 원칙

- 코드 수준 세부사항은 최소화하고, 경계와 흐름을 먼저 쓴다.
- 구현 세부 규칙은 `policies/`나 `domains/`로 보낸다.
- 큰 결정의 이유는 `decisions/`의 ADR에 남기고 여기서는 현재 상태만 요약한다.
