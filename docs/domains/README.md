# Domain Docs

도메인 문서는 코드의 도메인 모델과 DB 모델 사이의 의미 차이를 기록한다.

## 파일 역할

| 파일 | 역할 | 업데이트 기준 |
| --- | --- | --- |
| `_template.md` | 도메인 문서 작성 템플릿 | 템플릿 자체를 개선할 때 |
| `user.md` | User 도메인, Cognito 매핑, 상태 | User 필드, 상태, 동기화 방식이 바뀔 때 |
| `project.md` | Project와 ProjectMember 관계 | 프로젝트 소유/멤버십 규칙이 바뀔 때 |
| `apikey.md` | API Key 생명주기와 보안 속성 | 발급, 폐기, hash/prefix 정책이 바뀔 때 |
| `asset.md` | Asset과 AssetVariant 관계 | 업로드, 변환본, 삭제, 상태 규칙이 바뀔 때 |
| `processing.md` | ProcessingLog와 비동기 처리 상태 | Lambda/SQS 처리 결과 모델이 바뀔 때 |

## 작성 원칙

- 도메인 모델 필드와 DB 컬럼을 구분해서 쓴다.
- 상태 전이는 코드 메서드와 연결해서 쓴다.
- 외부 시스템의 식별자는 내부 ID와 분리해서 쓴다.
- “왜 VO인지”, “왜 Entity인지”는 짧게 남긴다.
