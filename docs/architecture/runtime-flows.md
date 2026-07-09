# Runtime Flows

## 역할

런타임에서 여러 컴포넌트가 어떤 순서로 상호작용하는지 기록한다.

## Dashboard Login and Lazy User Sync

```mermaid
sequenceDiagram
    actor User as Dashboard User
    participant Web as Web Dashboard
    participant Cognito as Cognito User Pool
    participant API as Spring Boot API
    participant DB as PostgreSQL

    User->>Web: Sign up / Sign in
    Web->>Cognito: Managed Login / Auth flow
    Cognito-->>Web: Access token, ID token, refresh token
    Web->>API: GET /me with Bearer JWT
    API->>API: Validate JWT and extract claims
    API->>DB: Find user by cognito_sub
    alt user does not exist
        API->>DB: Create local User
    else user exists
        API->>DB: Optionally sync email/name
    end
    API-->>Web: User response
```

로컬 User 생성은 Cognito 회원가입 순간이 아니라, 인증된 사용자가 OneAsset 보호 API에 처음 진입하는 시점에 수행한다.

## Project and API Key Flow

```mermaid
sequenceDiagram
    actor User as Dashboard User
    participant Web as Web Dashboard
    participant API as Spring Boot API
    participant DB as PostgreSQL

    Web->>API: POST /projects
    API->>DB: Create Project and owner ProjectMember
    API-->>Web: Project
    Web->>API: POST /projects/{projectId}/api-keys
    API->>API: Generate raw key
    API->>API: Create key prefix and hash
    API->>DB: Store prefix/hash only
    API-->>Web: Raw API key once
```

## Asset Upload and Processing Flow

```mermaid
sequenceDiagram
    actor App as Developer App
    participant API as Spring Boot API
    participant DB as PostgreSQL
    participant S3 as S3
    participant SQS as SQS
    participant Lambda as Lambda

    App->>API: POST /v1/assets with X-OneAsset-Api-Key
    API->>DB: Validate API key hash and project
    API->>S3: Upload original file
    API->>DB: Save Asset metadata
    S3-->>SQS: ObjectCreated notification
    API-->>App: assetId, key, status
    SQS->>Lambda: Processing message
    Lambda->>S3: Read original and write variants
    Lambda->>DB: Save AssetVariant and mark Asset READY
```

## 업데이트 시점

- 요청 흐름이 바뀔 때
- 동기/비동기 경계가 바뀔 때
- 외부 시스템 호출 위치가 바뀔 때

## 작성 형식

가능하면 Mermaid sequence diagram을 사용한다.
