# Deletion Policy

## 역할

도메인별 삭제 정책을 기록한다.

## 현재 기준

| 대상 | 정책 | 이유 |
| --- | --- | --- |
| Project | soft delete 후보 | 하위 Asset/API Key 관계 보존 필요 |
| API Key | hard delete 대신 `REVOKED` | 감사와 보안 추적 |
| Asset | soft delete 후보 + S3 삭제 별도 처리 | metadata와 object 삭제 실패 보정 필요 |
| AssetVariant | Asset 삭제 정책에 따른다 | parent Asset과 생명주기 연동 |
| User | `WITHDRAWN` 상태 | Cognito와 local User 매핑 보존 |

## Asset 삭제 순서 후보

```text
API request
-> mark Asset deleted or deleting
-> delete S3 objects
-> mark deletedAt
```

S3 삭제 실패와 DB 반영 실패가 나뉠 수 있으므로, 실제 구현 전 보정 정책을 정한다.

## API 응답

삭제 API는 MVP 계약상 다음 형태를 따른다.

```json
{
  "success": true,
  "data": {
    "deleted": true
  }
}
```

## 업데이트 시점

- `deletedAt` 컬럼이 추가될 때
- Asset, Project, API Key 삭제 API가 구현될 때
- 삭제 정책이 도메인별로 달라질 때
