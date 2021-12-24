package com.sbs.example.board.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class Comment {
	private int id;
	private LocalDateTime regDate;
	private LocalDateTime updateDate;
	private int articleId;
	private int memberId;
	private String title;
	private String body;
	private String extra_writer; // "extra_"는 다른 테이블과 조인해서 얻은 값을 저장합니다.
	
	public Comment(Map<String, Object> commentMap) {
		this.id = (int) commentMap.get("id");
		this.regDate = (LocalDateTime) commentMap.get("regDate");
		this.updateDate = (LocalDateTime) commentMap.get("updateDate");
		this.articleId = (int) commentMap.get("articleId");
		this.memberId = (int) commentMap.get("memberId");
		this.title = (String) commentMap.get("title");
		this.body = (String) commentMap.get("body");
		this.extra_writer = (String) commentMap.get("extra_writer");
		// 자료형 타입 알아내는 방법 : 데이터.getClass().getName();
	}
	
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
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
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
	
}
