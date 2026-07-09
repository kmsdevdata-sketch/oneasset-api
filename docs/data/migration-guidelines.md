# Migration Guidelines

## 역할

Flyway 마이그레이션 작성 규칙을 기록한다.

## 파일명 규칙

```text
V{version}__{description}.sql
```

예시:

```text
V1__create_users_table.sql
V2__create_projects_table.sql
```

## 작성 순서

1. `docs/data/schema.md`에 변경 의도를 먼저 적는다.
2. 마이그레이션 SQL을 작성한다.
3. 로컬 DB에 적용한다.
4. 실제 DB 구조와 문서를 다시 대조한다.

## 원칙

- MVP1 초기에는 destructive migration도 로컬에서 가능하지만, main/prod 반영 후에는 수정 대신 새 마이그레이션을 추가한다.
- enum은 우선 varchar + application enum으로 시작한다.
- FK와 unique 제약은 애플리케이션 검증과 별도로 DB에도 둔다.
- seed data는 필요해질 때 별도 정책을 정한다.

## 업데이트 시점

- 첫 마이그레이션 작성 전
- 마이그레이션 운영 규칙이 바뀔 때
