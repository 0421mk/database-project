package com.sbs.example.board.session;

import com.sbs.example.board.dto.Member;

public class Session {
	private int loginedMemberId;
	private Member loginedMember;
	
	public Session() {
		loginedMemberId = -1; // 처음 -1로 초기화
	}

	public int getLoginedMemberId() {
		return loginedMemberId;
	}

	public void setLoginedMemberId(int loginedMemberId) {
		this.loginedMemberId = loginedMemberId;
	}

	public Member getLoginedMember() {
		return loginedMember;
	}

	public void setLoginedMember(Member loginedMember) {
		this.loginedMember = loginedMember;
	}

}
