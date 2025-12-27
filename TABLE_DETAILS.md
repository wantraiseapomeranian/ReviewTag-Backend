# 🗂 Database Schema Details

이 문서는 프로젝트의 전체 데이터베이스 테이블 구조와 컬럼 명세를 기술합니다.

---

## 1. 👤 회원 (Member & Auth)

### `MEMBER` (회원 정보)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **MEMBER_ID** | NUMBER | 회원 고유 ID | **PK** |
| MEMBER_PW | VARCHAR2(100) | 비밀번호 | NN |
| MEMBER_NICKNAME | VARCHAR2(50) | 닉네임 | NN |
| MEMBER_EMAIL | VARCHAR2(100) | 이메일 | |
| MEMBER_CONTACT | VARCHAR2(15) | 연락처 | |
| MEMBER_BIRTH | DATE | 생년월일 | |
| MEMBER_POST | VARCHAR2(10) | 우편번호 | |
| MEMBER_ADDRESS1 | VARCHAR2(300) | 주소 | |
| MEMBER_ADDRESS2 | VARCHAR2(300) | 상세주소 | |
| MEMBER_LEVEL | VARCHAR2(20) | 회원 등급 | |
| MEMBER_POINT | NUMBER | 보유 포인트 | Default 0 |
| MEMBER_RELIABILITY | NUMBER | 신뢰도 점수 | |
| MEMBER_JOIN | DATE | 가입일 | |

### `MEMBER_TOKEN` (인증 토큰)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **MEMBER_TOKEN_NO** | NUMBER | 토큰 고유 번호 | **PK** |
| MEMBER_TOKEN_TARGET | VARCHAR2(20) | 회원 ID | FK |
| MEMBER_TOKEN_VALUE | VARCHAR2(500) | 토큰 값 | |
| MEMBER_TOKEN_TIME | DATE | 만료 시간 | |

### `MEMBER_ICONS` (회원 보유 아이콘)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **MEMBER_ICONS_ID** | NUMBER | 고유 번호 | **PK** |
| MEMBER_ICONS_MEMBER_ID | VARCHAR2(20) | 회원 ID | FK |
| MEMBER_ICONS_ICON_ID | NUMBER | 아이콘 ID | FK |
| MEMBER_ICONS_OBTAIN_TIME| DATE | 획득 일시 | |
| IS_EQUIPPED | VARCHAR2(1) | 장착 여부 | |

### `CERT` (이메일 인증)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **CERT_EMAIL** | VARCHAR2(100) | 인증 이메일 | **PK** |
| CERT_NUMBER | VARCHAR2(10) | 인증 번호 | |
| CERT_TIME | DATE | 유효 시간 | |

### `WATCHLIST` (찜한 컨텐츠)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **WATCHLIST_CONTENT_ID** | NUMBER | 컨텐츠 ID | **PK, FK** |
| **WATCHLIST_MEMBER_ID** | VARCHAR2(20) | 회원 ID | **PK, FK** |
| WATCHLIST_TYPE | VARCHAR2(20) | 타입 | |
| WATCHLIST_TIME | DATE | 등록일 | |

---

## 2. 🎬 컨텐츠 (Contents)

### `CONTENTS` (컨텐츠 메인)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **CONTENTS_ID** | NUMBER | 컨텐츠 ID | **PK** |
| CONTENTS_TITLE | VARCHAR2(255) | 제목 | |
| CONTENTS_TYPE | VARCHAR2(50) | 유형 | |
| CONTENTS_OVERVIEW | VARCHAR2(4000) | 줄거리 | |
| CONTENTS_POSTER_PATH | VARCHAR2(255) | 포스터 경로 | |
| CONTENTS_BACKDROP_PATH | VARCHAR2(255) | 배경 경로 | |
| CONTENTS_VOTE_AVERAGE | NUMBER | 평점 | |
| CONTENTS_RUNTIME | NUMBER | 상영 시간 | |
| CONTENTS_RELEASE_DATE | DATE | 개봉일 | |
| CONTENTS_LANGUAGE | VARCHAR2(50) | 언어 | |
| CONTENTS_DIRECTOR | VARCHAR2(100) | 감독 | |
| CONTENTS_MAIN_CAST | VARCHAR2(500) | 주연 배우 | |

### `CONTENTS_GENRE` (장르)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **GENRE_ID** | NUMBER | 장르 ID | **PK** |
| GENRE_NAME | VARCHAR2(50) | 장르명 | NN |

### `CONTENTS_GENRE_MAP` (장르 매핑)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **CONTENTS_ID** | NUMBER | 컨텐츠 ID | **PK, FK** |
| **GENRE_ID** | NUMBER | 장르 ID | **PK, FK** |

---

## 3. 📝 게시판 (Board)

### `BOARD` (게시글)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **BOARD_NO** | NUMBER | 게시글 번호 | **PK** |
| BOARD_TITLE | VARCHAR2(300) | 제목 | NN |
| BOARD_WRITER | VARCHAR2(20) | 작성자 | FK |
| BOARD_CONTENTS_ID | NUMBER | 관련 컨텐츠 | FK |
| BOARD_WTIME | DATE | 작성일 | |
| BOARD_ETIME | DATE | 수정일 | |
| BOARD_TEXT | VARCHAR2(4000)| 본문 | |
| BOARD_VIEW_COUNT | NUMBER | 조회수 | |
| BOARD_LIKE | NUMBER | 좋아요 | |
| BOARD_UNLIKE | NUMBER | 싫어요 | |
| BOARD_REPLY_COUNT | NUMBER | 댓글수 | |
| BOARD_NOTICE | VARCHAR2(1) | 공지 여부 | |
| BOARD_REPORT_COUNT | NUMBER | 신고 횟수 | |

### `REPLY` (댓글)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **REPLY_NO** | NUMBER | 댓글 번호 | **PK** |
| REPLY_WRITER | VARCHAR2(20) | 작성자 | FK |
| REPLY_TARGET | NUMBER | 게시글 번호 | FK |
| REPLY_CONTENT | VARCHAR2(1000)| 내용 | |
| REPLY_WTIME | DATE | 작성일 | |
| REPLY_ETIME | DATE | 수정일 | |

### `BOARD_ATTACHMENT` (첨부파일 매핑)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **BOARD_NO** | NUMBER | 게시글 번호 | **PK, FK** |
| **ATTACHMENT_NO** | NUMBER | 파일 번호 | **PK, FK** |

### `ATTACHMENT` (파일 정보)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **ATTACHMENT_NO** | NUMBER | 파일 번호 | **PK** |
| ATTACHMENT_NAME | VARCHAR2(255) | 파일명 | |
| ATTACHMENT_SIZE | NUMBER | 크기 | |
| ATTACHMENT_TYPE | VARCHAR2(50) | 타입 | |
| ATTACHMENT_TIME | DATE | 등록일 | |

### `BOARD_RESPONSE` (좋아요/싫어요)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **BOARD_NO** | NUMBER | 게시글 번호 | **PK, FK** |
| **MEMBER_ID** | VARCHAR2(20) | 회원 ID | **PK, FK** |
| RESPONSE_TYPE | VARCHAR2(10) | 타입(Like/Unlike)| |

### `BOARD_REPORT` (게시글 신고)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **BOARD_REPORT_ID** | NUMBER | 신고 ID | **PK** |
| BOARD_REPORT_MEMBER_ID | VARCHAR2(20) | 신고자 | FK |
| BOARD_REPORT_BOARD_NO | NUMBER | 대상 게시글 | FK |
| BOARD_REPORT_TYPE | VARCHAR2(100) | 신고 유형 | |
| BOARD_REPORT_CONTENT | VARCHAR2(1000)| 신고 내용 | |
| BOARD_REPORT_DATE | DATE | 신고일 | |

---

## 4. ⭐ 리뷰 (Review)

### `REVIEW` (리뷰)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **REVIEW_NO** | NUMBER | 리뷰 번호 | **PK** |
| REVIEW_WRITER | VARCHAR2(20) | 작성자 | FK |
| REVIEW_CONTENTS | NUMBER | 컨텐츠 ID | FK, NN |
| REVIEW_RATING | NUMBER | 별점 | NN |
| REVIEW_SPOILER | VARCHAR2(1) | 스포일러 | |
| REVIEW_LIKE | NUMBER | 좋아요 수 | |
| REVIEW_RELIABILITY | NUMBER | 신뢰도 | |
| REVIEW_WTIME | DATE | 작성일 | |
| REVIEW_ETIME | DATE | 수정일 | |
| REVIEW_TEXT | VARCHAR2(4000)| 내용 | |
| REVIEW_PRICE | NUMBER | (미사용) | |

### `REVIEW_LIKE` (리뷰 좋아요)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **MEMBER_ID** | VARCHAR2(20) | 회원 ID | **PK, FK** |
| **REVIEW_NO** | NUMBER | 리뷰 번호 | **PK, FK** |

### `REVIEW_REPORT` (리뷰 신고)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **REVIEW_REPORT_ID** | NUMBER | 신고 ID | **PK** |
| REVIEW_REPORT_MEMBER_ID | VARCHAR2(20) | 신고자 | FK |
| REVIEW_REPORT_REVIEW_ID | NUMBER | 대상 리뷰 | FK |
| REVIEW_REPORT_TYPE | VARCHAR2(50) | 신고 유형 | |
| REVIEW_REPORT_CONTENT | VARCHAR2(1000)| 신고 내용 | |
| REVIEW_REPORT_DATE | DATE | 신고일 | |

---

## 5. 🧩 퀴즈 (Quiz)

### `QUIZ` (퀴즈 문제)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **QUIZ_ID** | NUMBER | 퀴즈 ID | **PK** |
| QUIZ_CONTENTS_ID | NUMBER | 컨텐츠 ID | FK |
| QUIZ_CREATOR_ID | VARCHAR2(20) | 출제자 ID | FK |
| QUIZ_QUESTION | VARCHAR2(1000)| 문제 | |
| QUIZ_ANSWER | VARCHAR2(500) | 정답 | |
| QUIZ_STATUS | VARCHAR2(20) | 상태 | |
| QUIZ_LOG_SOLVED_AT | DATE | (Log 테이블참조)| |

### `QUIZ_LOG` (퀴즈 기록)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **QUIZ_LOG_ID** | NUMBER | 로그 ID | **PK** |
| QUIZ_LOG_MEMBER_ID | VARCHAR2(20) | 회원 ID | FK |
| QUIZ_LOG_QUIZ_ID | NUMBER | 퀴즈 ID | FK |
| QUIZ_LOG_IS_CORRECT | VARCHAR2(1) | 정답 여부 | |
| QUIZ_LOG_SOLVED_AT | DATE | 풀이 시간 | |

### `DAILY_QUIZ` (오늘의 퀴즈)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **QUIZ_NO** | NUMBER | 번호 | **PK** |
| QUIZ_QUESTION | VARCHAR2(1000)| 문제 | |
| QUIZ_ANSWER | VARCHAR2(500) | 정답 | |

### `HEART` (퀴즈 하트)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **MEMBER_ID** | VARCHAR2(20) | 회원 ID | **PK, FK** |
| HEART_COUNT | NUMBER | 보유 하트 | |
| HEART_LAST_REFILL_DATE | DATE | 충전 시간 | |
| HEART_MAX | NUMBER | 최대치 | |

### `QUIZ_REPORT` (퀴즈 신고)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **QUIZ_REPORT_ID** | NUMBER | 신고 ID | **PK** |
| QUIZ_REPORT_MEMBER_ID | VARCHAR2(20) | 신고자 | FK |
| QUIZ_REPORT_QUIZ_ID | NUMBER | 대상 퀴즈 | FK |
| QUIZ_REPORT_TYPE | VARCHAR2(50) | 유형 | |
| QUIZ_REPORT_CONTENT | VARCHAR2(1000)| 내용 | |
| QUIZ_REPORT_DATE | DATE | 일시 | |

---

## 6. 🛒 상점 & 포인트 (Shop & Point)

### `POINT_ITEM_STORE` (상점)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **POINT_ITEM_NO** | NUMBER | 아이템 번호 | **PK** |
| POINT_ITEM_NAME | VARCHAR2(100) | 아이템명 | NN |
| POINT_ITEM_TYPE | VARCHAR2(50) | 타입 | |
| POINT_ITEM_CONTENT | VARCHAR2(500) | 설명 | |
| POINT_ITEM_PRICE | NUMBER | 가격 | |
| POINT_ITEM_STOCK | NUMBER | 재고 | |
| POINT_ITEM_SRC | VARCHAR2(255) | 이미지 | |
| POINT_ITEM_IS_LIMITED_PURCHASE | DATE | 구매제한 | |
| POINT_ITEM_REG_DATE | DATE | 등록일 | |
| POINT_ITEM_REQ_LEVEL | VARCHAR2(20) | 필요 등급 | |
| POINT_ITEM_DAILY_LIMIT | NUMBER | 일일 제한 | |

### `INVENTORY` (인벤토리)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **INVENTORY_NO** | NUMBER | 인벤토리 ID | **PK** |
| INVENTORY_MEMBER_ID | VARCHAR2(20) | 회원 ID | FK |
| INVENTORY_ITEM_NO | NUMBER | 아이템 번호 | FK |
| INVENTORY_CREATED_AT | DATE | 획득일 | |
| INVENTORY_QUANTITY | NUMBER | 수량 | |
| INVENTORY_EQUIPPED | VARCHAR2(1) | 장착 여부 | |

### `POINT_HISTORY` (포인트 내역)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **POINT_HISTORY_ID** | NUMBER | 내역 ID | **PK** |
| POINT_HISTORY_MEMBER_ID | VARCHAR2(20) | 회원 ID | FK |
| POINT_HISTORY_AMOUNT | NUMBER | 변동액 | |
| POINT_HISTORY_TRX_TYPE | VARCHAR2(10) | 유형 | |
| POINT_HISTORY_CREATED_AT | DATE | 일시 | |
| POINT_HISTORY_REASON | VARCHAR2(200) | 사유 | |

### `POINT_WISHLIST` (찜 목록)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **POINT_WISHLIST_NO** | NUMBER | 찜 번호 | **PK** |
| POINT_WISHLIST_MEMBER_ID | VARCHAR2(20) | 회원 ID | FK |
| POINT_WISHLIST_ITEM_NO | NUMBER | 아이템 번호 | FK |
| POINT_WISHLIST_TIME | DATE | 등록일 | |

### `ICON` (아이콘 정보)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **ICON_ID** | NUMBER | 아이콘 ID | **PK** |
| ICON_NAME | VARCHAR2(100) | 이름 | NN |
| ICON_RARITY | VARCHAR2(50) | 희귀도 | |
| ICON_CATEGORY | VARCHAR2(50) | 카테고리 | |
| ICON_SRC | VARCHAR2(255) | 이미지 경로 | |
| ICON_CONTENTS | VARCHAR2(500) | 설명 | |

### `POINT_GET_QUEST_LOG` (퀘스트 로그)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **POINT_GET_QUEST_LOG_ID** | NUMBER | 로그 ID | **PK** |
| POINT_GET_USER_ID | VARCHAR2(20) | 회원 ID | FK |
| POINT_GET_QUEST_TYPE | VARCHAR2(50) | 퀘스트 타입 | |
| POINT_GET_QUEST_DATE | DATE | 일자 | |
| POINT_GET_QUEST_COUNT | NUMBER | 카운트 | |
| POINT_GET_QUEST_REWARD_YN | VARCHAR2(1) | 보상 여부 | |
| POINT_GET_QUEST_CREATED_AT | DATE | 생성일 | |

---

## 7. 📅 출석 (Attendance)

### `ATTENDANCE_STATUS` (출석 현황)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **ATTENDANCE_STATUS_NO** | NUMBER | 상태 ID | **PK** |
| ATTENDANCE_STATUS_MEMBER_ID | VARCHAR2(20) | 회원 ID | FK |
| ATTENDANCE_STATUS_CURRENT | NUMBER | 현재 연속일 | |
| ATTENDANCE_STATUS_MAX | NUMBER | 최대 연속일 | |
| ATTENDANCE_STATUS_TOTAL | NUMBER | 총 출석일 | |
| ATTENDANCE_STATUS_LASTDATE | DATE | 마지막 출석 | |

### `ATTENDANCE_HISTORY` (출석 상세 기록)
| 컬럼명 | 타입 | 설명 | 비고 |
| :--- | :--- | :--- | :--- |
| **ATTENDANCE_HISTORY_NO** | NUMBER | 기록 ID | **PK** |
| ATTENDANCE_HISTORY_MEMBER_ID | VARCHAR2(20) | 회원 ID | FK |
| ATTENDANCE_HISTORY_DATE | DATE | 출석일 | |