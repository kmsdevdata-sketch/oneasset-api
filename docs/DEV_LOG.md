# DEV_LOG

## 2026-07-08 - project-structure 작업 
### 해야할 작업 
- 도메인 패키지내의 구조 확립 
  - asset / project / user 세개 도메인 패키지로 나눔 
  - ERD확인해보면 PROJECT_MEMBERS / ASSET_VARIANTS / API_KEYS 가 있기는 한데 
  - 도메인으로 나뉘는게 아니라 VO형태로 넣는게 맞다고 판단하였음 
    - 생각전환  
    - API_KEY 는 id,status,lastUsedAt등 생명주기가 있어 Entity 또는 도메인 모델 성격이 강함 
    - PROJECT_MEMBERS 도 role과 가입 이력등 단순 VO보다는 join entity 성격이 강함 
    - ASSET_VARIANTS 는 asset하위 객체로 둘수는 있지만 최소한 영속화 대상 child entity가 더 어울림 
- 전체 헥사고날 구조 확립 
  - adapter / application / domain 세개의 구조로 나눔 
  - 현재 프로젝트 규모에서는 계층 기준으로 탐색하는 것이 더 직관적이라고 판단하였다.
  - application은 도메인별 하위 패키지로 구성하여 유스케이스를 관리한다.

## 2026-07-08 - domain-model 작업
### 해야할 작업 
- ERD 확인을 통한 도메인 모델 속성 적합성 판단
- 도메인 모델 작성 
- 도메인 모델별 책임범위 파악을 통한 메서드 작성 
  - User
    - id는 vo로 분리하여 해당 아이디생성 메서드책임 vo(UserID)로 이전
    - 일단 애매했던 점은 UserOauth,UserProfile분리가 필요한가라는것임 
      - 기존에는 자체 구현 Oauth2연결을 하였는데 이번에는 Cognito사용예정이라 정확히 어떤 플로우로 진행되는지에 대한 이해부족
      - 우선은 이전 어플리케이션은 분리에대한 정확한 이점이 뭔지 파악이 되지않을정도로 (io이득이 있는지 체감이 안될정도로 트래픽적었음 오히려 분리하면 초반 설계 복잡도 증가)
      - 일단은 구현 복잡도 고려 분리하지 않고 하나로 통합 
    - UserId는 UUID사용고려 VO로 분리 
    - cognitoSub - 정확히 어떤 역할로 ERD설계에 포함되었는지 모르겠음 관련 항목 찾아봐도 정확히 서버측에서 소지해야할 정보인가?싶음
    - email - VO분리를 고려했지만 웹에서 찾아본 AWS응답 형식을 보면 아마도 Validation과정은 모두 거쳤을 거라고 판단되어 우선은 String
    - name - VO분리는 고려하지 않았지만 nickname같이 내부 정책에 따라 생성전략을 다르게 가져가지 않아도 되서 우선 String
    - 생성자는 private으로 막은뒤에 create메서드 사용하여 유저 생성시 메서드 생성 강제 - 이것도 사실 나혼자 작업하는데 필요한가 싶기도함  
    - 재구성 메서드 만들어 추후 엔티티 매핑 고려 
      - new user로 만드는 이유는 다른 어플리케이션(ex.3Layer)과 달리 도메인모델은 영속성을 갖지 않기 때문에 새로생성하는것으로 이해하는중
    - 그외 메서드는 간단한 유저 상태변경 관련 메서드 
  - Project
    - 특별한 사항은 없는걸로 학인된다 기존 User도메인과 비슷한 형식으로 작성해나가면 될듯함 
    - 책임도 단지 하나의 프로젝트를 관리하는것이기 때문에 인텔리전시한 도메인메서드가 필요하지는 않은걸로 보임 
    - requiredText같은 메서드는 공용 유틸로 분리하는게 나을것으로 판단 

### 확인한것
#### Cognito와 LazySync에 대해서 
- cognitoSub,email,name 을 회원가입시점에 어떻게 알수있나? Cognito를 사용해서 일반적인 OAuth과정과는 다르지 않을까 싶었음 
- 해답은 실제로 알수없다(물론 Lambda사용하면 되는데 복잡도 수직상승)
- LazySync기법을 사용해서 실제 가입시점에 User를 생성하는것이 아닌 유저의 첫진입(첫사용)시점에 생성하면 된다
```text
회원가입
↓
Cognito User 생성
↓
(아무것도 안 함)
↓
나중에 로그인해서
OneAsset API 처음 호출
↓
DB 조회
↓
없으면 INSERT
```
- Cognito User - 이건 AWS가 관리 : 인증
- OneAsset User - 이건 내가 관리 : 인가 
- AWS 측에서 제공하는 JWT에 정보가 많이 들어있다고함
```json
{
  "sub": "abc123",
  "email": "minseo@test.com",
  "name": "김민서"
}
```
- 우리는 이거 받아서 필터단에서 findOrCreateUser() 요런거 실행해주면 된다 : LazySync니까 따로 생성관리보다 필터단에서 거치는게 안전

## 2026-07-09 - domain-model 작업
### 나머지 도메인 모델링
#### ApiKey
- 역할: 프로젝트에서 해당되는 API키이며 대시보드 사용이 아닌 개발자 어플리케이션 사용용도 
- 주요 속성:
- VO로 뺄 것: ID만 VO로 빼면 될듯한데 key_hash가 실질적인 저장값이면 key_hash도 VO로 빼서 해시 변경책임을 가져가도 나쁘지않을지도 ? 
- 어차피 도메인 엔진에서 변경을 하긴해야될테니까 한번 고려해보자 ! - 그리고 해시에 salt사용할거니까 참고(근데 salt를 마지막에 집어넣는것보다 중간값에 아예 껴버리는게 낫지않나?)
- 상태: 상태는 활성화 , 취소(삭제) 정도면 될거같음 
- 책임 메서드: 책임메서드는 당연히 필드값별 검증과 만료 revoke전환 


#### ProjectMember
- 모두 기존 패턴과 동일하게 가고 role같은 경우에 enum으로 가는게 낫지않을까? 
- 아니면 String파싱? role이 있다면 어떤 롤을 둘건지 아니면 사용자가 그냥 생성을하고 권한부여형식으로 갈건지 한번 상의 필요 

#### ASSET_VARIANTS
- 정형화된 방식그대로 사용 

## 2026-07-09 - global-exception-handler
### 공통예외 응답구조 생성 
- BaseException 과 ErrorCode는 기존에 자주 사용하던 유형대로 생성하였음 
- 그러던중 내가 이 포맷을 어떻게하다 사용하게 되었지를 생각해보니 사실 별생각이없이 다른 프로젝트에서 보게된 좋아보이는 형식을 따라하게되었다 
- 그래서 찾아보니 좋은 글을 발견함 [좋은글](https://infinitecode.tistory.com/142) 단순히 좋아보이는게 아닌 
- 글에서 말하는 문제점이 내가 이전 프로젝트에서 겪은 문제점과 동일하였음 1.예외 클래스 수 폭발 2.레이어 책임 분산 [설명글](https://apidog.com/kr/blog/what-is-rfc-9457-api-error-responses/)
- 우선 에러코드 인터페이스를 만들고 표준필드 게터메서드를 넣어 필드생성 강제화(enum)
  - code : 클라쪽 분기처리용 내부 식별자
  - title : 에러 종류의 고정 요약 
  - status : Http상태 오류 
  - typeUri : 에러 종류 식별 URI
### 에러 응답형식 포맷 
- 에러 발생시 응답구조 포맷 작성을 하기위해 공통응답 포맷으로 활용할 ApiResponse 생성 
- 성공(응답포함),성공(응답 미포함),실패(예외포함) 형태로 3개 메서드 생성 
- 실패 - 예외가 터진 경우 ErrorResult DTO활용 
  - ErrorResult의 경우 of는 모든 필드구성 포함한 경우만 우선적으로 작성함 
- **수정사항**
  - ErrorCode에 typeUri보다는 type이 자연스럽다는 지적 수용 
  - ErrorResult는 추후 사용성 고려하여 필드 수정 
### 예외처리 핸들러 고민 
- 예외처리 핸들러 (RestControllerAdvice)를 어디에 둘지가 상당히 고민이였음 
- 결론적으로는 인바운드 어댑터가 적절하다는 답변을 얻음 
  - config와 같이 그냥 별도 패키지로 분리할까 생각도 하였지만 GraphQL,Kafka같은 경우 예외처리 방식이 다르다는걸 깨닳음
  - 당장은 쓸일이 없지만 예외처리는 무조건 RestControllerAdvice로 처리하는거 아닌가 = Http응답만 신경쓰고있었음
- 예외처리 과정이 예외처리 라기보다 애플리케이션에서 던져진 예외를 HTTP응답으로 변환하는 역할이라는걸 리마인드 

## 2026-07-09 - persistence-adapter
- 우선은 User만 작업하였음 
- 어댑터 생성과정에서 포트 상속 받게할까(LoadUserPort?등등) 고민하였는데 라이트 헥사고날로 가기위해서 굳이 영속성 관련은 포트 생성 안해도 괜찮다고 판단 
- save : 저장 과정에서는 엔티티의 from()메서드 사용하여 도메인모델 엔티티로 매핑하여 저장
- load : 로드시에는 아이디와 상태(Active)확인하여 가져온뒤 toDomain메서드 활용하여 모델로 매핑 
- 작업 과정중에 UserRole이 없다는걸 깨닳음 추가 작업이 필요할것으로 예상된다
- 후속 확인 결과 `UserStatus`는 계정 상태이고 관리자 페이지 접근 권한과 역할이 다르므로 `UserRole(USER, ADMIN)`을 별도 축으로 추가한다.
- `users.id` 마이그레이션과 JPA Entity의 PK 컬럼명이 맞지 않아 `UserEntity.id -> users.id`로 정렬한다.

## 2026-07-10 - persistence-adapter
- 기존 User 영속성 어댑터 패턴을 유지하여 Project, ProjectMember, ApiKey, Asset, AssetVariant, ProcessingLog 영속성 어댑터를 추가하였다.
- 현재 단계에서는 복잡도를 낮추기 위해 별도 port 인터페이스를 만들지 않고, application 계층 구현 시 의존성과 테스트 난이도가 커지는 시점에 분리를 재검토한다.
- 각 어댑터는 JPA Entity의 `from(domain)` / `toDomain()` 변환을 통해 도메인 모델과 영속성 모델을 분리한다.
- 테스트는 mock repository를 직접 주입하여 `when`, `verify` 중심으로 얇게 작성하였다.
- ProcessingLog는 Asset의 현재 상태를 판단하는 모델이 아니라 비동기 처리 과정의 이벤트 이력이다.
- ProcessingLog의 이번 구현 범위는 테이블, 도메인 모델, JPA Entity, Repository, Persistence Adapter, 어댑터 단위 테스트까지만 포함한다.
- ProcessingLog를 저장하는 application service, API 응답 노출, Lambda/SQS 실제 연동, 실패/재시도 정책은 이번 범위에 포함하지 않는다.
