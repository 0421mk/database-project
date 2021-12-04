package com.sbs.example.board.controller;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import com.sbs.example.board.dto.Article;
import com.sbs.example.board.service.ArticleService;
import com.sbs.example.board.session.Session;

public class ArticleController {

	private ArticleService articleService;

	private Scanner scanner;
	private String cmd;
	private Session session;

	// DB 접근은 DAO에서 합니다.
	// 하지만 DAO에 바로 접근할 수 없습니다. Service를 거치는게 관례입니다.
	// scanner, cmd, session은 컨트롤러에서 클라이언트를 상대할 때 사용하는 객체 및 변수들입니다.

	public ArticleController(Connection conn, Scanner scanner, String cmd, Session session) {
		this.scanner = scanner;
		this.cmd = cmd;
		this.session = session;

		articleService = new ArticleService(conn);
	}

	public void doWrite() {

		if (session.loginedMember == null) {
			System.out.println("로그인 후 이용해주세요.");
			return;
		}

		String title;
		String body;

		System.out.println("== 게시글 작성 ==");
		System.out.printf("제목: ");
		title = scanner.nextLine();
		System.out.printf("내용: ");
		body = scanner.nextLine();

		int id = articleService.doWrite(title, body, session.loginedMemberId);

		System.out.printf("%d번 게시물이 생성되었습니다. \n", id);

	}

	public void doMoidfy() {

		if (session.loginedMember == null) {
			System.out.println("로그인 후 이용해주세요.");
			return;
		}

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		int articlesCount = articleService.getArticleCntById(id);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}

		Article article = articleService.getArticle(id);

		if (article.memberId != session.loginedMemberId) {
			System.out.println("권한이 없습니다.");
			return;
		}

		String title;
		String body;

		System.out.println("== 게시글 수정 ==");
		System.out.printf("새 제목: ");
		title = scanner.nextLine();
		System.out.printf("새 내용: ");
		body = scanner.nextLine();

		articleService.doModify(title, body, id);

		System.out.printf("%d번 글이 수정되었습니다. \n", id);

	}

	public void showList() {

		System.out.println("== 게시글 목록 ==");

		List<Article> articles = articleService.getArticles();

		if (articles.size() == 0) {
			System.out.println("게시글이 존재하지 않습니다.");
			return;
		}

		System.out.println("번호 / 제목 / 작성자");
		for (Article article : articles) {
			System.out.printf("%d / %s / %s\n", article.id, article.title, article.extra_writer);
		}

	}

	public void showDetail() {

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		int articlesCount = articleService.getArticleCntById(id);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}

		Article article = articleService.getArticle(id);

		System.out.printf("번호: %d\n", article.id);
		System.out.printf("등록날짜: %s\n", article.regDate);
		System.out.printf("수정날짜: %s\n", article.updateDate);
		System.out.printf("작성자: %s\n", article.extra_writer);
		System.out.printf("제목: %s\n", article.title);
		System.out.printf("내용: %s\n", article.body);

	}

	public void doDelete() {

		if (session.loginedMember == null) {
			System.out.println("로그인 후 이용해주세요.");
			return;
		}

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		System.out.println("== 게시글 삭제 ==");

		int articlesCount = articleService.getArticleCntById(id);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}

		Article article = articleService.getArticle(id);

		if (article.memberId != session.loginedMemberId) {
			System.out.println("권한이 없습니다.");
			return;
		}

		articleService.doDelete(id);

		System.out.printf("%d번 글이 삭제되었습니다. \n", id);

	}

}
