# QUALITY_TOOLS.md

# Quality Tools

OneAsset 프로젝트에서 사용하는 코드 품질 관리 도구와 사용 방법을 정리한 문서이다.

모든 기능 개발 후에는 아래 절차를 통해 코드 품질을 확인한다.

---

# Spotless

## 목적

코드 스타일을 자동으로 통일한다.

자동 수행 항목

- Google Java Format 적용
- 사용하지 않는 import 제거
- 줄 끝 공백 제거
- 파일 마지막 개행 추가

## 실행

```bash
./gradlew spotlessApply
```

## 언제 실행하는가?

- 기능 구현 후
- Commit 전

---

# SpotBugs

## 목적

컴파일된 바이트코드를 분석하여 잠재적인 버그를 탐지한다.

예시

- NullPointerException 가능성
- 사용하지 않는 필드
- 잘못된 객체 사용
- 성능 문제

## 실행

```bash
./gradlew spotbugsMain
```

또는

```bash
./gradlew check
```

## 실패 시

리포트를 확인한다.

```
build/reports/spotbugs/main.html
```

---

# JaCoCo

## 목적

테스트가 실제 코드를 얼마나 실행했는지 측정한다.

측정 항목

- Line Coverage
- Branch Coverage

## 실행

```bash
./gradlew test
```

또는

```bash
./gradlew jacocoTestReport
```

## 리포트

```
build/reports/jacoco/test/html/index.html
```

---

# 전체 품질 검사

기능 구현이 완료되면 아래 명령을 실행한다.

```bash
./gradlew clean build
```

확인 항목

- Build Success
- 모든 테스트 통과
- SpotBugs 경고 없음
- JaCoCo 리포트 생성

---

# 개발 체크리스트

기능 개발 완료 후

- [ ] 테스트 코드 작성
- [ ] `./gradlew spotlessApply`
- [ ] `./gradlew clean build`
- [ ] SpotBugs 리포트 확인
- [ ] JaCoCo 리포트 확인

---

# AI 활용 원칙

AI가 생성한 코드도 반드시 직접 검토한다.

특히 아래 항목은 항상 확인한다.

- API 계약 준수
- 예외 처리
- 트랜잭션 범위
- 보안 및 권한
- SQL/JPA 성능
- 동시성 문제