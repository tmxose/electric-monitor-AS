package com.eco.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.eco.domain.StaffVO;

@Mapper
public interface StaffMapper {
	// 직원 로그인
    public StaffVO selectStaffByIdAndPw(@Param("staff_id") String staff_id, @Param("staff_pw") String staff_pw);
    
    // 아이디 중복 조회
    public StaffVO selectStaffById(@Param("staff_id") String staff_id);
    
    // 직원 계정 생성
    public int insertStaff(StaffVO staffVO);
    
    // 직원 정보 수정
    public int updateStaff(StaffVO staffVO);
}
