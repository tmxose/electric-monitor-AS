<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<link rel="stylesheet" href="../../resources/css/common.css?after" />
</head>
<body>
<div class="wrapper">
		<h2 class="header-title">A/S 신고</h2>
		<%@ include file="/WEB-INF/views/common/commonHeader.jsp" %>
		<main class="main">
			<table border="1">
			    <thead>
			        <tr>
			            <th>시설물</th>
			            <th>문제 종류</th>
			            <th>상세 내용</th>
			            <th>예약일시</th>
			            <th>진행상태</th>
			        </tr>
			    </thead>
			    <tbody>
			        <c:forEach var="as" items="${userList}">
			            <tr>
			                <td><a href="/as/edit?as_cd=${as.as_cd}">${as.as_facility}</a></td>
			                <td>${as.as_title}</td>
			                <td>${as.as_content}</td>
			                <td>${as.as_date}</td>
			                <td>${as.as_status}</td>
			            </tr>
			        </c:forEach>
			    </tbody>
			</table>
		</main>
	</div>
	<%@ include file="/WEB-INF/views/common/footer.jsp" %>
</body>
</html>