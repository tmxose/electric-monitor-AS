<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- --------------------------- 공통 헤더 영역 Start --------------------------- -->
<link rel="stylesheet" href="../../resources/css/commonHeader.css?after" />
<script>
//로그아웃 로직
function logoutConfirm() {
    if (confirm('정말 로그아웃 하시겠습니까?')) {
        window.location.href = '/logout';
    }
}
</script>
<header class="main-header">
	<nav class="main-nav">
		<ul class="nav-list">
			<li class="nav-item">
				<a href="#" class="nav-link" data-target="mega-notice">공지사항</a>
			</li>
			<li class="nav-item">
				<a href="#" class="nav-link" data-target="mega-as">A/S 신청</a>
			</li>
			<li class="nav-item">
				<a href="#" class="nav-link" data-target="mega-page">페이지</a>
			</li>
			<li class="nav-item">
				<a href="#" class="nav-link" data-target="mega-etc">기타</a>
			</li>
		</ul>
		<c:choose>
			 <c:when test="${empty currentUserInfo}">
		        <button class="login-btn" onclick="location.href='/login'">🔑 로그인</button>
		    </c:when>
			<c:when test="${not empty currentUserInfo and userType == 'common'}">
			    <p>${currentUserInfo.user_nm}님 환영합니다.</p>
			    <button class="logout-btn" onclick="logoutConfirm()">🚪 로그아웃</button>
			</c:when>
			
			<c:when test="${not empty currentUserInfo and (userType == 'staff' or userType == 'admin')}">
			    <p>${currentUserInfo.staff_nm}님 환영합니다.</p>
			    <button class="logout-btn" onclick="logoutConfirm()">🚪 로그아웃</button>
			</c:when>
	
		</c:choose>

	</nav>

    <div class="common-mega-menu-dropdown">
        <div id="mega-notice" class="mega-menu-content">
            <div class="mega-menu-column">
                <h3>공지사항 안내</h3>
                <ul>
                    <li><a href="/notice">전체 공지사항</a></li>
                    <li><a href="#">시스템 업데이트</a></li>
                    <li><a href="#">긴급 점검 안내</a></li>
                    <li><a href="#">이벤트 및 프로모션</a></li>
                </ul>
            </div>
            <div class="mega-menu-column">
                <h3>주요 공지사항</h3>
                <p>최신 공지사항을 확인하여 시스템 이용에 불편함이 없도록 해주세요. 중요한 변경사항이나 공지사항을 놓치지 않도록 주기적인 확인을 권장합니다.</p>
            </div>
            <div class="mega-menu-column">
                <h3 class="visually-hidden">바로가기</h3>
                <a href="#" class="menu-shortcut-btn">공지사항 전체보기</a>
                <p style="text-align: center; font-size: 3em; margin-top: 15px;">📢</p> </div>
        </div>

        <div id="mega-as" class="mega-menu-content">
            <div class="mega-menu-column">
                <h3>A/S 서비스 신청</h3>
                <ul>
                    <li><a href="/asRegister">A/S 신청서 작성</a></li>
                    <li><a href="/as/order">A/S 진행 현황</a></li>
                    <li><a href="/as/form">A/S 완료 내역</a></li>
                    <li><a href="/as/detail">FAQ: A/S 관련</a></li>
                </ul>
            </div>
            
            <div class="mega-menu-column">
                <h3>신청 가이드</h3>
                <p>간편하게 A/S를 신청하고 처리 현황을 확인하세요. 전문 기사가 신속하고 정확하게 처리해드립니다. 온라인으로 편리하게 접수 가능합니다.</p>
             </div>
             
             <div class="mega-menu-column">
                <h3>긴급 문의</h3>
                <p>긴급한 A/S는 고객센터로 직접 문의해주세요.</p>
                <p>📞 0000-1234</p>
            </div>
        </div>

        <div id="mega-page" class="mega-menu-content">
            <div class="mega-menu-column">
                <h3>사이트 정보</h3>
                <ul>
                    <li><a href="/">메인 페이지</a></li>
                    <li><a href="/profileEdit">회원정보 수정</a></li>
                    <li><a href="#">자주 묻는 질문</a></li>
                    <li><a href="#">사이트맵</a></li>
                </ul>
            </div>
            <div class="mega-menu-column">
                <h3>추가 정보</h3>
                <ul>
                    <li><a href="#">개인정보 처리방침</a></li>
                    <li><a href="#">이용약관</a></li>
                    <li><a href="#">오시는 길</a></li>
                </ul>
            </div>
            <div class="mega-menu-column">
                <h3>유용한 링크</h3>
                <ul class="icon-list">
                    <li><a href="#"> <i class="fas fa-file-alt"></i> 자료실</a></li>
                    <li><a href="#"> <i class="fas fa-envelope"></i> 1:1 문의</a></li>
                    <li><a href="#"> <i class="fas fa-search"></i> 검색</a></li>
                </ul>
            </div>
        </div>

        <div id="mega-etc" class="mega-menu-content">
            <div class="mega-menu-column">
                <h3>고객지원</h3>
                <ul>
                    <li><a href="#">고객센터</a></li>
                    <li><a href="#">온라인 문의</a></li>
                    <li><a href="#">원격 지원 요청</a></li>
                </ul>
            </div>
            <div class="mega-menu-column">
                <h3>회사 정보</h3>
                <ul>
                    <li><a href="#">회사 소개</a></li>
                    <li><a href="#">인재 채용</a></li>
                    <li><a href="#">제휴 문의</a></li>
                </ul>
            </div>
            <div class="mega-menu-column">
                <h3>관련 정책</h3>
                <p>문의 전에 관련 정책을 확인하시면 빠르게 궁금증을 해결할 수 있습니다. 각 서비스의 약관 및 규정을 숙지하시기 바랍니다.</p>
            </div>
        </div>
    </div>
</header>

<div class="overlay"></div>

<script src="../../resources/js/commonHeader.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
<!-- --------------------------- 공통 헤더 영역 End --------------------------- -->