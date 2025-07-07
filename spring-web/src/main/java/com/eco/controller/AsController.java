package com.eco.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eco.domain.ASVO;
import com.eco.domain.UserVO;
import com.eco.service.AsService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@AllArgsConstructor
@RequestMapping("/as")
public class AsController {
	private final AsService asService;
	
	// as신청폼 접속
	@GetMapping("/form")
	public String asForm(@RequestParam(required = false) String selectedDate, HttpSession session, RedirectAttributes redirectAttrs) {
		log.info("as신청 페이지 요청");
		UserVO user = (UserVO) session.getAttribute("currentUserInfo");
	    if (user == null) {
	        redirectAttrs.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/login"; // 💡 로그인 페이지 URL에 맞게 수정
	    }
		return "/as/asForm";
	}
	//예약 차 있는 시간 선택 비활성화
	@GetMapping("/form/booked-times")
	@ResponseBody
	public List<String> getBookedTimes(@RequestParam String selectedDate) {
	    LocalDate date = LocalDate.parse(selectedDate);
	    return asService.getTotalAs(date);
	}
	
	//일반회원의 as신청
	@PostMapping("/insertCommon")
	public String registerAsByCommon(ASVO vo,
			@RequestParam String reserve_time, @RequestParam String reserve_date,
			@RequestParam(required=false) String as_facility_custom,
            @RequestParam(required=false) String as_title_custom,
			HttpSession session, RedirectAttributes redirectAttrs) {
		log.info("일반회원의 as신청");
		UserVO user = (UserVO)session.getAttribute("currentUserInfo");
		vo.setUser_cd(user.getUser_cd());
		//vo.setUser_mail(user.getUser_mail());
		
		//예약 날짜 시간 합치기
		LocalDate localDate = LocalDate.parse(reserve_date);
	    LocalDateTime combinedDateTime = LocalDateTime.parse(localDate.toString() + "T" + reserve_time + ":00");
	    vo.setAs_date(combinedDateTime);
	    
	    // 기타 입력 처리
	    if ("기타".equals(vo.getAs_facility()) && as_facility_custom != null && !as_facility_custom.isBlank()) {
	        vo.setAs_facility(as_facility_custom);
	    }
	    if ("기타".equals(vo.getAs_title()) && as_title_custom != null && !as_title_custom.isBlank()) {
	        vo.setAs_title(as_title_custom);
	    }

	    log.info("ASVO 입력값: " + vo);
	    
		boolean result = asService.registerAsByCommon(vo);
		log.info("insert 결과: " + result);
		if (result) {
			redirectAttrs.addFlashAttribute("message", "AS 신고가 완료되었습니다.");
		} else {
			redirectAttrs.addFlashAttribute("message", "AS 신고에 실패하였습니다.");
		}
		return "redirect:/as/form";
	}
	
	@GetMapping("/detail")
	public String asDetail(HttpSession session, RedirectAttributes redirectAttrs) {
		log.info("as신청 내역 상세 페이지 요청");
		UserVO user = (UserVO) session.getAttribute("currentUserInfo");
	    if (user == null) {
	        redirectAttrs.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/login"; // 💡 로그인 페이지 URL에 맞게 수정
	    }
		return "/as/asDetail";
	}

}
