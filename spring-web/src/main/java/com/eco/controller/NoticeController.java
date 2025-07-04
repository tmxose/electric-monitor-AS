package com.eco.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eco.domain.NoticeVO;
import com.eco.service.NoticeService;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/notice")
public class NoticeController {

	/*
	 * // 공지사항 페이지로 이동하기
	 * 
	 * @GetMapping("") public String noticePage() { log.info("공지사항 페이지로 이동"); return
	 * "notice"; }
	 * 
	 * 
	 * // 공지사항 상세 페이지로 이동하기
	 * 
	 * @GetMapping("/detail") public String noticeDetailPage() {
	 * log.info("공지사항 상세 페이지로 이동"); return "noticeDetail"; }
	 */
	@Autowired
	private NoticeService noticeService;

	// 공지사항 목록 페이지 이동 + 데이터 조회
	@GetMapping("")
	public String noticePage(Model model) {
		log.info("공지사항 목록 조회 페이지로 이동");
		List<NoticeVO> noticeList = noticeService.getNoticeList();
		model.addAttribute("noticeList", noticeList);
		return "notice"; // /WEB-INF/views/notice.jsp
	}

	// 공지사항 상세/수정/등록 페이지 이동
	@GetMapping("/detail")
	public String noticeDetailPage(@RequestParam(required = false) Integer notice_cd,
			@RequestParam(defaultValue = "view") String mode, Model model) {
		log.info("공지사항 상세 페이지로 이동, mode = " + mode);
		// 상세 조회 일때 
		if(notice_cd != null && !mode.equals("insert")) {
			NoticeVO notice = noticeService.getNoticeDetail(notice_cd);
			model.addAttribute("notice",notice);
		}
		
		model.addAttribute("mode", mode); // view, edit, insert 구분
		return "noticeDetail"; // /WEB-INF/views/noticeDetail.jsp
	}
	
	// 공지사항 등록 처리
	@PostMapping("/insert")
	public String insertNotice(NoticeVO notice) {
		log.info("공지사항 등록 처리");
		noticeService.insertNotice(notice);
		return "redirect:/notice";
	}
	
	// 공지사항 수정 처리
	@PostMapping("/update")
	public String updateNotice(NoticeVO notice) {
		log.info("공지사항 수정 처리");
		noticeService.updateNotice(notice);
		return "redirect:/notice/detail?notice_cd=" + notice.getNotice_cd() + "&mode=view";
	}
	
	//공지사항 삭제 처리(소프트 삭제)
	@PostMapping("/delete")
	public String deleteNotice(@RequestParam("notice_cd")int notice_cd) {
		log.info("공지사항 삭제 처리");
		noticeService.deleteNotice(notice_cd);
		return "redirect:/notice";
	}
}
