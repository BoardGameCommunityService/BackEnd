# Boardmate Backend

Spring Boot 기반의 Boardmate API 서버입니다.  
로컬 개발 시 `.env.local` 파일을 통해 환경변수를 주입하여 실행합니다.

---

## 🚀 1. 로컬 개발 환경 설정

### 1) `.env.local` 파일 생성

프로젝트 루트 경로에 `.env.local` 파일을 만들어 사용하세요.

> ⚠️ 이 파일은 절대 Git에 추가하지 마세요!

`.env.local`은 자동으로 `.gitignore`에 포함되어 있어 Git에 올라가지 않습니다.

---

## 🚀 2. 환경변수 로드 후 프로젝트 실행

최초 실행 전 아래 명령어를 실행하세요:

```bash
export $(cat .env.local | xargs)
./mvnw spring-boot:run
