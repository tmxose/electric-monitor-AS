package com.eco.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eco.domain.StaffVO;
import com.eco.service.StaffService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@AllArgsConstructor
@RequestMapping("/newStaff")
public class NewStaffController {
	private final StaffService staffService;
	
	@GetMapping("")
	public String newStaffPage() {
		return "admin/newStaff";
	}
	
	// 아이디 중복체크(GET /signup/checkId?user_id=xxx)
	@GetMapping(value = "/checkId", produces = "text/plain; charset=UTF-8")
	@ResponseBody
	public ResponseEntity<String> checkId(StaffVO vo) {
		log.info("아이디 중복 체크 요청: " +  vo.getStaff_id());
		StaffVO staff = staffService.checkId(vo.getStaff_id());
		if (staff == null) {
			return ResponseEntity.ok("사용 가능");
		} else {
			return ResponseEntity.ok("이미 사용 중인 아이디입니다.");
		}
	}
	
	@PostMapping("/staff")
	public String registerNewStaff(StaffVO vo, RedirectAttributes redirectAttrs) {
		log.info("직원 생성 요청: " + vo);
		boolean result = staffService.register(vo);
		if (result) {
			redirectAttrs.addFlashAttribute("message", "직원 계정이 생성되었습니다.");
		} else {
			redirectAttrs.addFlashAttribute("message", "계정 생성 실패");
		}
		return "redirect:/newStaff";
	}
	
	@PostMapping("/admin")
	public String guestLogin(StaffVO vo, RedirectAttributes redirectAttrs) {
		log.info("직원 생성 요청: " + vo);
		boolean result = staffService.register(vo);
		if (result) {
			redirectAttrs.addFlashAttribute("message", "관리자 계정이 생성되었습니다.");
		} else {
			redirectAttrs.addFlashAttribute("message", "계정 생성 실패");
		}
		return "redirect:/newStaff";
	}

}
