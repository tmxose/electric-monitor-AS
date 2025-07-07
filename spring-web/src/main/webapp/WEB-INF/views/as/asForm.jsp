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
<body>
	<div class="wrapper">
		<h2 class="header-title">A/S 신고</h2>
		<%@ include file="/WEB-INF/views/common/commonHeader.jsp" %>
		<main class="main">
			<form action="/as/insertCommon" method="post">
				<div>
					<span>신고자 정보</span>
					<div class="i">
						<label for="user_nm">이름</label><br> 
						<input type="text" name="user_nm" id="user_nm" value="${currentUserInfo.user_nm}" readonly>
					</div>
					<div class="i">
						<label for="user_mail">이메일</label><br> 
						<input type="text" name="user_mail" id="user_mail" value="${currentUserInfo.user_mail}">
					</div>
				</div>
				<div>
					<span>시설물 정보</span>
					<div class="i">
						<label for="as_facility">종류</label><br>
						<select id="as_facility" name="as_facility">
							<option value="">-- 시설 선택 --</option>
							<option value="전기배선">전기배선</option>
							<option value="분전반">분전반</option>
							<option value="누전차단기">누전차단기</option>
							<option value="전등설비">전등설비</option>
							<option value="옥내배선">옥내배선</option>
							<option value="기타">기타 (직접 입력)</option>
						</select>
						<input type="text" name="as_facility_custom" id="as_facility_custom" placeholder="직접 입력" style="display:none;" autocomplete="off">
					</div>
					<div class="i">
						<label for="as_addr">주소</label><br> 
						<input type="text" name="as_addr" id="as_addr" autocomplete="off">
					</div>
				</div>
				<div>
					<span>상세 정보</span>
					<div class="i">
						<label for="as_title">문제 종류</label><br>
						<select id="as_title" name="as_title">
							<option value="">-- 문제 유형 선택 --</option>
							<option value="합선 위험">합선 위험</option>
							<option value="과열 현상">과열 현상</option>
							<option value="스파크 발생">스파크 발생</option>
							<option value="노출된 전선">노출된 전선</option>
							<option value="차단기 작동 불능">차단기 작동 불능</option>
							<option value="기타">기타 (직접 입력)</option>
						</select>
						<input type="text" id="as_title_custom" name="as_title_custom" placeholder="직접 입력" style="display:none;" autocomplete="off">
					</div>
					<div class="i">
						<label for="as_content">상세 정보</label><br> 
						<input type="text" name="as_content" id="as_content" autocomplete="off">
					</div>
					<div class="i">
						<label for="reserve_date">예약 일자</label><br> 
						<input type="date" name="reserve_date" id="reserve_date">
					</div>
					<div class="i">
						<span>예약 시간</span><br>
						<div id="time-options">
						</div>
					</div>
				</div>
				<button type="submit">제출하기</button>
			</form>
		</main>
	</div>

	<%@ include file="/WEB-INF/views/common/footer.jsp" %>
	<script>
		document.addEventListener("DOMContentLoaded", () => {
			// 기타 선택시 직접 입력 input 보이게
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
			
			// 오늘 날짜 이후부터 선택 가능
			const dateInput = document.getElementById("reserve_date");
			const today = new Date();
			today.setDate(today.getDate() + 1);
			const yyyy = today.getFullYear();
			const mm = String(today.getMonth() + 1).padStart(2, '0');
			const dd = String(today.getDate()).padStart(2, '0');
			const minDate = yyyy+ "-" +mm+ "-" +dd;
			dateInput.min = minDate;
			
			// 예약된 시간 선택 비활성화
			const timeOptions = document.getElementById("time-options");
			dateInput.addEventListener("change", function () {
				const selectedDate = this.value;
				if (!selectedDate) return;

				$.ajax({
					url: "/as/form/booked-times",
					method: "GET",
					data: { selectedDate },
					dataType: "json",
					success: function (bookedTimes) {
						renderTimeOptions(bookedTimes);
					},
					error: function () {
						alert("예약된 시간을 불러오는 데 실패했습니다.");
					}
				});
			});
			function renderTimeOptions(bookedTimes) {
		        let html = "";
		        for (let hour = 9; hour <= 17; hour++) {
		            const hourStr = (hour < 10 ? "0" : "") + hour + ":00";
		            const isBooked = bookedTimes.includes(hourStr);
		            html += "<label>";
		            html += "<input type='radio' name='reserve_time' value='" + hourStr + "' " + (isBooked ? "disabled" : "") + ">";
		            html += hourStr + "</label><br>";
		        }
		        timeOptions.innerHTML = html;
		    }
			
			const form = document.querySelector('form[action="/as/insertCommon"]');
		    form.addEventListener("submit", function (e) {
		        const userMail = document.getElementById("user_mail");
		        const asFacility = document.getElementById("as_facility");
		        const asFacilityCustom = document.getElementById("as_facility_custom");
		        const asAddr = document.getElementById("as_addr");
		        const asTitle = document.getElementById("as_title");
		        const asTitleCustom = document.getElementById("as_title_custom");
		        const asContent = document.getElementById("as_content");
		        const reserveDate = document.getElementById("reserve_date");
		        const reserveTimeRadios = document.querySelectorAll('input[name="reserve_time"]');
		        
		        // 필수값 검증
		        if (userMail.value.trim() === "") {
		            alert("이메일을 입력해주세요.");
		            userMail.focus();
		            e.preventDefault();
		            return;
		        }
		        if (asFacility.value === "") {
		            alert("시설 종류를 선택해주세요.");
		            asFacility.focus();
		            e.preventDefault();
		            return;
		        }
		        if (asFacility.value === "기타" && asFacilityCustom.value.trim() === "") {
		            alert("시설 종류를 직접 입력해주세요.");
		            asFacilityCustom.focus();
		            e.preventDefault();
		            return;
		        }
		        if (asAddr.value.trim() === "") {
		            alert("주소를 입력해주세요.");
		            asAddr.focus();
		            e.preventDefault();
		            return;
		        }
		        if (asTitle.value === "") {
		            alert("문제 종류를 선택해주세요.");
		            asTitle.focus();
		            e.preventDefault();
		            return;
		        }
		        if (asTitle.value === "기타" && asTitleCustom.value.trim() === "") {
		            alert("문제 종류를 직접 입력해주세요.");
		            asTitleCustom.focus();
		            e.preventDefault();
		            return;
		        }
		        if (asContent.value.trim() === "") {
		            alert("상세 정보를 입력해주세요.");
		            asContent.focus();
		            e.preventDefault();
		            return;
		        }
		        if (reserveDate.value === "") {
		            alert("예약 일자를 선택해주세요.");
		            reserveDate.focus();
		            e.preventDefault();
		            return;
		        }
		        let timeSelected = false;
		        reserveTimeRadios.forEach(radio => {
		            if (radio.checked) timeSelected = true;
		        });
		        if (!timeSelected) {
		            alert("예약 시간을 선택해주세요.");
		            e.preventDefault();
		            return;
		        }
		    });
		});
	</script>
</body>
</html>