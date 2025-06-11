# 서점ZIP

![Slide 16_9 - 1](https://github.com/user-attachments/assets/81ae111b-6daf-4781-84c7-1886aabc334e)


##  프로젝트 개요

이 프로젝트는 개인만의 특성을 드러내는 독립출판물의 감성을 즐기는 사용자가 독립출판물을 더 잘 찾아낼 수 있도록, 독립출판물을 비치한 서점에 대한 위치, 키워드, 운영시간 등의 정보와 도서출판전산망에 등록되지 않은 독립출판물 리뷰 제공 및 추천 시스템을 제공합니다.

### 주요 기능
![Slide 16_9 - 31](https://github.com/user-attachments/assets/a3baf993-d50c-4f18-84ff-75b9e08f7cd5)


1. **BOOKSNAP** (사용자 리뷰 기반 독립출판물 데이터 구축)

   - 사용자가 직접 독립출판물에 대한 리뷰를 등록 (책 제목, 작가, 평점, 서점 사진 등)
   - 사용자 참여 기반 데이터를 구축하여 독립출판물 정보를 확보
   - 축적된 리뷰 데이터를 활용하여 추천 시스템 학습 데이터로 활용 가능

2. **서점 ZIP** (지도 기반 서점 탐색)
   - 사용자의 현재 위치 또는 검색 위치를 기준으로 주변 독립서점을 지도에서 시각화
   - Kakao 지도 API 및 거리 계산 알고리즘을 활용하여 위치 기반 필터링, 태그 기반 분류 지원
   - 서점별 보유 도서, 운영 시간, 태그 정보 등 상세 정보 제공
3. **Bookie** (RAG 기반 독립출판물 추천)
   - LLM + Vector DB를 활용한 RAG 기반 AI 추천 시스템
   - 사용자 리뷰, 검색, 찜 목록 등의 활동 데이터를 바탕으로 개인화된 독립출판물 추천
   - 챗봇 인터페이스를 통해 직관적인 추천 경험 제공

### 부가 기능

![Slide 16_9 - 51](https://github.com/user-attachments/assets/d2d274fd-b67d-41ce-be16-f1f9a7f4043f)

## 🛠️ 사용 기술
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) 
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=Gradle&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-000000?style=flat-square&logo=data:image/svg+xml;base64,PHN2ZyB4bWxu...%29)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white) 
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white) 
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-2C4F72?style=flat-square&logo=lombok&logoColor=white)

---

## 💡 시작하기

### 사전준비
- [Git](https://git-scm.com/downloads)
- Java Development Kit(JDK)
  - version : [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- 데이터베이스 준비
  - [MySQL](https://dev.mysql.com/downloads/mysql/)
    - version : 8.0
  - [Redis](https://redis.io/downloads/)
    - version : 8.0
- InteliJ IDEA
  - local에서 프로젝트 실행을 위해 필요합니다.

- 부가 기능 api키 준비
  - spring.mail
    - 메일 발신자 아이디 (example@example.com이면 example)
    - 메일 앱 비밀번호   
  - kakao
    - 로그인을 위한 rest api key   
  - cloudinary
    - api key
    - clode name
  - ai
    - gpt api key
    
### How to Build
1. Repository 클론
```
git clone https://github.com/TEAM-ZIP/Backend.git
```

2. .env
- 전달받은 `.env`파일을 사용하거나 직접 .env 파일의 환경변수를 설정합니다.
- .env 파일의 위치는 src/main/resources입니다.
```
ai.base.uri = {ai 서버 주소}
BOOKSTORE_CAFE_KEY = { 공공데이터포털 카페가 있는 서점 api key }
BOOKSTORE_CHILD_KEY = { 공공데이터포털 아동 서점 api key }
BOOKSTORE_INDEP_KEY = {공공데이터포털 독립 서점 api key}
cloudinary.api_key = {cloudniary api key}
cloudinary.api_secret = {cloudniary secret key}
DB_PASSWORD = {MySQL DB 비밀번호}
DB_URL = {MySQL url}
DB_USERNAME = {MySQL DB name}
jwt.secret = {60자 이상의 jwt 생성용 문자열}
kakao.client.id = {kakao rest api key}
kakao.redirect-uri = {kakao 로그인 용 redirect uri}
mail.password = {메일 앱 비밀번호}
mail.username = {메일 호트 이름}
openai.api.key = {gpt api key}
spring.redis.host = {redis 주소}
spring.redis.port = {redis 포트 번호}
```

3.  빌드
```
cd Capstone-Backend
./gradlew build
```


### How to Install
- 서버 실행
```
cd build/libs
nohup java -jar bszip-0.0.1-SNAPSHOT.jar &
```

### How to Test
- 서버 실행 후, `http://localhost:8080/ `에 접속
- api test를 원하면, `http://localhost:8080/swagger-ui/index.html#/`에 접속하여 swagger를 통해 api 호출 테스트할 수 있음
![image](https://github.com/user-attachments/assets/f4e6db4a-72a2-4081-9967-bfa285aca9ef)
![image](https://github.com/user-attachments/assets/9c5b18dd-0494-4cca-91ea-77ea9606a048)

- 프론트와 연동되어 테스트 진행 시, 프론트엔드 빌드/실행한 로컬 주소 혹은 [배포된 주소](https://bookstore-zip.site/)에서 실제 이용하고 있는 api 호출 결과 확인 가능
  - 배포된 주소에서 다음과 같은 이메일과 비밀번호로 로그인 시 기존 데이터로 테스트할 수 있습니다.
  - email : user@example.com
  - password : password123



---
## 👋🏻 Members

| <img src="https://github.com/topograp2.png" width=120/> | <img src="https://github.com/sohyu-na.png" width=120/> |
| :-----------------------------------------------------: | :----------------------------------------------------: |
|         [홍지형](https://github.com/topograp2)          |         [소현아](https://github.com/sohyu-na)          |

## 📍 아키텍처
![architecture](https://github.com/user-attachments/assets/8229de7c-c9d1-4c91-86bf-6b757596b158)


## 🗂️ 폴더 구조
```
📂 src/main/java/com/capstone/bszip
├─ 📂 Book             ▶️ 도서 관련 기능 모듈
│   ├─ 📂 controller
│   ├─ 📂 domain
│   ├─ 📂 dto
│   ├─ 📂 repository
│   └─ 📂 service
├─ 📂 Bookie           ▶️ 챗봇 관련 기능 모듈
│   ├─ 📂 controller
│   ..
│   └─ 📂 service
├─ 📂 Bookstore        ▶️ 서점 관련 기능 모듈
│   ├─ 📂 controller
│   ..
│   └─ 📂 service
├─ 📂 Member           ▶️ 회원 관련 기능 모듈
│   ├─ 📂 controller
│   ..
│   └─ 📂 service
├─ 📂 auth             ▶️ 인증 관련 모듈 
│   ├─ 📂 blackList    ▶️ 로그아웃 
│   ├─ 📂 dto          ▶️ 토큰 요청/응답 객체 
│   ├─ 📂 refreshToken ▶️ Refresh 토큰 
│   ├─ 📂 security     ▶️ Jwt
│   ├─ AuthController
│   └─ AuthService
├─ 📂 cloudinary       ▶️ 클라우디너리 이미지 업로드 관련 기능
│   └─ 📂 service
├─ 📂 commonDto        ▶️ 공통 응답 객체 및 예외 처리
│   ├─ 📂 exception
│   ├─ ErrorResponse
│   └─ SuccessResponse
├─ 📂 config           ▶️ 전역 설정
└─ BszipApplication    ▶️ 메인 애플리케이션 실행 클래스
```

 - `controller`: HTTP 요청을 받아 처리하는 진입점
 - `service`: 비즈니스 로직 구현
 - `repository`: 데이터베이스에 접근하는 계층
 - `domain`: 데이터베이스 매핑을 위한 도메인 모델
 - `dto`: 요청/응답에 사용되는 데이터 전송 객체


## 📚 오픈소스

## Backend Libraries & Tools

1. **Spring Boot** : [Spring Boot Official Site](https://spring.io/)
2. **Lombok** :  [Lombok Official Site](https://projectlombok.org/)
3. **MySQL** : [MySQL Official Site](https://www.mysql.com/)
4. **Redis** : [Redis Official Site](https://redis.io/)
5. **Swagger** : [Swagger Official Site](https://swagger.io/)
