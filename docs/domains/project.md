# Project Domain

## 역할

Project와 ProjectMember의 소유, 멤버십, 권한 규칙을 기록한다.

## 핵심 판단

Project는 OneAsset 리소스의 소유 단위다.

ProjectMember는 단순 VO가 아니라 Project와 User 사이의 membership 생명주기를 가진 모델로 본다. role, 생성 시각, 향후 초대/권한 정책이 들어갈 수 있기 때문이다.

Project 도메인 자체의 책임은 MVP1에서 얇게 유지한다.

- 생성
- DB 복원
- 이름 변경
- 삭제 처리

Project 생성 시 owner ProjectMember 생성은 별도 유스케이스에서 함께 조합한다.

## Slug

slug는 사람이 읽을 수 있고 URL이나 외부 식별에 쓰기 좋은 안전한 문자열이다.

예:

```text
Project name: My Blog
slug: my-blog
```

slug는 Project의 내부 UUID를 대체하지 않는다. 내부 관계와 FK는 `ProjectId`를 사용하고, slug는 UI/URL/조회 편의용 식별자로 사용한다.

MVP1에서는 slug를 Project 도메인 내부에서 직접 생성하지 않는다. slug 생성은 중복 확인과 DB unique 제약 처리가 필요하므로 application 계층 또는 별도 정책 객체에서 만든 값을 `Project.create(name, slug)`에 전달한다.

이름 변경은 slug를 자동 변경하지 않는다. slug 변경은 URL 안정성 문제와 충돌 정책이 필요하므로 후속 정책으로 둔다.

## 도메인 속성 초안

| 모델 | 속성 | 의미 |
| --- | --- | --- |
| Project | `id` | 프로젝트 ID |
| Project | `name` | 사용자 입력 프로젝트 이름 |
| Project | `slug` | URL/식별용 slug |
| Project | `createdAt`, `updatedAt`, `deletedAt` | 생성/수정/삭제 시각 |
| ProjectMember | `id` | membership ID |
| ProjectMember | `projectId` | Project FK |
| ProjectMember | `userId` | User FK |
| ProjectMember | `role` | MVP1 기본 owner/member |
| ProjectMember | `createdAt` | membership 생성 시각 |

## 생성 규칙

- Project 생성 시 생성자를 owner로 하는 ProjectMember를 함께 만든다.
- MVP1에서는 복잡한 초대/권한 모델을 만들지 않고, 기본 owner 중심으로 시작한다.
- slug는 프로젝트 이름에서 생성하되 DB unique 제약과 충돌 처리 정책이 필요하다.

## 공용 검증

`requiredText`류 검증은 도메인 전반에서 반복되므로 `DomainValidator.requireText` 같은 작은 공용 도메인 유틸을 사용한다.

단, 이 유틸은 null/blank 같은 범용 불변식만 다룬다. Project 이름 길이, slug 허용 문자, 예약어 같은 정책은 Project 정책 또는 전용 값 객체로 분리한다.

## 업데이트 시점

- 프로젝트 생성/조회 유스케이스가 구현될 때
- 멤버십 또는 권한 정책이 바뀔 때
- DB 관계와 인덱스가 바뀔 때
