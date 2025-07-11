package com.eco.service;

import java.util.List;

import com.eco.domain.vo.NoticeVO;

public interface NoticeService {

	public List<NoticeVO> getNoticeList(String userType);
	public NoticeVO getNoticeDetail(int notice_cd);
	public int insertNotice(NoticeVO notice);
	public int updateNotice(NoticeVO notice);
	public int deleteNotice(int notice_cd);
}
