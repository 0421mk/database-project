package com.sbs.example.board.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class Article {
	public int id;
	public LocalDateTime regDate;
	public LocalDateTime updateDate;
	public int memberId;
	public int hit;
	public String title;
	public String body;
	public String extra_writer; // "extra_"는 다른 테이블과 조인해서 얻은 값을 저장합니다.

	public Article(Map<String, Object> articleMap) {
		this.id = (int) articleMap.get("id");
		this.regDate = (LocalDateTime) articleMap.get("regDate");
		this.updateDate = (LocalDateTime) articleMap.get("updateDate");
		this.memberId = (int) articleMap.get("memberId");
		this.hit = (int) articleMap.get("hit");
		this.title = (String) articleMap.get("title");
		this.body = (String) articleMap.get("body");
		this.extra_writer = (String) articleMap.get("extra_writer");
		// 자료형 타입 알아내는 방법 : 데이터.getClass().getName();
	}
}
