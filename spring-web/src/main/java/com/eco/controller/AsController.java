package com.eco.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eco.domain.DTO.ASCallenderDTO;
import com.eco.domain.DTO.ASListDTO;
import com.eco.domain.DTO.ASPageResponseDTO;
import com.eco.domain.DTO.AsScheduleResponseDTO;
import com.eco.domain.DTO.GuestDTO;
import com.eco.domain.vo.ASVO;
import com.eco.domain.vo.StaffVO;
import com.eco.domain.vo.UserVO;
import com.eco.service.AsService;
import com.eco.service.MailService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@AllArgsConstructor
@RequestMapping("/as")
public class AsController {
	
	private final AsService asService;
	private final MailService mailService;

	// as신청폼 접속
	@GetMapping("/form")
	public String asForm(@RequestParam(required = false) String selectedDate, HttpSession session,
			RedirectAttributes redirectAttrs) {
		log.info("as신청 페이지 요청");
		
		Object currentUser = session.getAttribute("currentUserInfo");
	    
	    if (currentUser == null) {
	        redirectAttrs.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/login";
	    }
	    
	    if (currentUser instanceof UserVO) {
	        // userVO 관련 추가 처리 필요시 여기 작성
	        return "/as/asForm";
	    } 
	    
	    else if (currentUser instanceof GuestDTO) {
	        // guestDTO 관련 처리 필요시 여기 작성
	        return "/as/asForm";
	    }
	    
	    else {
	        redirectAttrs.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/login";
	    }
	}

	// 예약 차 있는 시간 선택 비활성화
	@GetMapping("/form/booked-times")
	@ResponseBody
	public List<String> getBookedTimes(@RequestParam String selectedDate, @RequestParam String region) {
		log.info("as 예약 불가능 한 시간 선택 비활성화");
		LocalDate date = LocalDate.parse(selectedDate);
		List<String> result = asService.getFullyBookedSlots(date, region);
		log.info(result);
		return result;
	}

	// 일반회원의 as신청
	@PostMapping("/insertCommon")
	public String registerAsByCommon(ASVO vo, @RequestParam String reserve_time, @RequestParam String reserve_date,
			@RequestParam(required = false) String as_facility_custom,
			@RequestParam(required = false) String as_title_custom, HttpSession session,
			RedirectAttributes redirectAttrs) {
		log.info("일반회원의 as신청");

		Object currentUser = session.getAttribute("currentUserInfo");
		
		boolean result = false;
		String mailAddr = "";
		
		if (currentUser instanceof UserVO) {
			UserVO user = (UserVO) currentUser;
			vo.setUser_cd(user.getUser_cd());

			// 예약 날짜 시간 합치기
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
			mailAddr = vo.getUser_mail();
			result = asService.registerAsByCommon(vo);
		} else if (currentUser instanceof GuestDTO) {
			// 예약 날짜 시간 합치기
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

			mailAddr = vo.getGuest_mail();
			result = asService.registerAsByGuest(vo);
			
		} else {
			redirectAttrs.addFlashAttribute("message", "등록 정보가 올바르지 않습니다.");
			return "redirect:/as/form";
		}		
		
		if (result) {
			redirectAttrs.addFlashAttribute("message", "AS 신고가 완료되었습니다.");
			vo.setAs_status("신고 접수");
			mailService.sendAsStatus(mailAddr, vo);
		} else {
			redirectAttrs.addFlashAttribute("message", "AS 신고에 실패하였습니다.");
		}
		return "redirect:/as/detail";
	}

	// 최초 전체 페이지
	@GetMapping("/detail")
	public String asDetail(HttpSession session, RedirectAttributes redirectAttrs, Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
		log.info("as신청 내역 상세 페이지 요청");

	    Object currentUser = session.getAttribute("currentUserInfo");

	    if (currentUser == null) {
	        redirectAttrs.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/login";
	    }

	    List<ASVO> asvo = null;
	    int size = 10;

	    if (currentUser instanceof UserVO) {
	    	int user_cd = ((UserVO) currentUser).getUser_cd();
	    	ASPageResponseDTO pageInfo = asService.getUserAsListWithPaging(user_cd, page, size);
	    	model.addAttribute("pageInfo", pageInfo);
	    	asvo =  pageInfo.getAsList();
	    } else if (currentUser instanceof GuestDTO) {
	        GuestDTO guest = (GuestDTO) currentUser;
	        ASPageResponseDTO pageInfo  = asService.getGuestAsListWithPaging(guest, page, size);
	        model.addAttribute("pageInfo", pageInfo);
	    	asvo =  pageInfo.getAsList();
	    } else {
	        redirectAttrs.addFlashAttribute("message", "로그인 정보가 올바르지 않습니다.");
	        return "redirect:/login";
	    }
	    
	    List<Map<String, Object>> parsedList = parseAsList(asvo);
	    model.addAttribute("userList", parsedList);

	    return "/as/asDetail";
	}
	
	// AJAX: fragment만 반환
	@GetMapping("/detail/list")
	public String asDetailList(HttpSession session, Model model, @RequestParam String sort, @RequestParam(value = "page", defaultValue = "1") int page) {
		log.info("as신청 내역 fragment 요청");
		Object currentUser = session.getAttribute("currentUserInfo");
		if (currentUser == null)
			return "redirect:/login";

		List<ASVO> asvo = null;

		int size = 10;
		if (currentUser instanceof UserVO) {
			int user_cd = ((UserVO) currentUser).getUser_cd();
			if ("reservationDate".equals(sort)) {
				ASPageResponseDTO pageInfo = asService.getUserAsListOrderByAsDateWithPaging(user_cd, page, size);
				model.addAttribute("pageInfo", pageInfo);
				asvo = pageInfo.getAsList();
			} else {
				ASPageResponseDTO pageInfo = asService.getUserAsListWithPaging(user_cd, page, size);
				model.addAttribute("pageInfo", pageInfo);
				asvo = pageInfo.getAsList();
			}
		} else if (currentUser instanceof GuestDTO) {
			GuestDTO guest = (GuestDTO) currentUser;
			if ("reservationDate".equals(sort)) {
				ASPageResponseDTO pageInfo = asService.getGuestAsListOrderByAsDateWithPaging(guest, page, size);
				model.addAttribute("pageInfo", pageInfo);
				asvo = pageInfo.getAsList();
			} else {
				ASPageResponseDTO pageInfo = asService.getGuestAsListWithPaging(guest, page, size);
				model.addAttribute("pageInfo", pageInfo);
				asvo = pageInfo.getAsList();
			}
		} else {
			return "redirect:/login";
		}

		List<Map<String, Object>> parsedList = parseAsList(asvo);
		model.addAttribute("userList", parsedList);
	    return "/as/asList";
	}


	// 날짜/시간 파싱 함수
	private List<Map<String, Object>> parseAsList(List<ASVO> asvo) {
	    List<Map<String, Object>> parsedList = new ArrayList<>();
	    if (asvo == null) {
	    	return parsedList; 
	    }
	    
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	    for (ASVO vo : asvo) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("as", vo);
	        if (vo.getAs_date() != null) {
	            map.put("as_date_str", vo.getAs_date().format(dateFormatter));
	            map.put("as_time_str", vo.getAs_date().format(timeFormatter));
	        } else {
	            map.put("as_date_str", "");
	            map.put("as_time_str", "");
	        }
	        parsedList.add(map);
	    }
	    return parsedList;
	}

	// as신고 상세 정보
	@GetMapping("/info")
	public String asInfo(@RequestParam("as_cd") int as_cd, Model model, HttpSession session, RedirectAttributes redirectAttrs) {
	    log.info("as 예약 상세정보 화면 접속");

	    Object currentUser = session.getAttribute("currentUserInfo");
	    if (currentUser == null) {
	        redirectAttrs.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/login";
	    }

	    ASListDTO asvo = asService.readAsListDetailByUser(as_cd);

	    // 본인 확인 로직
	    boolean authorized = false;
	    if (currentUser instanceof UserVO) {
	        int user_cd = ((UserVO) currentUser).getUser_cd();
	        if (asvo != null && asvo.getUser_cd() == user_cd) {
	            authorized = true;
	        }
	    } else if (currentUser instanceof GuestDTO) {
	        GuestDTO guest = (GuestDTO) currentUser;
	        if (asvo != null && guest.getGuest_mail().equals(asvo.getGuest_mail())
	                && guest.getGuest_nm().equals(asvo.getGuest_nm())) {
	            authorized = true;
	        }
	    }

	    if (!authorized) {
	        redirectAttrs.addFlashAttribute("message", "유효하지 않은 요청이거나 권한이 없습니다.");
	        return "redirect:/as/detail";
	    }

	    
	    if (asvo != null) {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	        String formattedAsDate = asvo.getAs_date().format(formatter);
	        
	        String fullAddr = asvo.getAs_addr();
		    String baseAddr = "";
		    String detailAddr = "";
		    if (fullAddr != null && fullAddr.contains(":")) {
		        String[] parts = fullAddr.split(":", 2);
		        baseAddr = parts[0];
		        detailAddr = parts[1];
		    } else {
		        baseAddr = fullAddr != null ? fullAddr : "";
		        detailAddr = "";
		    }
	        
	        model.addAttribute("asVO", asvo);
	        model.addAttribute("formattedAsDate", formattedAsDate);
	        model.addAttribute("base_addr", baseAddr);
	        model.addAttribute("detail_addr", detailAddr);
	    }
	    return "/as/asInfo";
	}
	
	// AS 신고 수정 화면
	@GetMapping("/edit")
	public String asEdit(@RequestParam("as_cd") int as_cd, Model model, HttpSession session, RedirectAttributes redirectAttrs) {
	    log.info("as 예약 수정 화면 접속");

	    Object currentUser = session.getAttribute("currentUserInfo");
	    if (currentUser == null) {
	        redirectAttrs.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/login";
	    }

	    ASVO asvo = asService.readAsDetailByUser(as_cd);

	    // 본인 확인 로직
	    boolean authorized = false;
	    if (currentUser instanceof UserVO) {
	        int user_cd = ((UserVO) currentUser).getUser_cd();
	        if (asvo != null && asvo.getUser_cd() == user_cd) {
	            authorized = true;
	        }
	    } else if (currentUser instanceof GuestDTO) {
	        GuestDTO guest = (GuestDTO) currentUser;
	        if (asvo != null && guest.getGuest_mail().equals(asvo.getGuest_mail())
	                && guest.getGuest_nm().equals(asvo.getGuest_nm())) {
	            authorized = true;
	        }
	    }

	    if (!authorized) {
	        redirectAttrs.addFlashAttribute("message", "유효하지 않은 요청이거나 권한이 없습니다.");
	        return "redirect:/as/detail";
	    }

	    // 주소 분리
	    String fullAddr = asvo.getAs_addr();
	    String baseAddr = "";
	    String detailAddr = "";
	    if (fullAddr != null && fullAddr.contains(":")) {
	        String[] parts = fullAddr.split(":", 2);
	        baseAddr = parts[0];
	        detailAddr = parts[1];
	    } else {
	        baseAddr = fullAddr != null ? fullAddr : "";
	        detailAddr = "";
	    }

	    model.addAttribute("asVO", asvo);
	    model.addAttribute("base_addr", baseAddr);
	    model.addAttribute("detail_addr", detailAddr);

	    return "/as/asEdit";
	}


	// 일반회원의 as수정
	@PostMapping("/updateCommon")
	public String eidtAsByCommon(ASVO vo, @RequestParam String reserve_time, @RequestParam String reserve_date,
			@RequestParam(required = false) String as_facility_custom,
			@RequestParam(required = false) String as_title_custom, HttpSession session,
			RedirectAttributes redirectAttrs) {
		log.info("일반 회원의 as 예약 수정");
		UserVO user = (UserVO) session.getAttribute("currentUserInfo");
		vo.setUser_cd(user.getUser_cd());
		
		// 예약 날짜 시간 합치기
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
		
		// 수정 가능한 상태인지 체크
		String status = vo.getAs_status();
		if (!("신고 접수".equals(status))) {
			redirectAttrs.addFlashAttribute("message", "현재 상태에서는 수정할 수 없습니다.");
			return "redirect:/as/detail";
		}

		boolean result = asService.editAsListByCommon(vo);
		if (result) {
			redirectAttrs.addFlashAttribute("message", "AS 수정이 완료되었습니다.");
			mailService.sendAsStatus(vo.getUser_mail(), vo);
		} else {
			redirectAttrs.addFlashAttribute("message", "AS 수정에 실패하였습니다.");
		}
		return "redirect:/as/detail";
	}

	// 게스트의 AS 수정
	@PostMapping("/updateGuest")
	public String editAsByGuest(ASVO vo,
	                             @RequestParam String reserve_time,
	                             @RequestParam String reserve_date,
	                             @RequestParam(required = false) String as_facility_custom,
	                             @RequestParam(required = false) String as_title_custom,
	                             HttpSession session,
	                             RedirectAttributes redirectAttrs) {
	    log.info("게스트의 as 예약 수정");

	    GuestDTO guest = (GuestDTO) session.getAttribute("currentUserInfo");
	    if (guest == null) {
	        redirectAttrs.addFlashAttribute("message", "로그인 후 이용해주세요.");
	        return "redirect:/login";
	    }

	    vo.setGuest_mail(guest.getGuest_mail());
	    vo.setGuest_nm(guest.getGuest_nm());

	    LocalDate localDate = LocalDate.parse(reserve_date);
	    LocalDateTime combinedDateTime = LocalDateTime.parse(localDate + "T" + reserve_time + ":00");
	    vo.setAs_date(combinedDateTime);

	    if ("기타".equals(vo.getAs_facility()) && as_facility_custom != null && !as_facility_custom.isBlank()) {
	        vo.setAs_facility(as_facility_custom);
	    }
	    if ("기타".equals(vo.getAs_title()) && as_title_custom != null && !as_title_custom.isBlank()) {
	        vo.setAs_title(as_title_custom);
	    }

	    if (!"신고 접수".equals(vo.getAs_status())) {
	        redirectAttrs.addFlashAttribute("message", "현재 상태에서는 수정할 수 없습니다.");
	        return "redirect:/as/detail";
	    }

	    boolean result = asService.editAsListByGuest(vo);
	    if (result) {
			redirectAttrs.addFlashAttribute("message", "AS 수정이 완료되었습니다.");
			mailService.sendAsStatus(vo.getGuest_mail(), vo);
		} else {
			redirectAttrs.addFlashAttribute("message", "AS 수정에 실패하였습니다.");
		}
	    return "redirect:/as/detail";
	}

	// 일반 회원 신고 삭제
	@PostMapping("/cancleCommon")
	public String asCancle(@RequestParam("as_cd") int as_cd, RedirectAttributes redirectAttrs) {
		log.info("일반 회원의 as 예약 취소");
		ASVO vo = asService.readAsDetailByUser(as_cd);
		String status = vo.getAs_status();
		// 취소 가능한 상태인지 체크
		if (!("신고 접수".equals(status))) {
			redirectAttrs.addFlashAttribute("message", "현재 상태에서는 취소할 수 없습니다.");
			return "redirect:/as/detail";
		}
		
		boolean result = asService.cancleAsListByCommon(as_cd);
		if (result) {
			log.info("예약 취소");
			redirectAttrs.addFlashAttribute("message", "AS 신청을 취소하였습니다.");
			String mailTo = vo.getUser_mail();
			if (mailTo == null || mailTo.trim().isEmpty()) {
			    mailTo = vo.getGuest_mail();
			}

			if (mailTo != null && !mailTo.trim().isEmpty()) {
				vo.setAs_status("예약 취소");
			    mailService.sendAsStatus(mailTo, vo);
			} else {
			    // 둘 다 없다면? 로그만 남기거나 예외 처리
			    System.out.println("메일 발송 실패: 사용자 메일과 게스트 메일이 모두 null입니다.");
			}
		} else {
			redirectAttrs.addFlashAttribute("message", "AS 신청 취소에 실패하였습니다.");
		}
		return "redirect:/as/detail";
	}

	//asOrder 페이지로 이동
	@GetMapping("/order")
	public String asOderPage(HttpSession session, RedirectAttributes redirectAttrs) {
		Object currentUser = session.getAttribute("currentUserInfo");
        // 비정상적 루트로 접근 제한
        boolean accessAllow = false;
        if (currentUser instanceof UserVO) {
        	accessAllow = false;
        } else if(currentUser instanceof StaffVO) {
        	accessAllow = true;
        } else {
        	accessAllow = false;
        }
        
        if (accessAllow) {
        	log.info("직원, 관리자의 as목록 확인 페이지로 이동");
        	return "/as/asOrder";
        } else {
        	redirectAttrs.addFlashAttribute("message", "잘못된 접근입니다.");
        	return "redirect:/";
        }
	}

	// AS List 표의 하나의 행의 상세정보 버튼 클릭
	@GetMapping(value = "/task/{as_cd}", produces = "application/json")
	@ResponseBody
	public ASListDTO getAsTask(@PathVariable("as_cd") int as_cd) {
		log.info("상세 정보 보기");
		ASListDTO asList = asService.getAsTask(as_cd);
		
		if(asList != null && asList.getAs_addr() != null) {
			String[] parts = asList.getAs_addr().split(":", 2);
			if (parts.length == 2) {
				asList.setAs_addr(parts[0] + " " + parts[1]); // 도로명 + 상세주소
			} else {
				asList.setAs_addr(parts[0]); // 혹시 ':'이 없을 경우를 대비
			}
		}
		return asList;
	}

	// 상태정보 업데이트
	@PostMapping("/updateStatus")
	@ResponseBody
	public String updateStatus(@RequestParam("as_cd") int as_cd, @RequestParam("as_status") String as_status) {
		// 1️ 상태 업데이트
	    asService.updateStatus(as_cd, as_status);

	    // 2️ 업데이트된 AS 정보 다시 가져오기 (상태 포함)
	    ASVO vo = asService.readAsDetailByUser(as_cd); 

	    // 3️ 메일 보낼 대상 선택
	    String mailTo = vo.getUser_mail();
	    if (mailTo == null || mailTo.trim().isEmpty()) {
	        mailTo = vo.getGuest_mail();
	    }

	    // 4️ 메일 발송
	    if (mailTo != null && !mailTo.trim().isEmpty()) {
	        mailService.sendAsStatus(mailTo, vo);
	    } else {
	        System.out.println("메일 발송 실패: 사용자 메일과 게스트 메일이 모두 없음");
	    }

		return "success";
	}

	// 기간별 조회
	@GetMapping("/schedule")
	@ResponseBody
	public List<ASListDTO> getScheduleByPeriodAndStaff(@RequestParam String startDate, @RequestParam String endDate, 
		@RequestParam(required = false) String staffInfo, HttpSession session) {
		log.info("기간/담당자명 기준 조회");
		Object obj = session.getAttribute("currentUserInfo");

		if (obj == null) {
			// 로그인 정보 없으면 빈 리스트 반환 (또는 예외처리)
			return List.of();
		}

		LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
		LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
		
		if (staffInfo == null) {
	        staffInfo = "";
	    }

		if (obj instanceof StaffVO) {
			StaffVO staff = (StaffVO) obj;
			if ("admin".equalsIgnoreCase(staff.getStaff_role())) {
				// 관리자면 모든 사용자 다 조회
				return asService.getScheduleByPeriodAndStaff(start, end, staffInfo);
			} else {
				// 일반 직원이면 본인 스케줄만 조회
				staffInfo = ((StaffVO) obj).getStaff_id();
				return asService.getScheduleByStaffAndDate(start, end, staffInfo);
			}
		} else {
			// 예외 케이스 처리, 빈 리스트 반환
			return List.of();
		}
	}
	
	/**
     * 기간 및 담당자명 기준으로 AS 일정을 페이징 처리하여 조회합니다.
     * 사용자 권한에 따라 조회 범위가 달라집니다.
     *
     * @param startDate 조회 시작일 (YYYY-MM-DD)
     * @param endDate 조회 종료일 (YYYY-MM-DD)
     * @param staffInfo 담당자명 (선택 사항, 관리자만 유효)
     * @param page 현재 페이지 번호 (1-based, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @param session 현재 세션 (사용자 정보 확인용)
     * @return 페이징 정보와 함께 ASListDTO 목록을 담은 AsScheduleResponse 객체
     */
    @GetMapping("/schedule2")
    public ResponseEntity<AsScheduleResponseDTO> getScheduleByPeriodAndStaffPaged(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String staffInfo,
            @RequestParam(defaultValue = "1") int page, // 1-based 페이지 번호
            @RequestParam(defaultValue = "10") int size, // 페이지당 항목 수
            HttpSession session) {

		log.info("기간/담당자명 기준 조회 (페이징): startDate=" + startDate + ", endDate=" + endDate + ", staffInfo=" + staffInfo
				+ ", page=" + page + ", size=" + size);

        Object obj = session.getAttribute("currentUserInfo");

        // 로그인 정보 없으면 권한 없음 처리
        if (obj == null) {
            log.warn("AS 일정 조회 요청 - 로그인 정보 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                                 .body(new AsScheduleResponseDTO()); // 빈 응답 DTO 반환
        }

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        // staffInfo가 null이면 빈 문자열로 초기화
        if (staffInfo == null) {
            staffInfo = "";
        }
        
        // Offset과 Limit 계산: page는 1부터 시작하므로 offset은 (page-1) * size
        int offset = (page - 1) * size;
        int limit = size; // 한 페이지에 가져올 데이터 개수
        
        AsScheduleResponseDTO response;

        if (obj instanceof StaffVO) {
            StaffVO staff = (StaffVO) obj;
            if ("admin".equalsIgnoreCase(staff.getStaff_role())) {
                // 관리자면 모든 사용자 AS를 페이징하여 조회
            	 response = asService.getScheduleByPeriodAndStaffPaged(start, end, staffInfo, offset, limit); // 수정된 부분
 				log.info("관리자 AS 일정 조회 완료: totalElements=" + response.getTotalElements() + ", currentPage=" + response.getCurrentPage());
            } else {
                // 일반 직원이면 본인 스케줄만 페이징하여 조회
                String staffId = staff.getStaff_id();
                response = asService.getScheduleByStaffAndDatePaged(start, end, staffId, offset, limit); // 수정된 부분
                log.info("직원 '"+staffId+"' AS 일정 조회 완료: totalElements="+response.getTotalElements()+", currentPage=" + response.getCurrentPage());
            }
        } else {
            log.warn("AS 일정 조회 요청 - 권한 없는 사용자 유형: " + obj.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body(new AsScheduleResponseDTO());
        }
        return ResponseEntity.ok(response); // HTTP 200 OK와 함께 DTO 반환
    }
    
	// 전체 일정 확인 페이지 이동
	@GetMapping("/calendar")
	public String calendarPage(HttpSession session, RedirectAttributes redirectAttrs) {
		Object currentUser = session.getAttribute("currentUserInfo");
        // 비정상적 루트로 접근 제한
        boolean accessAllow = false;
        if (currentUser instanceof UserVO) {
        	accessAllow = false;
        } else if(currentUser instanceof StaffVO) {
        	accessAllow = true;
        } else {
        	accessAllow = false;
        }
        
        if (accessAllow) {
        	log.info("직원, 관리자의 as목록 확인 페이지로 이동");
        	return "/as/asCalendar";
        } else {
        	redirectAttrs.addFlashAttribute("message", "잘못된 접근입니다.");
        	return "redirect:/";
        }
	}
	
	// 캘린더 데이터
	@GetMapping(value = "/calendar-data", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ASCallenderDTO getAllScheduleForCalendar(@RequestParam(required = false) String staff, HttpSession session) {
	    Object obj = session.getAttribute("currentUserInfo");
		ASCallenderDTO calander = new ASCallenderDTO();
		List<ASListDTO> scheduleList = new ArrayList<>();
		String role = "";
		
	    if (obj instanceof StaffVO) {
	    	StaffVO staffVO = (StaffVO) obj;
	    	role = staffVO.getStaff_role();
	    	String staffIdOrName = (staff == null || staff.isBlank()) ? "" : staff.trim();
	    	
	    	LocalDate startDate = LocalDate.of(2020, 1, 1);
	    	LocalDate endDate = LocalDate.of(2030, 12, 31);
	    	
	    	if ("admin".equalsIgnoreCase(role)) {
	    		// 관리자는 모든 일정
	    		scheduleList = asService.getScheduleByPeriodAndStaff(startDate, endDate, staffIdOrName);
	    	} else {
	    		// 직원은 본인 일정만
	    		scheduleList = asService.getScheduleByStaffAndDate(startDate, endDate, staffVO.getStaff_id());
	    	}
	    	
	    	for (ASListDTO dto : scheduleList) {
	            if (dto.getAs_status() != null) {
	                dto.setAs_status(dto.getAs_status().replace("\u0000", ""));
	            }
	            if (dto.getAs_title() != null) {
	                dto.setAs_title(dto.getAs_title().replace("\u0000", ""));
	            }
	            if (dto.getStaff_nm() != null) {
	                dto.setStaff_nm(dto.getStaff_nm().replace("\u0000", ""));
	            }
	        }
	    	
	    } else {
	    	calander.setError("unauthorized");
	    	calander.setRole("none");
	    	calander.setEvents(List.of());
	        return calander;
		}
	    System.out.println("scheduleList size: " + scheduleList.size());
	    calander.setError("");
	    calander.setRole(role);
	    calander.setEvents(scheduleList);
        return calander;
	}
	
	// AS 기사 배정 지역 변경
	@GetMapping("/management")
	public String managementPage() {
		return "/as/asManagement";
	}
}
