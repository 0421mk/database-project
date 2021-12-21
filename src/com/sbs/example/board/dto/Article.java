package com.sbs.example.board.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class Article {
	private int id;
	private LocalDateTime regDate;
	private LocalDateTime updateDate;
	private int memberId;
	private int hit;
	private String title;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getRegDate() {
		return regDate;
	}

	public void setRegDate(LocalDateTime regDate) {
		this.regDate = regDate;
	}

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate) {
		this.updateDate = updateDate;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getExtra_writer() {
		return extra_writer;
	}

	public void setExtra_writer(String extra_writer) {
		this.extra_writer = extra_writer;
	}

	private String body;
	private String extra_writer; // "extra_"는 다른 테이블과 조인해서 얻은 값을 저장합니다.

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
