package com.sbs.example.board.controller;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import com.sbs.example.board.dto.Article;
import com.sbs.example.board.service.ArticleService;
import com.sbs.example.board.session.Session;

public class ArticleController extends Controller {

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

	@Override
	public void doAction() {

		if (cmd.equals("article write")) {
			doWrite();
		} else if (cmd.startsWith("article modify ")) {
			doMoidfy();
		} else if (cmd.startsWith("article list")) {
			showList();
		} else if (cmd.startsWith("article detail ")) {
			showDetail();
		} else if (cmd.startsWith("article delete ")) {
			doDelete();
		} else if (cmd.startsWith("article like ")) {
			doLike();
		} else {
			System.out.println("존재하지 않는 명령어입니다.");
		}
	}

	private void doLike() {
		
		if (session.getLoginedMember() == null) {
			System.out.println("로그인 후 이용해주세요.");
			return;
		}

		boolean isInt = cmd.split(" ")[2].matches("-?\\d+");

		if (!isInt) {
			System.out.println("게시글의 ID를 숫자로 입력해주세요.");
			return;
		}

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		int articlesCount = articleService.getArticleCntById(id);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}
		
		System.out.println("== 게시글 추천/비추천 ==");
		System.out.printf(">> [추천] 1, [비추천] 2, [해제] 3, [나가기] 0 <<\n");

		System.out.printf("[article like] 명령어) ");
		int likeType = scanner.nextInt();
		
		scanner.nextLine(); // 입력 버퍼 \n 이 남아있으므로 비워줍니다.
		
		if(likeType == 0) {
			return;
		}
		
		// 이미 추천/반대 했는지 여부 확인
		// 했다면 추천/반대 값을 리턴받음
		// 안했다면 0을 리턴받음
		int likeCheckCnt = articleService.likeCheck(id, session.getLoginedMemberId());
		
		if(likeCheckCnt == 0) {
			if(likeType == 1 || likeType == 2) {
				articleService.insertLike(id, likeType, session.getLoginedMemberId());
				
				String msg = (likeType == 1 ? "추천" : "비추천");
				System.out.println(msg + " 완료");
			} else if(likeType == 3) {
				System.out.println("해제할 추천/반대가 존재하지 않습니다.");
			} else {
				System.out.println("1 또는 2의 숫자만 입력해주세요.");
			}
		} else {
			if(likeType == 3) {
				articleService.deleteLike(id, session.getLoginedMemberId());
				System.out.println("추천/반대를 해제합니다.");
				return;
			}
			
			if(likeType == likeCheckCnt) {
				String msg = (likeType == 1 ? "추천" : "비추천");
				System.out.println("이미 " + msg + "하였습니다.");
				return;
			} else {
				// modify
				articleService.modifyLike(id, likeType, session.getLoginedMemberId());
				
				String msg = (likeType == 1 ? "추천" : "비추천");
				System.out.println(msg + "으로 변경 완료");
			}
		}
		
	}

	private void doWrite() {

		if (session.getLoginedMember() == null) {
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

		int id = articleService.doWrite(title, body, session.getLoginedMemberId());

		System.out.printf("%d번 게시물이 생성되었습니다. \n", id);

	}

	private void doMoidfy() {

		if (session.getLoginedMember() == null) {
			System.out.println("로그인 후 이용해주세요.");
			return;
		}

		boolean isInt = cmd.split(" ")[2].matches("-?\\d+");

		if (!isInt) {
			System.out.println("게시글의 ID를 숫자로 입력해주세요.");
			return;
		}

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		int articlesCount = articleService.getArticleCntById(id);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}

		Article article = articleService.getArticle(id);

		if (article.memberId != session.getLoginedMemberId()) {
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

	private void showList() {

		String[] cmdBits = cmd.split(" "); // 공백 체크
		String searchKeyword = "";
		List<Article> articles;

		System.out.println("== 게시글 목록 ==");

		int page = 1;
		int itemsInAPage = 5;

		while (true) {
			// page 입력받기 때문에 마지막 페이지 초과시 게시글 없음으로 예외처리
			// 음수 입력시 예외처리는 따로 필요
			if (cmdBits.length >= 3) {
				searchKeyword = cmd.substring("article list ".length());
				articles = articleService.getArticlesByKeyword(page, itemsInAPage, searchKeyword);
			} else {
				articles = articleService.getArticles(page, itemsInAPage);
			}

			if (articles.size() == 0) {
				System.out.println("게시글이 존재하지 않습니다.");
				return;
			}

			System.out.println("번호 / 제목 / 작성자");
			for (Article article : articles) {
				System.out.printf("%d / %s / %s\n", article.id, article.title, article.extra_writer);
			}

			int articleCnt = articleService.getArticlesCnt(searchKeyword);
			int lastPage = (int) Math.ceil(articleCnt / (double) itemsInAPage);

			/*
			 * int a = 142; System.out.println(a / 100); => 1 System.out.println(Math.ceil(a
			 * / 100)); => 1.0 System.out.println(a / 100.0); => 1.42
			 * System.out.println(Math.ceil(a / 100.0)); => 2.0 System.out.println((int)
			 * Math.ceil(a / 100.0)); => 2
			 */

			System.out.printf("현재 페이지: %d, 마지막 페이지: %d, 전체 글 수: %d\n", page, lastPage, articleCnt);
			System.out.printf(">> [페이지 이동] 번호, [종료] 0 미만의 수 입력 <<\n");

			System.out.printf("[article list] 명령어) ");
			page = scanner.nextInt();

			scanner.nextLine(); // 입력 버퍼 \n 이 남아있으므로 비워줍니다.

			if (page <= 0) {
				System.out.println("게시판 조회를 종료합니다.");
				break;
			}
		}

	}

	private void showDetail() {

		boolean isInt = cmd.split(" ")[2].matches("-?\\d+");

		if (!isInt) {
			System.out.println("게시글의 ID를 숫자로 입력해주세요.");
			return;
		}

		int id = Integer.parseInt(cmd.split(" ")[2].trim());

		int articlesCount = articleService.getArticleCntById(id);

		if (articlesCount == 0) {
			System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
			return;
		}

		articleService.increaseHit(id);

		Article article = articleService.getArticle(id);
		int likeVal = articleService.getLikeVal(id, 1);
		int disLikeVal = articleService.getLikeVal(id, 2);

		System.out.printf("번호: %d\n", article.id);
		System.out.printf("등록날짜: %s\n", article.regDate);
		System.out.printf("수정날짜: %s\n", article.updateDate);
		System.out.printf("작성자: %s\n", article.extra_writer);
		System.out.printf("조회수: %d\n", article.hit);
		System.out.printf("추천수: %d\n", likeVal);
		System.out.printf("반대수: %d\n", disLikeVal);
		System.out.printf("제목: %s\n", article.title);
		System.out.printf("내용: %s\n", article.body);

	}

	private void doDelete() {

		if (session.getLoginedMember() == null) {
			System.out.println("로그인 후 이용해주세요.");
			return;
		}

		boolean isInt = cmd.split(" ")[2].matches("-?\\d+");

		if (!isInt) {
			System.out.println("게시글의 ID를 숫자로 입력해주세요.");
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

		if (article.memberId != session.getLoginedMemberId()) {
			System.out.println("권한이 없습니다.");
			return;
		}

		articleService.doDelete(id);

		System.out.printf("%d번 글이 삭제되었습니다. \n", id);

	}

}
