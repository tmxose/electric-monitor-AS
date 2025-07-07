package com.eco.service;

import java.time.LocalDate;
import java.util.List;

import com.eco.domain.ASVO;

public interface AsService {

	// AS 신고내역 중 해당 날짜의 예약 시간 조회
	public List<String> getTotalAs(LocalDate date);
	
	// 기사, 관리자의 AS 리스트 가져오는 함수
	public List<ASVO> getAsList(String userType, int staffCd);
	// 사용자의의 AS 리스트 가져오는 함수
	public List<ASVO> getUserAsList(int user_cd);
	
	// 일반 회원의 AS 신고
	public boolean registerAsByCommon(ASVO asvo);
}
