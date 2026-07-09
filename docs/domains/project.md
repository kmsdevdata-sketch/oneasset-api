# Project Domain

## 역할

Project와 ProjectMember의 소유, 멤버십, 권한 규칙을 기록한다.

## 핵심 판단

Project는 OneAsset 리소스의 소유 단위다.

ProjectMember는 단순 VO가 아니라 Project와 User 사이의 membership 생명주기를 가진 모델로 본다. role, 생성 시각, 향후 초대/권한 정책이 들어갈 수 있기 때문이다.

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

## 업데이트 시점

- 프로젝트 생성/조회 유스케이스가 구현될 때
- 멤버십 또는 권한 정책이 바뀔 때
- DB 관계와 인덱스가 바뀔 때
