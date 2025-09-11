# 책담책담 — Backend (Spring Boot 3, MyBatis, MySQL)

개인 책장 관리 및 독서 기록 서비스의 **백엔드**입니다.
Google OAuth2 로그인 성공 시 서버가 JWT를 발급하여 FE와 연동합니다.



---

## 링크
- Frontend Repo: https://github.com/suve1y34/suinprjtvue
- Backend Repo: https://github.com/suve1y34/suinprjt

---

## 기능
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

## API 목록

### 인증/사용자
- `GET /api/users/me` → 내 정보 조회
- `PATCH /api/users/me` → 닉네임 업데이트
- `/oauth2/authorization/google` → OAuth2 로그인 시작 (성공 시 FE 콜백으로 JWT 전달)

### 책장
- `POST /api/shelves/items/list`
- `POST /api/shelves/item/add`
- `POST /api/shelves/item/update`
- `POST /api/shelves/item/delete`

### 책/공개 메모
- `POST /api/books/detail`
- `POST /api/books/memos/public/list`

---
