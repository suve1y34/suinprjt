# 책담책담 — Backend (Spring Boot 3, MyBatis, MySQL)

개인 책장 관리 및 독서 기록 서비스의 **백엔드**입니다.
Google OAuth2 로그인 성공 시 서버가 JWT를 발급하여 FE와 연동합니다.



---

## 링크
- Frontend Repo: https://github.com/suve1y34/suinprjtvue
- Backend Repo: https://github.com/suve1y34/suinprjt

---

## 주요 기능
- Google OAuth2 로그인 → JWT 발급 (Stateless)
- 책장 조회 (상태/연/월 필터)
- 책 추가 (ISBN13 기준 중복 검사 후 upsert)
- 독서량/상태 갱신
- 메모 작성/수정 (공개/비공개 전환)
- 공개 메모 목록 (Cursor 기반 페이지네이션)
- 마이페이지 (닉네임 인라인 수정)

---

## 기술 스택
- Java 17
- Spring Boot 3
- Spring Security (JWT, OAuth2 – Google)
- MyBatis, MySQL
- Lombok
- Maven

---

## 요구 사항
- JDK **17+**
- Maven **3.9+**
- MySQL **8.0+** (로컬 또는 호스트 DB)

---

## DB 스키마

**book**
- book_id (PK), isbn13_code (UQ), title, author, pages, publisher, pub_date, cover_image_url, created_datetime, modified_datetime

**bookshelf**
- bookshelf_id (PK), user_id (FK), created_datetime

**shelf_book**
- shelf_book_id (PK), bookshelf_id (FK), book_id (FK), current_page, reading_status, memo, memo_visibility(PUBLIC/PRIVATE), added_datetime, modified_datetime

**user**
- user_id (PK), user_email (UQ), user_password(NULL for SNS-only), user_name, nickname(UQ), created_datetime, modified_datetime

---

## API 개요

### 인증/사용자
- `GET /api/users/me` → 내 정보 조회
- `PATCH /api/users/me` → 닉네임/연락처 업데이트
- `GET /api/users/goal-progress` → 내 책장 통계 조회
- `/oauth2/authorization/google` → OAuth2 로그인 시작 (성공 시 FE 콜백으로 JWT 전달)

### 알라딘
- `POST /api/search/aladin` → 알라딘 책 검색

### 책장
- `POST /api/shelves/books/add` → 책 추가
- `POST /api/shelves/books/me` → 내 책장 조회
- `POST /api/shelves/books/list` → 내 책장의 책 조회
- `POST /api/shelves/books/update` → 책 수정
- `POST /api/shelves/books/remove` → 책 삭제
- `GET /api/shelves/books/stats` → 내 책장 통계 조회

### 책/공개 메모
  `POST /api/books/reviews/public/list` → 공개 리뷰 조회

---

## 설정 (application.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/book_shelf?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=utf8
    username: root
    password: root
  jackson:
    serialization:
      write-dates-as-timestamps: false
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher

# CORS (필요 시 최소 설정)
app:
  cors:
    allowed-origins:
      - http://localhost:5173
      - http://127.0.0.1:5173

# JWT (간단 예시)
app:
  security:
    jwt:
      secret: "change-me-in-prod-please"
      expiration-minutes: 43200  # 30일
```

---

## 의존성 (pom.xml)
필수 항목만 예시로 적었습니다.

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  <dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
  </dependency>
  <dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.17.2</version>
  </dependency>
  <dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
  </dependency>
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
  </dependency>
  <!-- JWT 구현체 선택: jjwt 또는 jose4j 등 프로젝트에 맞춰 사용 -->
</dependencies>
```

---

## 외부 연동 - Aladin API
도서 검색은 Aladin Open API를 사용합니다.
- 기본 사용 엔드포인트: `ItemSearch` (제목/저자 검색), 필요 시 `ItemLookUp` (상세 조회)

### 환경변수
```env
ALADIN_TTB_KEY=<발급키>
ALADIN_BASE_URL=https://www.aladin.co.kr/ttb/api
```

### 동작 방식
- `aladin/AladinClient` → `service/BookInternalService` → `controller` 순으로 호출
- 검색 결과에서 `isbn13`, `title`, `author`, `pages` 등을 추출해 내부 모델로 매핑

---

## 실행 (Quick Start)

```bash
# 1) 의존성/빌드
./mvnw clean package -DskipTests

# 2) 실행
java -jar target/*.jar

# 또는
./mvnw spring-boot:run
```

### 프론트엔드 연동
- FE `VITE_API_BASE_URL` → `http://localhost:8080` 으로 설정
- CORS 허용 오리진에 `http://localhost:5173` 추가(위 YAML 참고)

---

## Tip
- 리뷰/메모는 서버에서 **Jsoup**으로 HTML 제거 (XSS 방지)
- 권한 체크: “내 책장/내 항목만” 접근 가능하도록 Service/Mapper에서 검증

---