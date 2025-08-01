package com.eco.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eco.domain.vo.StaffVO;
import com.eco.domain.vo.UserVO;
import com.eco.service.AsService;
import com.eco.service.StaffService;
import com.eco.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@AllArgsConstructor
@RequestMapping("/profileEdit")
public class ProfileEditController {
	private final UserService userService;
	private final StaffService staffService;
	private final AsService asService;

	//회원 정보 수정 페이지 접속
	@GetMapping("")
	public String profilePage(HttpSession session, RedirectAttributes redirectAttrs, Model model) {
		log.info("회원 정보 수정 화면 접속");
		String type = (String) session.getAttribute("userType");
		Object currentUser = session.getAttribute("currentUserInfo");

		if(currentUser == null) {
			redirectAttrs.addFlashAttribute("message", "잘못된 접근입니다.");
			return "redirect:/";
		}
	    if ("common".equals(type)) {
	        UserVO user = (UserVO) currentUser;
	        model.addAttribute("profileInfo", user);
	    } else if ("staff".equals(type) || "admin".equals(type)) {
	        StaffVO staff = (StaffVO) currentUser;
	        model.addAttribute("profileInfo", staff);
	    }

		return "user/profileEdit";
	}
	
	//회원 정보 수정
	@PostMapping("/common")
	public String profileEditCommon(UserVO inputVO, HttpSession session, RedirectAttributes redirectAttrs) {
		log.info("사용자 회원 정보 수정");
		boolean result = false;
	
		result = userService.modify(inputVO);
		
		if (result) {
			// DB에서 최신 정보 다시 조회
			UserVO updatedUser = userService.reSelectUserInfo(inputVO);
			session.setAttribute("currentUserInfo", updatedUser);
		}

		if (result) {
			redirectAttrs.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
		} else {
			redirectAttrs.addFlashAttribute("message", "회원 정보가 수정을 실패했습니다.");
		}
		return "redirect:/";
	}
	
	@PostMapping("/staff")
	public String profileEditStaff(StaffVO inputVO, HttpSession session, RedirectAttributes redirectAttrs) {
		log.info("직원 회원 정보 수정");
		boolean result = false;
		
		result = staffService.modify(inputVO);
		
		// 세션 갱신!
		if (result) {
			// DB에서 최신 정보 다시 조회
			StaffVO updatedStaff = staffService.checkId(inputVO.getStaff_id());
			session.setAttribute("currentUserInfo", updatedStaff);
		}

		if (result) {
			redirectAttrs.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
		} else {
			redirectAttrs.addFlashAttribute("message", "회원 정보가 수정을 실패했습니다.");
		}
		return "redirect:/";
	}
	
	//회원 탈퇴
	@GetMapping("/delete")
	public String deleteAccount(HttpSession session, RedirectAttributes redirectAttrs) {
		log.info("사용자 회원 탈퇴");
		Object obj = session.getAttribute("currentUserInfo");
		
		if (obj == null) {
			redirectAttrs.addFlashAttribute("message", "잘못된 접근입니다.");
			return "redirect:/";
		}

		boolean result = false;
		if (obj instanceof UserVO) {
			UserVO user = (UserVO) obj;
					
			try {
				asService.cancleAsListBydeleteUser(user);
			} catch(Exception e) {
				log.error("예약 취소 중 오류 발생", e);
				redirectAttrs.addFlashAttribute("message", "회원 탈퇴 중 오류가 발생하엿습니다.");
				return "redirect:/";
			}
			
			result = userService.deleteAccount(user);
			if (result) {	
				redirectAttrs.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
				session.invalidate();
				return "redirect:/";
			} else {
				redirectAttrs.addFlashAttribute("message", "회원 탈퇴를 실패했습니다.");
				return "redirect:/profileEdit";
			}
		} else if (obj instanceof StaffVO) {
			StaffVO staff = (StaffVO) obj;
			result = staffService.deleteAccount(staff);
			if (result) {
				redirectAttrs.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
				session.invalidate();
				return "redirect:/";
			} else {
				redirectAttrs.addFlashAttribute("message", "회원 탈퇴를 실패했습니다.");
				return "redirect:/profileEdit";
			}
		} else {
			redirectAttrs.addFlashAttribute("message", "사용자 타입 오류 발생.");
			return "redirect:/profileEdit";
		}
	}

	// 비밀번호 일치 확인 (AJAX)
	@PostMapping(value = "/checkPassword", produces = "application/json")
	@ResponseBody
	public boolean checkPassword(@RequestParam("inputPw") String inputPw, HttpSession session) {
		String type = (String) session.getAttribute("userType");
		boolean match = false;
		if ("common".equals(type)) {
			UserVO currentUser = (UserVO) session.getAttribute("currentUserInfo");
			if (currentUser == null) {
				return false; // 로그인 정보 없음
			}
			match = userService.checkPassword(inputPw, currentUser.getUser_pw());
		} else if ("staff".equals(type) || "admin".equals(type)) {
			StaffVO currentstaff = (StaffVO) session.getAttribute("currentUserInfo");
			if (currentstaff == null) {
				return false; // 로그인 정보 없음
			}
			match = staffService.checkPassword(inputPw, currentstaff.getStaff_pw());
		}
		return match; // JSON true/false 로 응답!
	}
}
