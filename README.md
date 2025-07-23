-----

# ⚡️ 전기재해 모니터링 및 노후시설 A/S 신고 관리 시스템 (ElecMonitoring)

## 💡 프로젝트 개요

'전기재해 모니터링 및 노후시설 A/S 신고 관리 시스템(ElecMonitoring)'은 전기 재해를 예측하고 시각화하여 사용자 경각심을 높이고, 노후 전기시설의 A/S 신청부터 처리까지의 전 과정을 효율적으로 관리하기 위해 개발된 웹 기반 시스템입니다. 일반 사용자, 현장 직원, 관리자가 각자의 권한에 따라 시설 상태 조회, A/S 신고 및 처리, 직원 관리 등을 수행할 수 있도록 설계되었습니다.

-----

## 📅 개발 기간 및 참여

  * **개발 기간**: 2025년 7월 02일 \~ 2025년 7월 28일 (총 27일)
  * **수행 방식**: 2인 팀 프로젝트
  * **담당 범위**: 전체 웹 개발
  * **배포 현황**: 미출시
  * **GitHub**: [https://github.com/tmxose/electric-monitor-AS.git](https://github.com/tmxose/electric-monitor-AS.git)

-----

## 🎯 클라이언트 요구사항

### 💼 클라이언트 개요

  * **주 이용자**:
      * **일반 사용자**: 노후 전기시설 관리 및 A/S 신청
      * **현장 직원**: 배정된 A/S 일정 및 처리 현황 관리
      * **관리자**: 시스템 운영 및 사용자/데이터 권한 통합 관리
  * **클라이언트 목적**:
      * 전기 재해 모니터링 및 시각화를 통한 사고 예방 기여
      * 신속한 A/S 신청 및 일정 관리 프로세스 제공으로 서비스 처리 속도 및 만족도 향상
      * 현장 직원의 직관적인 일정 및 진행 현황 확인, 업데이트 시스템 환경 제공

### 📌 요구사항 요약

| 구분         | 요구사항                                        |
| :----------- | :---------------------------------------------- |
| 일반 사용자  | 노후 전기시설 상태 조회 및 A/S 신고             |
| 관리자       | 전체 A/S 요청 관리, 처리 상태 변경, 직원 계정 관리 |
| 직원         | 현장 출동 기록, 처리 진행 상황 업데이트         |
| 공통         | 사용자 권한별 접근 제어, 실시간 데이터 확인     |


-----
## 🛠️ 기술 스택 및 아키텍처

| 항목           | 상세 내용                                   |
| :------------- | :------------------------------------------ |
| **Backend** | Spring Framework, MyBatis, FastAPI          |
| **Frontend** | JSP, HTML/CSS, JavaScript, Chart.js, FullCalendar.js |
| **Machine Learning** | Python (전기 재해 피해 규모 예측)           |
| **DBMS** | MySQL                                       |
| **IDE** | STS3, VSCode (Python)                       |

-----
### 📐 시스템 구조

  * **Spring MVC 기반 Controller-Service-Mapper 분리**
  * **FastAPI 서버**에서 설비 상태 예측 후 JSON으로 Spring에 전달
  * **JSP/Chart.js**로 실시간 데이터 시각화
  * 사용자 **권한 정보 전역 관리**(`window.userType`)
  * **관리자/직원/사용자 권한별 페이지 및 기능 분기 처리**

### 📈 전기재해 예측 시각화 파이프라인

```
[1] 사용자 입력
    └─ 지역, 모델, 기간 등 파라미터 선택
    ↓
[2] 클라이언트 요청
    └─ JavaScript (AJAX)로 예측 API 호출
    ↓
[3] 서버 측 처리
    └─ FastAPI → 머신러닝 모델 로드 및 예측 실행
    ↓
[4] 응답 반환
    └─ JSON 형식으로 예측 결과 전달
    ↓
[5] 결과 시각화
    └─ Chart.js로 예측 데이터 그래프 렌더링
```

### 🛠️ 노후시설 A/S 접수 및 일정 관리 파이프라인

```
[1] 사용자 A/S 접수
    └─ 사용자 입력: 시설 위치, 고장내용, 예약 일시
        - Kakao 주소 API 연동
    ↓
[2] Spring 서버 처리
    └─ Controller → Service → Mapper → DB 저장
    ↓
[3] A/S 일정 확인
    └─ 직원: 본인 일정 조회
    └─ 관리자: 전체 직원 일정 확인
    ↓
[4] 캘린더 렌더링
    └─ FullCalendar.js로 일정 시각화
        - 일정 상태별 색상 분기 처리
    ↓
[5] 진행 상황 업데이트
    └─ 직원이 ‘작업 중’ 또는 ‘완료’ 상태로 변경
        - 상태 변경 시 사용자에게 메일 발송
```

-----

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

-----

## 🚀 문제 해결 및 기술적 도전

### ✅ 기술적 도전

  * **권한별 상태 변경 제한**: `window.userType`으로 프론트 메뉴 노출 제어 및 세션 기반 사용자 검증으로 이중 확인
  * **Python ↔ Spring 연동**: FastAPI로 모델 결과 JSON 반환 및 Ajax 호출로 실시간 데이터 바인딩
  * **동시 요청 처리**: AJAX 비동기 처리와 DB 트랜잭션으로 상태 일관성 유지
  * **SNS 로그인 처리**: 신규/기존 사용자 분기 처리 로직 구현

### ✅ 문제 사례 1: 전기재해 신고글 등록 시 파일 조회가 안되는 현상

  * **문제점**: 팀원이 동일한 경로에 접근할 수 없어 업로드된 파일을 보지 못함
    
    ### 📌 원인

- `D:\01-STUDY\SHJ\upload` 경로는 **서버 로컬 경로**이므로, 서버에 직접 접근하지 않는 이상 **외부에서는 파일을 볼 수 없음**
- 웹에서는 `D:\01-STUDY\SHJ\upload` 폴더가 **정적 리소스로 매핑되지 않았기 때문**

 * **해결 방법**

Spring에서 **정적 리소스 매핑 설정**을 추가하여 웹을 통해 해당 폴더에 접근할 수 있도록 설정해주어야 함.

### 1. `servlet-context.xml` 또는 설정 클래스에 아래 내용 추가:

```xml
<mvc:resources mapping="/files/**" location="file:///D:/01-STUDY/SHJ/upload/" />
<context:component-scan base-package="com.eco.controller, com.eco.config.web" />
```

- `mapping="/files/**"`: 웹에서 `/upload/파일명`으로 접근할 수 있도록 함
- `location="file:///D:/01-STUDY/SHJ/upload/"`: 실제 파일이 저장된 로컬 경로를 가리킴
- `MailConfig`와 `FileResourceConfig`는 서로 다른 초기화 컨텍스트(Spring Root vs Servlet)에서 동작해야 하기 때문에, 같은 패키지에 두면 충돌이 발생함. 이를 방지하기 위해 각각 `com.eco.config.mail`과 `com.eco.config.web`로 분리하여 설정함.
    
    ```jsx
    **com.eco.config
    │
    ├── MailConfig          <-- 메일 관련 설정 (RootContext에서 사용)
    ├── FileResourceConfig  <-- 정적 리소스 설정 (ServletContext에서 사용)
    │
    └── (충돌 발생 ⚠️)
          동일 컨텍스트로 초기화하려다 충돌
    
    📌 해결 방법
    패키지 분리 후 각각의 컨텍스트에서 초기화되도록 설정
    
    com.eco.config.mail
    └── MailConfig          <-- RootContext 전용 설정
    
    com.eco.config.web
    └── FileResourceConfig  <-- ServletContext 전용 설정**
    
    ```

-----
## 📈 개발 결과 및 회고

### ✅ 주요 성과

  * 전기재해 모니터링부터 A/S 신고/처리까지 전체 흐름 **풀스택 구축**
  * **FastAPI 연동**으로 Python 예측 모델과 Spring 간 실시간 데이터 통신 성공
  * **권한별 메뉴/기능 차별화**로 사용자 혼선 최소화 및 관리 편의성 향상

### 🔍 회고

  * 실시간 모니터링 데이터의 **정확도와 속도의 중요성**을 경험했습니다.
  * **SNS 로그인 및 외부 API 연동 경험**을 통해 확장성 높은 설계에 대해 고민할 수 있었습니다.
  * 향후 알림, 메시징 등 실시간 사용자 피드백 기능 추가를 계획하고 있습니다.

-----

## 🖥️ 시연 화면
### **메인화면**

- **메인화면**
    
    ![screencapture-localhost-8080-2025-07-22-17_01_46.png](attachment:4d8971d4-c936-4d0a-9312-934794792555:screencapture-localhost-8080-2025-07-22-17_01_46.png)
    
- **로그인 화면**
    
    ![screencapture-localhost-8080-login-2025-07-22-17_02_15.png](attachment:05f8386d-8271-48f4-a84d-5756b4364743:screencapture-localhost-8080-login-2025-07-22-17_02_15.png)
    

### **공통헤더**

- **전기재해 모니터링**
    
    ![image.png](attachment:2a3f930f-1b1d-421d-9bbb-942042ccce78:image.png)
    
- **노후시설 A/S 서비스(로그아웃)**
    
    ![image.png](attachment:c0cc60dc-d417-498d-a1ab-c315890c98ec:image.png)
    
- **노후시설 A/S 서비스(회원)**
    
    ![image.png](attachment:56a06eff-e2c1-4d33-af8f-fef61ea451d1:image.png)
    
- **노후시설 A/S 서비스(직원, 관리자)**
    
    ![image.png](attachment:bd37ff62-980d-437d-9416-221f315defd5:image.png)
    
- **고객지원 (비회원, 회원, 직원)**
    
    ![image.png](attachment:fdca7e05-8a39-4bba-9813-86a6000cd829:image.png)
    
- **고객지원 (관리자)**
    
    ![image.png](attachment:9da70684-9eab-4375-b382-97acfc7c10a2:image.png)
    

### **📊 모니터링 대시보드**

- 대시보드
    
    ![screencapture-localhost-8080-dashboard-2025-07-22-17_02_04.png](attachment:73ce27e6-88a7-4a0e-a53f-966d4c3836a0:screencapture-localhost-8080-dashboard-2025-07-22-17_02_04.png)
    
- **전기 재해 신고 목록**
    
    ![screencapture-localhost-8080-report-2025-07-22-17_40_59.png](attachment:74f82631-5351-41c9-b7ec-30fdd2977a08:screencapture-localhost-8080-report-2025-07-22-17_40_59.png)
    

### **🗂 A/S 처리 현황 및 캘린더**

- **A/S 처리 현황**
    
    ![screencapture-localhost-8080-as-form-2025-07-22-17_02_29.png](attachment:1c506e15-1b1b-4c89-a711-d9ca4829473e:screencapture-localhost-8080-as-form-2025-07-22-17_02_29.png)
    
    ![screencapture-localhost-8080-as-detail-2025-07-22-17_02_39.png](attachment:92d77a85-0868-427a-9bff-427aa011c4df:screencapture-localhost-8080-as-detail-2025-07-22-17_02_39.png)
    
    ![screencapture-localhost-8080-as-info-2025-07-22-17_02_47.png](attachment:55771f09-d999-4323-9515-a99d33411e9e:screencapture-localhost-8080-as-info-2025-07-22-17_02_47.png)
    
- **캘린더**
    
    ![screencapture-localhost-8080-as-calendar-2025-07-22-17_03_55.png](attachment:a0678953-35ec-4ba3-b6ad-2817e11afa44:screencapture-localhost-8080-as-calendar-2025-07-22-17_03_55.png)
    

### ✅ **관리자 메뉴 및 공지사항**

- **관리자 메뉴**
    
    ![screencapture-localhost-8080-admin-account-2025-07-22-17_04_05.png](attachment:9972496b-682d-477a-a06d-4330e6fedcca:screencapture-localhost-8080-admin-account-2025-07-22-17_04_05.png)
    
- **공지사항**
    
    ![screencapture-localhost-8080-notice-detail-2025-07-22-17_04_28.png](attachment:9411690c-74a6-41d3-8555-285c420607a1:screencapture-localhost-8080-notice-detail-2025-07-22-17_04_28.png)
### ✅ 권한별 메뉴 및 페이지 접근 제어
<img width="1920" height="1622" alt="관리자 계정관리" src="https://github.com/user-attachments/assets/54af1305-22af-48cc-97ea-a6ec81e72175" />

