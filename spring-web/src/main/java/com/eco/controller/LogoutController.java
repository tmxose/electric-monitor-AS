package com.eco.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/logout")
public class LogoutController {

	//로그아웃
    @GetMapping("")
    public String logout(HttpSession session, Model model) {
    	log.info("로그아웃 시도");
        session.removeAttribute("currentUserInfo");
        session.removeAttribute("userType");
        model.addAttribute("message", "로그아웃이 완료되었습니다.");
        return "index"; 
    }
    
    @GetMapping("/auto")
    public String logoutAuto(HttpSession session,  Model model) {
    	log.info("로그인 세션 만료");
        session.removeAttribute("currentUserInfo");
        session.removeAttribute("userType");
        model.addAttribute("message", "로그인 유지시간이 만료되어 자동 로그아웃 되었습니다.");
        return "index"; 
    }
}
