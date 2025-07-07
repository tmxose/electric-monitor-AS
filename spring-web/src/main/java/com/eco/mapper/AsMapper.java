package com.eco.mapper;

import java.util.List;

import com.eco.domain.ASVO;

public interface AsMapper {
	// AS 신고내역 전체 조회
	public List<ASVO> selectAllAsList();
	
	// 기사에 해당하는 AS 신고내역 조회
	public List<ASVO> selectAsListByStaff(int staffCd);
	// 기사에 해당하는 AS 신고내역 조회
	public List<ASVO> selectAsListByUser(int user_cd);
	
	// 일반 회원의 AS 신고
	public int insertAsListByCommon(ASVO asvo);
	
	// AS 신고 수정 화면
	public ASVO selectAsDetailByCommon(int as_cd);
	// AS 신고 수정
	public int updateAsListByCommon(ASVO asvo);
	
	// AS 신고 삭제
	public int deleteAsListByCommon(int as_cd);
	
	
}
