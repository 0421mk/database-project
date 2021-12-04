package com.sbs.example.board.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sbs.example.board.dto.Article;
import com.sbs.example.board.service.ArticleService;
import com.sbs.example.board.session.Session;
import com.sbs.example.board.util.DBUtil;
import com.sbs.example.board.util.SecSql;

public class ArticleController {
	
	private ArticleService articleService;
	
	private Connection conn;
	private Scanner scanner;
	private String cmd;
	private Session session;
	
	// DB 접근은 DAO에서 합니다.
	// 하지만 DAO에 바로 접근할 수 없습니다. Service를 거치는게 관례입니다.
	// scanner, cmd, session은 컨트롤러에서 클라이언트를 상대할 때 사용하는 객체 및 변수들입니다.

	public ArticleController(Connection conn, Scanner scanner, String cmd, Session session) {
		this.conn = conn;
		this.scanner = scanner;
		this.cmd = cmd;
		this.session = session;
		
		articleService = new ArticleService(conn);
	}

	public void doWrite() {

		String title;
		String body;

		System.out.println("== 게시글 작성 ==");
		System.out.printf("제목: ");
		title = scanner.nextLine();
		System.out.printf("내용: ");
		body = scanner.nextLine();

		int id = articleService.doWrite(title, body);

		System.out.printf("%d번 게시물이 생성되었습니다. \n", id);

	}

	public void doMoidfy() {

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*)");
		sql.append("FROM article");
		sql.append("WHERE id = ?", id);

		int articlesCount = DBUtil.selectRowIntValue(conn, sql);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}

		String title;
		String body;

		System.out.println("== 게시글 수정 ==");
		System.out.printf("새 제목: ");
		title = scanner.nextLine();
		System.out.printf("새 내용: ");
		body = scanner.nextLine();

		sql = new SecSql();

		sql.append("UPDATE article");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);
		sql.append("WHERE id = ?", id);

		DBUtil.update(conn, sql);

		System.out.printf("%d번 글이 수정되었습니다. \n", id);

	}

	public void showList() {

		System.out.println("== 게시글 목록 ==");

		List<Article> articles = new ArrayList<>(); // 데이터베이스

		SecSql sql = new SecSql();

		sql.append("SELECT * FROM article");
		sql.append("ORDER BY id DESC"); // Select 쿼리시 DBUtil에서 쿼리 데이터 결과값에 해당하는 메소드를 적절히 사용해야 합니다.

		List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql); // List<Article>이 사용하기 편합니다.
		// DB에서 데이터를 받을 때는 List<Map<String, Object>> 형태가 편하고 프로그램에서 사용하기에는 List<Article>
		// 형태가 편합니다.

		for (Map<String, Object> articleMap : articleListMap) {
			articles.add(new Article(articleMap));
		}

		if (articles.size() == 0) {
			System.out.println("게시글이 존재하지 않습니다.");
			return;
		}

		System.out.println("번호 / 제목");
		for (Article article : articles) {
			System.out.printf("%d / %s\n", article.id, article.title);
		}

	}

	public void showDetail() {

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*)");
		sql.append("FROM article");
		sql.append("WHERE id = ?", id);

		int articlesCount = DBUtil.selectRowIntValue(conn, sql);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}

		sql = new SecSql();

		sql.append("SELECT *");
		sql.append("FROM article");
		sql.append("WHERE id = ?", id);

		Map<String, Object> articleMap = DBUtil.selectRow(conn, sql);

		Article article = new Article(articleMap);

		System.out.printf("번호: %d\n", article.id);
		System.out.printf("등록날짜: %s\n", article.regDate);
		System.out.printf("수정날짜: %s\n", article.updateDate);
		System.out.printf("제목: %s\n", article.title);
		System.out.printf("내용: %s\n", article.body);

	}

	public void doDelete() {

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		System.out.println("== 게시글 삭제 ==");

		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*)");
		sql.append("FROM article");
		sql.append("WHERE id = ?", id);

		int articlesCount = DBUtil.selectRowIntValue(conn, sql);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}

		sql = new SecSql();

		sql.append("DELETE FROM article");
		sql.append("WHERE id = ?", id);

		DBUtil.delete(conn, sql);

		System.out.printf("%d번 글이 삭제되었습니다. \n", id);

	}

}
