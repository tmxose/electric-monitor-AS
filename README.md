# ⚡️ 전기재해 모니터링 및 노후시설 A/S 신고 관리 시스템 (ElecMonitoring)

## 💡 프로젝트 개요

'전기재해 모니터링 및 노후시설 A/S 신고 관리 시스템(ElecMonitoring)'은 전기 재해를 예측하고 시각화하여 사용자 경각심을 높이고, 노후 전기시설의 A/S 신청부터 처리까지의 전 과정을 효율적으로 관리하기 위해 개발된 웹 기반 시스템입니다. 일반 사용자, 현장 직원, 관리자가 각자의 권한에 따라 시설 상태 조회, A/S 신고 및 처리, 직원 관리 등을 수행할 수 있도록 설계되었습니다.

## 🛠️ 개발 환경 및 기술 스택

- 개발 기간: 2025년 7월 02일 \~ 2025년 7월 28일
- 개발자: 신혁주(팀장), 박정현
- 프레임워크: Spring Framework (Spring MVC), FastAPI
- IDE: STS3 (Spring Tool Suite 3)
- 서버: Apache Tomcat 9 (Spring Backend), Uvicorn (FastAPI)
- 데이터베이스: MySQL
- 아키텍처: MVC (Spring) + RESTful API (Spring ↔ FastAPI) + Machine Learning (FastAPI)
- 예측 모델: Python (Prophet)


## 📁 프로젝트 구조

프로젝트는 효율적인 관리와 역할 분담을 위해 다음과 같이 모듈화하여 구성되었습니다.

```
📁elecMonitorAS/
├── 📁fastapi_ml/                # 예측 모델 및 API 서버 (FastAPI)
│   ├── 📁data/                  # 예측용 데이터 저장 폴더
│   ├── 📁models/                # 학습된 모델 파일 저장소
│   ├── 🐍main.py                # FastAPI 진입점
│   ├── 🐍train.py               # 예측 모델 학습 스크립트
│   ├── 🐍prediction.py          # 예측 로직 처리
│   ├── 🐍firereason.py          # 전기 화재 원인 JSON 반환
│   ├── 🐍shockreason.py         # 감전 사고 원인 JSON 반환
│   ├── 🐍elecrate.py            # 전체 화재 중 전기 화재 비율 JSON 반환
│   ├── 🐍list.py                # 전기 재해 현황 목록 JSON 반환
│   └── 📑requirements.txt       # FastAPI 의존성 목록
├── 📁spring-web/                # Spring MVC 기반 웹 백엔드
│   └── 📁src/
│       └── 📁main/
│           ├── 📁java/com/eco/
│           │   ├── 📁config/mail/           # 이메일 설정 클래스
│           │   │   └── 📄MailConfig.java
│           │   ├── 📁controller/            # 웹 컨트롤러
│           │   ├── 📁domain/                # VO/DTO 클래스
│           │   ├── 📁exception/             # 예외 처리
│           │   ├── 📁mapper/                # MyBatis 매퍼 인터페이스
│           │   └── 📁service/               # 서비스 로직
│           ├── 📁resources/
│           │   ├── 📁com/eco/mapper/        # MyBatis XML 매퍼
│           │   ├── 📁sql/
│           │   │   ├── 📄create_DB.sql      # DB 생성 스크립트
│           │   │   └── 📄insert_staff.sql   # 더미 직원 데이터 삽입
│           │   ├── 📄log4j.xml
│           │   └── 📄log4jdbc.log4j2.properties
│           └── 📁webapp/                    # JSP/정적 자원
├── 📄.gitignore                # Git 무시 파일 설정
└── 📄README.md                 # 프로젝트 설명 파일
```

## ✨ 주요 기능

  * **📌 재해 모니터링**: 전기재해 피해액, 건수 예측 차트 및 추이 데이터 표시
  * **📝 A/S 신고**: 일반 사용자가 노후 설비 이상 발견 시 온라인 신고
  * **🔒 권한 관리**: 관리자, 직원, 일반 사용자별 접근 권한 및 메뉴 차별화
  * **✅ 처리 상태 관리**: 신고 접수 → 처리 담당자 배정 → 처리 완료 단계 관리
  * **📊 시각화**: 전기 재해 피해 예측 데이터, 피해 현황 Chart.js 기반 그래프 제공

### ➕ 상세 기능

  * **로그인/회원가입**: SNS 간편 로그인 (Google, Naver, Kakao) 및 비회원 로그인 지원
  * **주소 검색**: Kakao 주소 API 연동으로 정확한 A/S 위치 입력
  * **직원 관리**: 관리자 페이지에서 직원 계정 생성/삭제/복구 및 담당 지역 설정
  * **일정 관리**: FullCalendar.js 기반 직원별/전체 일정 캘린더 제공
  * **공지사항/게시판**: 관리자 전용 공지사항 등록 및 관리
  * **데이터 무결성**: 상태에 따른 접근 제어, 논리적 삭제/복구 기능 구현
  * **통계 대시보드**: 연도별/지역별 피해 통계, 사고원인 비율, 전기 화재 대비 전체 화재 비율 시각화


## 🔁 시스템 플로우

### 🛠 A/S 처리 프로세스

```
[1] 사용자 A/S 신청
    ↓
[2] DB 저장 (Spring → MyBatis → MySQL)
    ↓
[3] 직원 캘린더에서 할당 및 일정 확인
    ↓
[4] 처리 상태 업데이트 (진행 중/완료)
    ↓
[5] 사용자에게 상태 메일 발송
```

### 📊 전기재해 예측 시각화

```
[1] 사용자 요청 (지역/모델 선택)
    ↓
[2] JavaScript → FastAPI 호출 (AJAX)
    ↓
[3] Python 예측 → JSON 반환
    ↓
[4] Spring Controller 처리
    ↓
[5] Chart.js로 시각화
```

## 🚀 프로젝트 실행 방법

**1.프로젝트 클론:**

```bash
git clone https://github.com/tmxose/electric-monitor-AS.git
```

**2. MySQL 초기화:**

 - MySQL 데이터베이스를 생성합니다.
 - 제공된 SQL 파일을 사용하여 필요한 테이블을 생성하고 직원 계정을 생성합니다.

```bash
mysql -u [USER] -p [DB_NAME] < mysql-query/create_DB.sql
mysql -u [USER] -p [DB_NAME] < mysql-query/insert_staff.sql

```
**2. FastAPI 서버 실행**

 - fastapi-ml 디렉토리로 이동합니다.
 - 필요한 Python 의존성을 설치합니다.
 - Uvicorn을 사용하여 FastAPI 애플리케이션을 실행합니다.

```bash
cd fastapi-ml
pip install -r requirements.txt
uvicorn main:app --reload
```

**3. Spring 서버 실행**

 - spring-web 프로젝트를 IDE (STS/IntelliJ)로 임포트합니다.
 - src/main/webapp/WEB-INF/spring/root-context.xml 파일에 데이터베이스 연결 정보를 설정합니다.
 - Maven 의존성 설치: 프로젝트 우클릭 -> Maven -> Update Project... 를 통해 필요한 의존성을 다운로드합니다.
 - Apache Tomcat 9 서버를 IDE에 연동하고, 프로젝트를 서버에 추가합니다.
 - Tomcat 서버를 시작하여 웹 애플리케이션을 실행합니다.


## 🖥️ 시연 영상

https://github.com/user-attachments/assets/0d1994c2-a9e1-477b-8c94-2a1bf41791fa
