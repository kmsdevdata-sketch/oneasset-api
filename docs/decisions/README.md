# Architecture Decision Records

이 디렉터리는 아키텍처적으로 중요한 결정을 기록한다.

## 언제 ADR을 쓰는가

- 구조, 계층, 도메인 분리 기준이 바뀔 때
- 보안, 인증, 권한 정책이 바뀔 때
- 외부 시스템이나 인프라 선택이 바뀔 때
- API 계약이나 DB 무결성에 큰 영향을 주는 선택을 할 때
- 나중에 되돌리기 어렵거나 팀원이 이유를 알아야 하는 선택일 때

## 파일명 규칙

```text
NNNN-short-title.md
```

예시:

```text
0001-use-cognito-lazy-user-sync.md
```

## 상태

- `proposed`: 리뷰 전
- `accepted`: 적용하기로 결정
- `rejected`: 채택하지 않음
- `superseded`: 새 ADR로 대체됨

## 운영 규칙

- accepted/rejected ADR의 본문은 의미 변경하지 않는다.
- 결정이 바뀌면 새 ADR을 만들고 기존 ADR을 `superseded`로 표시한다.
- 코드 리뷰 때 관련 ADR을 확인한다.
