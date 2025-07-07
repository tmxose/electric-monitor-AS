<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<link rel="stylesheet" href="../../resources/css/common.css?after" />
<body>
<div class="wrapper">
		<h2 class="header-title">A/S 수정</h2>
		<%@ include file="/WEB-INF/views/common/commonHeader.jsp" %>
		<main class="main">
			<form action="/as/updateCommon" method="post">
				<input type="hidden" name="as_cd" value="${asVO.as_cd}" />
				<div>
					<span>신고자 정보</span>
					<div class="i">
						<label for="user_nm">이름</label><br> 
						<input type="text" name="user_nm" id="user_nm" value="${currentUserInfo.user_nm}" readonly>
					</div>
					<div class="i">
						<label for="user_mail">이메일</label><br> 
						<input type="text" name="user_mail" id="user_mail" value="${asVO.user_mail}">
					</div>
				</div>
				<div>
					<span>시설물 정보</span>
					<div class="i">
						<label for="as_facility">종류</label><br>
						<select id="as_facility" name="as_facility">
							<option value="">-- 시설 선택 --</option>
							<option value="전기배선" ${asVO.as_facility == '전기배선' ? 'selected' : ''}>전기배선</option>
							<option value="분전반" ${asVO.as_facility == '분전반' ? 'selected' : ''}>분전반</option>
							<option value="누전차단기" ${asVO.as_facility == '누전차단기' ? 'selected' : ''}>누전차단기</option>
							<option value="전등설비" ${asVO.as_facility == '전등설비' ? 'selected' : ''}>전등설비</option>
							<option value="옥내배선" ${asVO.as_facility == '옥내배선' ? 'selected' : ''}>옥내배선</option>
							<c:set var="facilityKnown" value="false" />
						    <c:if test="${asVO.as_facility == '전기배선' or asVO.as_facility == '분전반' or asVO.as_facility == '누전차단기' or asVO.as_facility == '전등설비' or asVO.as_facility == '옥내배선'}">
						        <c:set var="facilityKnown" value="true" />
						    </c:if>
						    <option value="기타" <c:if test="${!facilityKnown}">selected</c:if>>기타 (직접 입력)</option>
						</select>
						<input type="text" name="as_facility_custom" id="as_facility_custom" placeholder="직접 입력" autocomplete="off" style="display:none;"
    					value="<c:if test='${!facilityKnown}'>${asVO.as_facility}</c:if>"/>
					</div>
					<div class="i">
						<label for="as_addr">주소</label><br> 
						<input type="text" name="as_addr" id="as_addr" autocomplete="off" value="${asVO.as_addr}">
					</div>
				</div>
				<div>
					<span>상세 정보</span>
					<div class="i">
	                    <label for="as_title">문제 종류</label><br>
	                    <select id="as_title" name="as_title">
	                        <option value="">-- 문제 유형 선택 --</option>
	                        <option value="합선 위험" ${asVO.as_title == '합선 위험' ? 'selected' : ''}>합선 위험</option>
	                        <option value="과열 현상" ${asVO.as_title == '과열 현상' ? 'selected' : ''}>과열 현상</option>
	                        <option value="스파크 발생" ${asVO.as_title == '스파크 발생' ? 'selected' : ''}>스파크 발생</option>
	                        <option value="노출된 전선" ${asVO.as_title == '노출된 전선' ? 'selected' : ''}>노출된 전선</option>
	                        <option value="차단기 작동 불능" ${asVO.as_title == '차단기 작동 불능' ? 'selected' : ''}>차단기 작동 불능</option>
	                        <c:set var="titleKnown" value="false" />
						    <c:if test="${asVO.as_title == '합선 위험' or asVO.as_title == '과열 현상' or asVO.as_title == '스파크 발생' or asVO.as_title == '노출된 전선' or asVO.as_title == '차단기 작동 불능'}">
						        <c:set var="titleKnown" value="true" />
						    </c:if>
						    <option value="기타" <c:if test="${!titleKnown}">selected</c:if>>기타 (직접 입력)</option>
						</select>
	                    <input type="text" id="as_title_custom" name="as_title_custom" placeholder="직접 입력" autocomplete="off" style="display:none;"
    						value="<c:if test='${!titleKnown}'>${asVO.as_title}</c:if>"/>
	                </div>
					<div class="i">
						<label for="as_content">상세 정보</label><br> 
						<input type="text" name="as_content" id="as_content" autocomplete="off" value="${asVO.as_content}">
					</div>
					<div class="i">
						<label for="reserve_date">예약 일자</label><br> 
						<input type="date" name="reserve_date" id="reserve_date" value="${fn:substring(asVO.as_date.toString(),0,10)}">
					</div>
					<div class="i">
						<span>예약 시간</span><br>
						<div id="time-options">
						</div>
					</div>
				</div>
				<button type="submit">수정하기</button>
			</form>
			<form action="/as/cancleCommon" method="post">
				<input type="hidden" name="as_cd" value="${asVO.as_cd}" />
				<button type="submit">예약 취소</button>
			</form>
		</main>
	</div>

	<%@ include file="/WEB-INF/views/common/footer.jsp" %>
	<script>
	document.addEventListener("DOMContentLoaded", () => {
	    const facilitySelect = document.getElementById("as_facility");
	    const facilityInput = document.getElementById("as_facility_custom");
	    
	    const titleSelect = document.getElementById("as_title");
	    const titleInput = document.getElementById("as_title_custom");
	
	    function toggleCustomInput(selectElem, inputElem) {
	        if (selectElem.value === "기타") {
	            inputElem.style.display = "block";
	        } else {
	            inputElem.style.display = "none";
	            inputElem.value = "";
	        }
	    }
	
	    facilitySelect.addEventListener("change", () => {
	        toggleCustomInput(facilitySelect, facilityInput);
	    });
	    titleSelect.addEventListener("change", () => {
	        toggleCustomInput(titleSelect, titleInput);
	    });
	
	    // 초기 로딩시 '기타' 선택시 커스텀 input 보여주기
	    toggleCustomInput(facilitySelect, facilityInput);
	    toggleCustomInput(titleSelect, titleInput);
	
	    // 오늘 날짜 이후부터 선택 가능
	    const dateInput = document.getElementById("reserve_date");
	    const today = new Date();
	    today.setDate(today.getDate() + 1);
	    const yyyy = today.getFullYear();
	    const mm = String(today.getMonth() + 1).padStart(2, '0');
	    const dd = String(today.getDate()).padStart(2, '0');
	    const minDate = yyyy+ "-" +mm+ "-" +dd;
	    dateInput.min = minDate;
	
	    // 예약된 시간 선택 비활성화 및 기존 예약 시간 체크
	    const timeOptions = document.getElementById("time-options");
	    const existingDate = dateInput.value;
	    const existingTime = "${fn:substring(asVO.as_date.toString(), 11,16)}"; // HH:mm
	
	    function renderTimeOptions(bookedTimes) {
	    	console.log("renderTimeOptions called with:", bookedTimes);
	    	let html = "";
	        for (let hour = 9; hour <= 17; hour++) {
	            const hourStr = (hour < 10 ? "0" : "") + hour + ":00";
	            const isBooked = bookedTimes.includes(hourStr);
	            let disabled = "";
	            let checked = "";
	
	            if (hourStr === existingTime) {
	                checked = "checked";
	                checkedSet = true;
	            } else if (isBooked) {
	                disabled = "disabled";
	            }
	            html += '<label><input type="radio" name="reserve_time" value="' + hourStr + '" ' + disabled + ' ' + checked + '> ' + hourStr + '</label><br>';
	        }
	
	        timeOptions.innerHTML = html;
	    }
	
	    function fetchBookedTimes(date) {
	    	if (!date) {
	            timeOptions.innerHTML = "";
	            return;
	        }
	        $.ajax({
	            url: "/as/form/booked-times",
	            method: "GET",
	            data: { selectedDate: date },
	            dataType: "json",
	            success: function(bookedTimes) {
	                renderTimeOptions(bookedTimes);
	            },
	            error: function() {
	                alert("예약된 시간을 불러오는 데 실패했습니다.");
	            }
	        });
	    }
	
	    // 최초 렌더링 시 예약시간 옵션 생성
	    if (existingDate) {
	        fetchBookedTimes(existingDate);
	    }
	
	    // 날짜 변경시 예약된 시간 다시 불러오기
	    dateInput.addEventListener("change", function() {
	        fetchBookedTimes(this.value);
	    });
	});
</script>
</body>
</html>