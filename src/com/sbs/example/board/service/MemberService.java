package com.sbs.example.board.service;

import java.sql.Connection;

import com.sbs.example.board.dao.MemberDao;
import com.sbs.example.board.dto.Member;

public class MemberService {
	private MemberDao memberDao;

	public MemberService(Connection conn) {
		memberDao = new MemberDao(conn);
	}

	public int getMemberCntByLoginId(String loginId) {
		return memberDao.getMemberCntByLoginId(loginId);
	}

	public void doJoin(String loginId, String loginPw, String name) {
		
		memberDao.doJoin(loginId, loginPw, name);
		
	}

	public Member getArticleByLoginId(String loginId) {
		return memberDao.getArticleByLoginId(loginId);
	}
}
