package com.sbs.example.board.session;

import com.sbs.example.board.Member;

public class Session {
	public int loginedMemberId;
	public Member loginedMember;
	
	public Session() {
		loginedMemberId = -1; // 처음 -1로 초기화
	}
}
