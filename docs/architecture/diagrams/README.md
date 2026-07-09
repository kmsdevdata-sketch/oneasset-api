# Architecture Diagrams

이 디렉터리는 Mermaid 기반 다이어그램을 보관한다.

## 파일 역할

| 파일 예시 | 역할 |
| --- | --- |
| `system-context.md` | 사용자, Web, API, Cognito, AWS 서비스 관계 |
| `container-view.md` | 실행/배포 단위와 저장소 관계 |
| `component-api.md` | API 애플리케이션 내부 주요 컴포넌트 |
| `runtime-auth.md` | 로그인과 Lazy Sync 시퀀스 |
| `runtime-asset-upload.md` | Asset 업로드와 비동기 처리 시퀀스 |

## 업데이트 기준

- 텍스트 설명만으로 경계나 흐름이 헷갈릴 때 추가한다.
- 다이어그램은 항상 관련 문서에서 링크한다.
- 그림만 갱신하지 말고 관련 `architecture/`, `policies/`, `domains/` 문서도 함께 확인한다.
