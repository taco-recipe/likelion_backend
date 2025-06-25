# 🦁 멋사 Backend Plus 5기

> **매일 학습한 내용을 커밋하고, 요약 정리는 여기에 기록합니다.**  
> **자세한 정리는 Notion에 링크로 연결됩니다.**

---

## ✅ 일차별 학습 리스트

### 📅 1일차 - 컨테이너 & Docker 개념 이해
- 개발 환경 세팅: Docker, IntelliJ, Postman, MySQL Workbench
- Docker 기본 개념
  - 이미지 / 컨테이너 / 레지스트리 / 네트워크 / 볼륨
- 주요 Docker 명령어 실습 (`run`, `exec`, `logs`, `ps`, `stop`, `rm` 등)
- MySQL, Nginx 컨테이너 실행
- Dockerfile로 Spring Boot 앱 빌드 및 실행
- Docker Compose로 Nginx + Spring 연동
- 로드밸런싱 구성 및 Compose 네트워크 명시 이유

---

### 📅 2일차 - WebSocket 개념 및 Spring 구현
- HTTP의 한계와 WebSocket 개요
- Spring WebSocket + TextWebSocketHandler 기반 채팅 구현
- 세션 리스트 Set 관리, 메시지 브로드캐스트 처리
- 채팅방 구분 및 세션 매핑 방식 설계
- JPA를 통한 Room Entity 저장 및 조회 API 구성
- STOMP 프로토콜 개요와 도입 흐름

---

### 📅 3일차 - Redis 연동으로 STOMP 확장
- 문제: 인스턴스 간 WebSocket 세션 분리 문제
- Redis Pub/Sub으로 서버 간 메시지 중계 해결
  - `RedisPublisher` → 메시지 발행
  - `RedisSubscriber` → Redis 메시지 수신 후 STOMP 브로드캐스트
- 귓속말 기능 구현: `/user/queue/private` 방식
- Redis 채널명과 STOMP 경로 구분 이유 정리

---

### 📅 4일차 - GPT AI 챗봇 연동
- 클라이언트가 직접 GPT API 호출하지 않는 이유
  - API 키 보안 / 실시간성 / 응답 비동기 처리
- WebSocket 서버를 통한 GPT 중계 흐름
- `GPTService` 클래스 구현 (OpenAI API 호출 및 응답 추출)
- GPT 전용 WebSocket 엔드포인트 추가 `/ws-gpt`
- 사용자 메시지 전송 → GPT 응답 → 브라우저 전달 흐름 구성

---

### 📅 5일차 - CI/CD & 프로젝트 구조 개편
- 프로젝트 디렉토리 구조 리팩토링
  - `data`: MySQL, Redis
  - `backendProject`: backend1, 2, 3 + nginx
- Jenkins 도입
  - Jenkinsfile 작성 및 파이프라인 등록
  - nginx와 backend 이미지 빌드 및 compose 구성
- MySQL 테이블 스키마 구성 (`user`, `auth`, `profile`, `board`, `comment`)
- 회원가입 & 로그인 API 개발
  - DTO/Entity 연관관계 설정
  - `User` ↔ `UserProfile` 양방향 매핑

---

### 📅 6일차 - JPA
- 개인정보 변경 API (UserService.updateUser)
- 게시글 API
  - 등록 / 수정 / 삭제 / 조회
  - 유저 연관관계 (@ManyToOne)
  - 댓글 연관관계 (@OneToMany)
- 댓글 API
  - 등록 / 수정 / 삭제 / 조회
- 페이징 처리 (PageRequest, Page<BoardDTO>)
- 게시글 검색 기능 (@Query, 키워드 기반)
- 배치 저장
  - JPA persist + flush + clear
  - JDBC batchUpdate + UUID batchKey
