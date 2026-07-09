# Change Checklist

## 작업 전

- [ ] 이번 변경이 어떤 도메인에 속하는지 확인한다.
- [ ] 관련 `domains/` 문서를 읽는다.
- [ ] 관련 `policies/` 문서를 읽는다.
- [ ] API 변경이면 `api/` 문서를 읽는다.
- [ ] DB 변경이면 `data/` 문서를 읽는다.
- [ ] 되돌리기 어려운 결정이면 `decisions/`에 ADR이 필요한지 판단한다.

## 작업 중

- [ ] 코드가 문서의 경계와 정책을 벗어나면 이유를 기록한다.
- [ ] 불확실한 판단은 `DEV_LOG.md`나 ADR proposed 상태로 남긴다.

## 작업 후

- [ ] 바뀐 코드와 관련 문서가 일치하는지 확인한다.
- [ ] 새 API/DTO/DB 컬럼/상태/정책을 문서에 반영한다.
- [ ] 품질 검증 명령 결과를 확인한다.
- [ ] 남은 위험이나 보류한 판단을 기록한다.

## 문서 갱신 매핑

| 변경 내용 | 확인/수정할 문서 |
| --- | --- |
| 도메인 필드/상태/메서드 | `domains/{domain}.md`, `data/schema.md` |
| JPA Entity/마이그레이션 | `data/schema.md`, `data/constraints-and-indexes.md`, 관련 `domains/` |
| Controller/DTO | `api/`, 관련 `policies/` |
| 인증/인가 | `policies/auth-and-user-sync.md`, `architecture/runtime-flows.md` |
| 계층/패키지 구조 | `architecture/layer-boundaries.md`, ADR |
| 외부 AWS 서비스 연동 | `architecture/overview.md`, `architecture/deployment-view.md`, 관련 `policies/` |
