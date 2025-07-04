<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	/* 폼 숨기기/보이기 */
	.login-form { display: none; }
	.login-form.active { display: block; }
	
	/* 탭 스타일 */
	#login-tabs {
		display: flex;
		list-style: none;
		padding: 0;
		margin-bottom: 20px;
	}

	#login-tabs li a {
		padding: 10px 20px;
		text-decoration: none;
		background-color: #eee;
		margin-right: 5px;
		border-radius: 5px 5px 0 0;
		color: #333;
		border: 1px solid #ccc;
		border-bottom: none;
	}

	#login-tabs li a.active {
		background-color: white;
		font-weight: bold;
		border-bottom: 1px solid white;
	}
</style>
</head>
<body>
	<div class="wrapper">
		<h2 class="header-title">로그인</h2>
		<%@ include file="/WEB-INF/views/common/commonHeader.jsp" %>
		<main class="main">
			<!-- 로그인 유형 탭 -->
			<ul id="login-tabs">
				<li><a href="#" data-tab="user" class="active">일반 로그인</a></li>
				<li><a href="#" data-tab="staff">직원 로그인</a></li>
				<li><a href="#" data-tab="guest">비회원 로그인</a></li>
			</ul>
		
			<!-- 일반 로그인 폼 -->
			<div id="user" class="login-form active">
				<form action="/login/user" method="post">
					<input type="text" name="user_id" placeholder="아이디"><br>
					<input type="password" name="user_pw" placeholder="비밀번호"><br>
					<button type="submit">로그인</button>
				</form>
			</div>
		
			<!-- 직원 로그인 폼 -->
			<div id="staff" class="login-form">
				<form action="/login/staff" method="post">
					<input type="text" name="staff_id" placeholder="직원 ID"><br>
					<input type="password" name="staff_pw" placeholder="비밀번호"><br>
					<button type="submit">직원 로그인</button>
				</form>
			</div>
		  
			<!-- 비회원 로그인 폼 -->
		  	<div id="guest" class="login-form">
				<form action="/login/guset" method="post">
					<input type="text" name="guset_nm" placeholder="이름"><br>
					<input type="text" name="guset_mail" placeholder="이메일"><br>
					<button type="submit">비회원 로그인</button>
				</form>
			</div>
		</main>
		<a href="/signup">회원가입</a>
	</div>

	<c:if test="${not empty message}">
   	 	<script>
        	alert('${message}');
    	</script>
	</c:if>

	<script>
		const tabs = document.querySelectorAll('#login-tabs a');
		const forms = document.querySelectorAll('.login-form');
	
		tabs.forEach(tab => {
			tab.addEventListener('click', function(e) {
				e.preventDefault();
				const targetId = this.dataset.tab;

				// 탭 버튼 상태 변경
				tabs.forEach(t => t.classList.remove('active'));
				this.classList.add('active');

				// 폼 보여주기/숨기기
				forms.forEach(form => {
				form.classList.toggle('active', form.id === targetId);
				});
			});
		});
	</script>

</body>
</html>