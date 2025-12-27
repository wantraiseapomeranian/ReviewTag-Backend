# ReviewTag Backend

## 📖 프로젝트 소개

**ReviewTag**는 영화와 TV 프로그램에 대한 열정을 가진 사용자들을 위한 종합 커뮤니티 플랫폼의 백엔드 서버입니다. 단순한 리뷰 기록을 넘어, TMDB (The Movie Database) API와 연동하여 신뢰도 높은 콘텐츠 정보를 제공하고, 사용자들의 적극적인 참여를 유도하기 위해 다채로운 게이미피케이션(Gamification) 요소를 도입했습니다.

본 프로젝트는 사용자들이 콘텐츠를 평가하고, 의견을 나누며, 커뮤니티 활동을 통해 보상을 얻는 선순환 구조를 구축하는 것을 목표로 합니다. 이를 통해 사용자들이 즐겁게 머무를 수 있는 활기찬 온라인 공간을 제공하고자 합니다.

---

## ✨ 상세 기능 목록

### 🎬 콘텐츠 (TMDB 연동)
- **정보 제공**: 영화, TV 시리즈의 상세 정보, 캐스팅, 포스터, 예고편 등 방대한 데이터를 제공합니다.
- **검색**: 제목 기반으로 원하는 콘텐츠를 쉽게 찾을 수 있습니다.
- **랭킹**: 인기 있는 콘텐츠, 높은 평점을 받은 콘텐츠 등의 랭킹을 제공합니다.
- **시청목록**: 관심 있는 영화/TV 프로그램을 시청목록에 추가하여 나중에 쉽게 찾아볼 수 있습니다.

### 👤 사용자 및 인증
- **인증**: JWT(Access Token, Refresh Token)를 사용한 안전하고 효율적인 인증 시스템을 구현합니다.
- **회원가입**: 아이디, 비밀번호, 이메일, 닉네임으로 가입하며, 이메일 인증을 통해 계정을 활성화합니다.
- **로그인/로그아웃**: 토큰 기반의 로그인 및 서버에서 토큰을 무효화하는 로그아웃을 지원합니다.
- **프로필 관리**: 마이페이지에서 닉네임, 비밀번호 등 개인 정보를 수정하고, 구매한 아이콘으로 프로필을 꾸밀 수 있습니다.
    - 랜덤 아이콘 뽑기 기능을 통해 아이콘을 획득할 수 있습니다.
    - 획득한 아이콘을 프로필에 장착하거나 장착 해제할 수 있습니다.
- **활동 조회**: 마이페이지에서 본인이 작성한 리뷰, 게시글, 획득한 포인트 내역 등을 일목요연하게 확인할 수 있습니다.

### ✍️ 커뮤니티
- **리뷰**:
    - 각 콘텐츠에 별점과 함께 텍스트 리뷰를 작성, 수정, 삭제할 수 있습니다.
    - 다른 사용자의 리뷰에 '좋아요'를 눌러 공감을 표현할 수 있습니다.
- **게시판**: 자유롭게 글을 작성하고 소통할 수 있는 공간입니다.
    - 게시글에 첨부파일을 업로드하고 다운로드할 수 있습니다.
- **댓글**: 게시글과 리뷰에 댓글을 달아 의견을 나눌 수 있습니다.
- **신고 시스템**: 부적절하거나 스팸성인 게시글, 리뷰, 퀴즈를 신고하여 커뮤니티 환경을 관리합니다.

### 🧩 퀴즈 시스템
- **하트 시스템**:
    - 퀴즈를 풀기 위해 필요한 하트 시스템입니다.
    - 매일 자동으로 하트가 5개까지 리필되며, 하트가 부족하면 퀴즈를 풀 수 없습니다.
    - 포인트 상점에서 하트를 추가로 구매할 수 있습니다.
- **컨텐츠 관련 퀴즈**:
    - 사용자들이 특정 영화/TV 프로그램에 대한 퀴즈를 직접 제작하고 공유할 수 있습니다.
    - 특정 콘텐츠에 대해 랜덤으로 5문제가 출제되며, 정답을 맞히면 정답 1개당 20포인트를 획득합니다.
    - 본인이 만든 퀴즈나 이미 정답을 맞춘 퀴즈는 제외되어 새로운 문제만 풀 수 있습니다.
    - 퀴즈를 풀기 위해서는 하트가 필요하며, 퀴즈 제출 시 하트가 소모됩니다.
    - 영화별 퀴즈 랭킹을 확인하여 자신의 순위를 확인할 수 있습니다.

### 🏆 포인트 및 게이미피케이션
- **포인트**:
    - **획득**: 리뷰 작성, 퀴즈 정답, 출석 체크, 일일 퀘스트 완료 등 다양한 활동으로 포인트를 얻습니다.
    - **사용**: 획득한 포인트로 상점에서 아이템(예: 프로필 아이콘)을 구매할 수 있습니다.
- **상점**:
    - 관리자가 등록한 다양한 아이콘 아이템을 전시하고 판매합니다.
    - 사용자는 포인트를 사용해 아이콘을 구매하고 인벤토리에 보관할 수 있습니다.
    - 상점 아이템에 찜하기 기능을 제공하여 나중에 구매할 수 있도록 관리합니다.
    - 구매한 아이템을 다른 사용자에게 선물할 수 있습니다.
- **인벤토리**:
    - 구매한 아이템들이 보관되는 개인 보관함입니다.
    - 아이템을 사용하여 프로필에 적용하거나 기능을 활용할 수 있습니다.
    - 룰렛 티켓 아이템을 사용하면 포인트를 획득할 수 있는 룰렛 게임을 즐길 수 있습니다.
- **랭킹**: 누적된 포인트를 기준으로 주간/월간/전체 사용자 랭킹을 실시간으로 제공하여 경쟁과 재미를 유도합니다.
- **일일 퀘스트 및 출석**: 매일 접속하여 간단한 퀘스트를 완료하거나 출석 체크를 하여 꾸준한 보상을 받을 수 있습니다.
- **데일리 퀴즈**: 
    - 매일 영화/TV와 관련된 새로운 퀴즈가 출제되며, 정답을 맞히면 포인트를 획득합니다.

### 🛠️ 관리자
- **대시보드**: 서비스의 주요 현황(신규 가입자, 신고 접수 등)을 확인할 수 있습니다.
- **사용자 관리**: 전체 사용자 목록을 조회하고, 특정 사용자의 권한을 관리하거나 제재할 수 있습니다.
- **콘텐츠 관리**: 접수된 신고 내역을 검토하고, 문제가 있는 콘텐츠를 삭제하거나 사용자를 제재합니다.

---

## 🏛️ 아키텍처 및 인증 흐름

### 3-Tier Layered Architecture
- **Controller Layer (`restcontroller`)**: HTTP 요청을 수신하고 응답을 반환합니다. 데이터 유효성 검사 후 서비스 계층으로 요청을 전달합니다.
- **Service Layer (`service`)**: 핵심 비즈니스 로직을 처리합니다. 여러 DAO를 조합하여 하나의 트랜잭션으로 작업을 수행합니다.
- **Data Access Layer (`dao`, `mybatis`)**: 데이터베이스와의 상호작용을 담당합니다. MyBatis를 사용하여 SQL 쿼리를 실행하고 결과를 매핑합니다.

### JWT 기반 인증 흐름
1.  **로그인**: 사용자가 아이디/비밀번호로 로그인을 요청합니다.
2.  **토큰 발급**: 서버는 사용자 인증 성공 시, **Access Token**과 **Refresh Token**을 생성하여 사용자에게 반환합니다.
3.  **API 요청**: 사용자는 이후 API 요청 시 HTTP 헤더의 `Authorization` 필드에 Access Token을 담아 보냅니다.
4.  **토큰 검증**: 서버의 `TokenParsingInterceptor`가 모든 요청을 가로채 Access Token의 유효성을 검증합니다.
5.  **토큰 갱신**: Access Token이 만료되었을 경우, 사용자는 Refresh Token을 사용하여 새로운 Access Token을 발급받을 수 있습니다. 이 과정은 `TokenRenewalInterceptor`에서 처리됩니다.

---

## 🛠️ 기술 스택

- **Backend**: Java 21, Spring Boot 3.5.7, Spring Security
- **Database**: Oracle Database, MyBatis, Spring Data JDBC
- **Authentication**: JSON Web Token (JWT) - Access/Refresh Token 방식
- **API Documentation**: SpringDoc (Swagger UI)
- **Build Tool**: Maven
- **Key Dependencies**: Lombok, Spring Web/WebFlux, JJWT, Spring Mail Sender, Jsoup

---

## 🚀 시작하기

### 1. 사전 요구사항
- **Java**: JDK 21
- **Build Tool**: Maven 3.6+
- **Database**: Oracle Database 11g 이상

### 2. 환경 설정 상세
`src/main/resources/application.properties` 파일의 각 항목에 대한 설명입니다.

```properties
# === Database Configuration ===
# Oracle DB 연결을 위한 JDBC URL
spring.datasource.url=jdbc:oracle:thin:@your_db_host:1521:XE
# DB 사용자 계정
spring.datasource.username=your_db_username
# DB 사용자 비밀번호
spring.datasource.password=your_db_password

# === JWT (JSON Web Token) Configuration ===
# 토큰 서명 및 검증에 사용될 비밀 키 (충분히 길고 복잡하게 설정)
jwt.secret=your-very-secret-key-for-jwt-token-generation-and-validation

# === TMDB API Configuration ===
# The Movie Database에서 발급받은 API Key
tmdb.api.key=your_tmdb_api_key

# === Spring Mail Configuration ===
# 사용할 이메일 서비스의 SMTP 서버 주소
spring.mail.host=smtp.gmail.com
# SMTP 서버 포트 (TLS)
spring.mail.port=587
# 이메일 계정
spring.mail.username=your_email@gmail.com
# 이메일 앱 비밀번호 (Gmail의 경우 2단계 인증 후 앱 비밀번호 사용)
spring.mail.password=your_email_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 3. 빌드 및 실행
```bash
# Maven을 사용하여 프로젝트를 클린하고 빌드합니다.
./mvnw clean install

# Spring Boot 애플리케이션을 실행합니다.
./mvnw spring-boot:run
```
서버가 시작되면 `http://localhost:8080`에서 애플리케이션에 접근할 수 있습니다.

---

## 📚 API 문서 및 주요 엔드포인트

서버 실행 후, **[http://localhost:8080/api.html](http://localhost:8080/api.html)** 에서 모든 API의 상세 명세와 테스트 기능을 제공하는 Swagger UI를 확인할 수 있습니다.

### 주요 엔드포인트 예시
| Method | URI | 설명 | 인증 |
| :--- | :--- | :--- | :--- |
| `POST` | `/member/join` | 회원가입 | 없음 |
| `POST` | `/member/login` | 로그인 (토큰 발급) | 없음 |
| `GET` | `/member/mypage` | 마이페이지 정보 조회 | 필요 |
| `POST` | `/cert/send` | 이메일 인증번호 전송 | 없음 |
| `POST` | `/cert/check` | 이메일 인증번호 확인 | 없음 |
| `GET` | `/tmdb/movie/{movie_id}` | 영화 상세 정보 조회 | 없음 |
| `POST` | `/watchlist/` | 시청목록 추가 | 필요 |
| `GET` | `/watchlist/check` | 시청목록 확인 | 필요 |
| `POST` | `/review` | 리뷰 작성 | 필요 |
| `GET` | `/review/list/{contents_no}` | 특정 콘텐츠의 리뷰 목록 조회 | 없음 |
| `GET` | `/board/list` | 게시글 목록 조회 | 없음 |
| `POST`| `/board` | 게시글 작성 | 필요 |
| `GET` | `/attachment/download` | 첨부파일 다운로드 | 없음 |
| `GET` | `/heart/` | 하트 정보 조회 | 필요 |
| `POST`| `/heart/charge` | 하트 충전 | 필요 |
| `GET` | `/quiz/daily` | 오늘의 퀴즈 조회 | 필요 |
| `POST`| `/quiz/solve` | 퀴즈 정답 제출 | 필요 |
| `GET` | `/quiz/game/{contentsId}` | 컨텐츠별 퀴즈 게임 (5문제) | 필요 |
| `POST`| `/quiz/` | 퀴즈 등록 | 필요 |
| `POST`| `/quiz/log/submit` | 퀴즈 정답 제출 및 포인트 획득 | 필요 |
| `GET` | `/quiz/log/ranking/{contentsId}` | 컨텐츠별 퀴즈 랭킹 조회 | 없음 |
| `GET` | `/point/main/store/list` | 상점 아이템 목록 조회 | 필요 |
| `POST`| `/point/main/store/buy` | 아이템 구매 | 필요 |
| `GET` | `/point/main/store/inventory/my` | 내 인벤토리 조회 | 필요 |
| `POST`| `/point/main/store/inventory/use` | 아이템 사용 | 필요 |
| `POST`| `/point/main/store/wish` | 상점 아이템 찜하기/취소 | 필요 |
| `GET` | `/icon/my` | 내 아이콘 목록 조회 | 필요 |
| `POST`| `/icon/draw` | 랜덤 아이콘 뽑기 | 필요 |
| `POST`| `/icon/equip` | 아이콘 장착 | 필요 |
| `GET` | `/point/quest/list` | 일일 퀘스트 목록 조회 | 필요 |
| `POST`| `/point/quest/reward` | 일일 퀘스트 보상 수령 | 필요 |

---

## 📁 프로젝트 구조

```
.
├── src
│   ├── main
│   │   ├── java/com/kh/finalproject
│   │   │   ├── aop            # 인터셉터: 토큰 검증, 권한 체크 등 AOP 로직
│   │   │   ├── configuration  # Spring Security, JWT, TMDB 등 핵심 설정
│   │   │   ├── dao            # MyBatis 매퍼 인터페이스 (DB와 1:1)
│   │   │   ├── dto            # Data Transfer Object: 계층 간 데이터 전송용 객체
│   │   │   ├── error          # 전역 예외 처리 및 커스텀 예외 클래스
│   │   │   ├── restcontroller # API 엔드포인트 정의 (HTTP 요청/응답)
│   │   │   ├── service        # 비즈니스 로직 구현 (트랜잭션 단위)
│   │   │   └── vo             # Value Object: View에 특화된 데이터 표현 객체
│   │   └── resources
│   │       ├── mybatis        # SQL 쿼리를 담은 MyBatis XML 매퍼
│   │       └── application.properties # 애플리케이션의 주 설정 파일
│   └── test                   # 단위 테스트 및 통합 테스트 코드
├── pom.xml                    # Maven 의존성 및 빌드 설정
└── readme.md                  # 프로젝트 문서
```