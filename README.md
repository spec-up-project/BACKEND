# Whirlwind - AI 기반 일정/할일 추출 시스템

> 📅 자연어 텍스트, 이메일, 이미지에서 AI를 활용해 일정과 할일을 자동으로 추출하고 관리하는 스마트 캘린더 시스템

## 🌟 주요 기능

### 📝 다양한 입력 방식 지원
- **텍스트 추출**: 자연어로 작성된 텍스트에서 일정/할일 추출
- **이메일 분석**: 이메일 제목과 본문에서 중요한 일정 추출
- **이미지 OCR**: 사진이나 스크린샷에서 텍스트 추출 후 일정 분석

### 🤖 AI 기반 지능형 분석
- **Ollama + Phi-3**: 로컬 LLM을 통한 텍스트 구조화
- **Facebook Duckling**: 정확한 날짜/시간 정보 추출
- **Tesseract OCR**: 한글/영문 이미지 텍스트 인식

### 📊 스마트 리포팅
- **주간/월간 리포트**: AI가 생성하는 생산성 분석 리포트
- **완료율 분석**: 개인 생산성 지표 및 인사이트 제공
- **마크다운 형식**: 읽기 쉬운 리포트 포맷

## 🏗️ 시스템 아키텍처

```
[사용자 입력] 
    ↓
[React Frontend] 🔄 REST API
    ↓
[Spring Boot Backend]
    ├── 🔤 Tesseract OCR (이미지 → 텍스트)
    ├── ⏰ Duckling NLP (날짜/시간 추출)  
    ├── 🧠 Ollama + Phi-3 (텍스트 구조화)
    └── 🗄️ MariaDB (데이터 저장)
```

## 🛠️ 기술 스택

| 구분 | 기술 | 버전 | 설명 |
|------|------|------|------|
| **Backend** | Spring Boot | 3.5.3 | REST API 서버 |
| **Database** | MariaDB | 11.4 | 메인 데이터베이스 |
| **AI/ML** | Ollama + Phi-3 | Latest | 로컬 LLM 서버 |
| **NLP** | Facebook Duckling | Latest | 날짜/시간 추출 |
| **OCR** | Tesseract | 5.x | 이미지 텍스트 추출 |
| **Containerization** | Docker Compose | Latest | 전체 서비스 관리 |
| **Java** | OpenJDK | 21 | 애플리케이션 런타임 |

## 🚀 빠른 시작

### 1. 사전 요구사항
- Docker & Docker Compose
- Git
- 최소 8GB RAM (Ollama LLM 구동용)

### 2. 프로젝트 클론 및 실행

```bash
# 1. 프로젝트 클론
git clone https://github.com/your-repo/whirlwind.git
cd whirlwind

# 2. 애플리케이션 빌드
./gradlew build

# 3. Docker Compose로 전체 스택 실행
docker-compose up -d

# 4. 서비스 상태 확인
docker-compose ps
```

### 3. 서비스 접속

| 서비스 | URL | 설명 |
|--------|-----|------|
| **API 서버** | http://localhost:8081 | 메인 애플리케이션 |
| **Swagger UI** | http://localhost:8081/swagger-ui.html | API 문서 |
| **Ollama** | http://localhost:11434 | LLM 서버 |
| **Duckling** | http://localhost:8000 | NLP 서버 |
| **MariaDB** | localhost:3306 | 데이터베이스 |

## 📡 API 엔드포인트

### 🔐 인증 API
```http
POST /api/user/register    # 회원가입
POST /api/user/login       # 로그인  
POST /api/user/reissue     # 토큰 재발급
```

### 📝 추출 API
```http
POST /api/extract/text     # 텍스트에서 일정/할일 추출
POST /api/extract/ocr      # 이미지 OCR 후 추출
POST /api/extract/email    # 이메일에서 추출
POST /api/extract/preview  # 저장 전 미리보기
```

### 📅 캘린더 API
```http
GET /api/calendar          # 내 일정 전체 조회
GET /api/calendar/range    # 기간별 일정 조회
GET /api/calendar/search   # 키워드 검색
```

### ✅ 할일 API
```http
GET /api/todos             # 내 할일 전체 조회
GET /api/todos/by-status   # 상태별 조회
GET /api/todos/overdue     # 기한 초과 할일
PUT /api/todos/{id}/complete # 할일 완료
```

### 📊 리포트 API
```http
GET /api/report/weekly     # 주간 리포트
GET /api/report/monthly    # 월간 리포트  
```

## 💡 사용 예시

### 1. 텍스트에서 일정 추출
```bash
curl -X POST http://localhost:8081/api/extract/text \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "text": "내일 오후 2시에 팀 미팅이 있고, 8월 15일까지 보고서를 완성해야 한다"
  }'
```

### 2. 이미지 OCR 처리
```bash
curl -X POST http://localhost:8081/api/extract/ocr \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "image=@screenshot.png" \
  -F "language=kor+eng"
```

### 3. 주간 리포트 생성
```bash
curl -X GET http://localhost:8081/api/report/weekly \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## ⚙️ 환경 설정

### Docker Compose 환경변수
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/whirlwinddb
  - APP_OLLAMA_URL=http://ollama:11434
  - APP_DUCKLING_URL=http://duckling:8000
```

### application.yml 커스터마이징
```yaml
app:
  ollama:
    model: phi3  # 또는 mistral, llama2 등
    timeout-seconds: 30
  tesseract:
    default-language: kor+eng  # 한글+영어
```

## 🔧 트러블슈팅

### 1. Ollama 모델 다운로드 실패
```bash
# 수동으로 모델 다운로드
docker exec -it whirlwind-ollama-1 ollama pull phi3
```

### 2. Tesseract 한글 인식 안됨
```bash
# 한글 언어팩 확인
docker exec -it whirlwind-whirlwind-api-1 tesseract --list-langs
```

### 3. 메모리 부족 오류
```yaml
# docker-compose.yml에 메모리 제한 설정
services:
  ollama:
    mem_limit: 4g
    memswap_limit: 4g
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 🔗 관련 링크

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Ollama GitHub](https://github.com/ollama/ollama)
- [Facebook Duckling](https://github.com/facebook/duckling)
- [Tesseract OCR](https://github.com/tesseract-ocr/tesseract)

---

**📧 문의사항**: [your-email@example.com](mailto:your-email@example.com)  
**🌐 데모**: [https://your-demo-site.com](https://your-demo-site.com)