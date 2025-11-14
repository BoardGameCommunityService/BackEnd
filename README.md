# Boardmate Backend

Spring Boot 기반의 Boardmate API 서버입니다.  

---

# 🚀 1. 로컬 개발 환경 설정

## 1) `.env.local` 파일 생성

프로젝트 루트 경로에 `.env.local` 파일을 만들어 사용하세요.

> ⚠️ 이 파일은 Git에 추가하지 마세요!

`.env.local`은 자동으로 `.gitignore`에 포함되어 있어 Git에 올라가지 않습니다.

---

# 🚀 2. 환경변수 로드 후 프로젝트 로컬 실행

최초 실행 전 아래 명령어를 실행하세요: (Mac기준)

```bash
export $(cat .env.local | xargs)
./mvnw spring-boot:run

```

---

# **🚀 3. DB 연결 확인 (로컬 테스트용)**

애플리케이션 실행 후 아래 URL로 접속하면 DB 연결 여부를 확인할 수 있습니다.

```
http://localhost:8080/db-check
```

---

# **🐳 4. EC2 서버 구조 (Dev / Prod 완전 분리)**

EC2 내부 구조는 다음과 같이 구성됩니다:

서버는 필요하면 계정을 생성해서 공유드리겠으나, 현재는 필요없으실 듯하여 SSH 권한 드리지 않겠습니다.

```
/srv/boardmate/
├── dev/
│   ├── docker-compose.yml
│   ├── deploy.sh
│   ├── db-data/
│   └── logs/
└── prod/
    ├── docker-compose.yml
    ├── deploy.sh
    ├── db-data/
    └── logs/
```

### **✔ Dev 환경**

- MySQL: 3307 포트 외부 노출 (보안 IP 제어)
- App: 8081 → 8080 (보안 IP 제어)
- 로컬 개발 시 이 DB로 접속 가능

### **✔ Prod 환경**

- MySQL: **외부 포트 없음 (내부 전용) - 개방 안하겠습니다** 
- App: 9001 → 8080

---

# **🔄 5. GitHub Actions → EC2 자동 배포**

| **브랜치** | **배포 목적**           |
| ---------- | ----------------------- |
| dev        | EC2 개발 서버 자동 배포 |
| prod       | EC2 운영 서버 자동 배포 |

## **🚀 1) Dev 환경 배포 흐름 (개발 서버)**

**개발 브랜치(dev)** 에 push 하면 자동으로 EC2 dev 환경에 배포됩니다.

```
(1) git push dev
      ↓
(2) GitHub Actions
      ├─ JDK 설치
      ├─ Spring Boot 빌드
      ├─ Docker build
      ├─ Docker Hub push
      ├─ EC2 SSH temporary open
      ├─ EC2 접속 후 deploy.sh 실행
      └─ SSH 자동 차단
```

## **🚀 2) Prod 환경 배포 흐름 (운영 서버)**

```
(1) Pull Request → dev → prod 머지 승인
      ↓
(2) prod 브랜치에 merge 발생
      ↓
(3) GitHub Actions 자동 실행
      ├─ JDK 설치
      ├─ Spring Boot 빌드
      ├─ Docker 이미지 빌드
      ├─ Docker Hub에 prod 이미지 push
      ├─ EC2 보안그룹에 SSH 임시 허용
      ├─ EC2 서버 접속 → /srv/boardmate/prod/deploy.sh 실행
      └─ SSH 포트 자동 차단
```

