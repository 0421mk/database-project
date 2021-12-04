package com.sbs.example.board;

import java.time.LocalDateTime;
import java.util.Map;

public class Member {
	public int id;
	public LocalDateTime regDate;
	public LocalDateTime updateDate;
	public String loginId;
	public String loginPw;
	public String name;

	public Member(Map<String, Object> articleMap) {
		this.id = (int) articleMap.get("id");
		this.regDate = (LocalDateTime) articleMap.get("regDate");
		this.updateDate = (LocalDateTime) articleMap.get("updateDate");
		this.loginId = (String) articleMap.get("loginId");
		this.loginPw = (String) articleMap.get("loginPw");
		this.name = (String) articleMap.get("name");
	}
}
