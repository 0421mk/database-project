package com.sbs.example.board.controller;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.sbs.example.board.dto.Article;
import com.sbs.example.board.dto.Comment;
import com.sbs.example.board.service.ArticleService;
import com.sbs.example.board.session.Session;
import com.sbs.example.board.util.Util;

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
		} else if (cmd.equals("article export")) {
			doHtml();
		} else {
			System.out.println("존재하지 않는 명령어입니다.");
		}
	}

	private void doHtml() {
		
		System.out.println("== HTML 생성을 시작합니다 ==");
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
		List<Article> articles = articleService.getArticles();

		for (Article article : articles) {
			
			String regDate = article.getRegDate().format(formatter);
			
			// 디렉토리 먼저 생성
			String fileName = "./html/" + article.getId() + ".html";
			String html = "<meta charset=\"UTF-8\">";
			html += "<div>번호 : " + article.getId() + "</div>";
			html += "<div>날짜 : " + regDate + "</div>";
			html += "<div>작성자 : " + article.getExtra_writer() + "</div>";
			html += "<div>제목 : " + article.getTitle() + "</div>";
			html += "<div>내용 : " + article.getBody() + "</div>";
			if (article.getId() > 1) {
				html += "<div><a href=\"" + (article.getId() - 1) + ".html\">이전글</a></div>";
			}

			html += "<div><a href=\"" + (article.getId() + 1) + ".html\">다음글</a></div>";
			
			Util.writeFileContents(fileName, html);
			
		}
		
	}

	private void doComment(int articleId) {

		while (true) {

			int totalCommentsCnt = articleService.getCommentsCnt(articleId);
			// 해당 글의 댓글 확인
			System.out.printf("%d번 게시글의 댓글 수: %d\n", articleId, totalCommentsCnt);

			System.out.printf(">> [댓글작성] 1, [수정] 2, [댓글보기] 3, [삭제] 4, [나가기] 0 <<\n");

			int commentType;

			// 예외 처리
			while (true) {
				try {
					System.out.printf("[article detail] 명령어) ");

					// scanner 객체를 재생성해줘야 합니다.
					// 예외로 인해 catch 방문하면 객체 사라지기 때문에 오류뜹니다.
					commentType = new Scanner(System.in).nextInt();

					break;
				} catch (InputMismatchException e) {
					System.out.println("정상적인 숫자를 입력해주세요.");
				}
			}

			if (commentType == 0) {
				System.out.println("article detail 명령을 종료합니다.");
				return;
			} else if (commentType == 1) {
				// 댓글 작성
				
				if (session.getLoginedMember() == null) {
					System.out.println("로그인 후 이용해주세요.");
					continue;
				}

				System.out.println("== 댓글 작성 ==");
				System.out.printf("댓글 제목: ");
				String title = scanner.nextLine();
				System.out.printf("댓글 내용: ");
				String body = scanner.nextLine();

				int commentId = articleService.wirteComment(articleId, title, body, session.getLoginedMemberId());

				System.out.printf("%d번 게시글에 %d번 댓글이 생성되었습니다. \n", articleId, commentId);

			} else if (commentType == 2) {
				
				if (session.getLoginedMember() == null) {
					System.out.println("로그인 후 이용해주세요.");
					continue;
				}
				// 댓글 수정

				// 수정할 댓글 존재 X
				// 수정할 권한 존재 X

				System.out.println("== 댓글 수정 ==");

				int commentId;

				while (true) {
					try {
						System.out.printf(">> [수정할 댓글의 id], [취소] 0 <<) ");
						commentId = new Scanner(System.in).nextInt();

						break;
					} catch (InputMismatchException e) {
						System.out.println("정상적인 숫자를 입력해주세요.");
					}
				}

				if (commentId == 0) {
					continue;
				}

				// 수정할 댓글이 있는지?
				int commentCnt = articleService.getCommentCntById(commentId, articleId);

				if (commentCnt == 0) {
					System.out.println("수정할 댓글이 존재하지 않습니다.");
					continue;
				}

				// 권한 체크
				Comment comment = articleService.getCommentById(commentId);

				if (comment.getMemberId() != session.getLoginedMemberId()) {
					System.out.println("댓글 수정 권한이 없습니다.");
					continue;
				}

				System.out.printf("새 댓글 제목: ");
				String title = scanner.nextLine();
				System.out.printf("새 댓글 내용: ");
				String body = scanner.nextLine();

				articleService.modifyComment(commentId, title, body);

				System.out.printf("%d번 게시글에 %d번 댓글이 수정되었습니다. \n", articleId, commentId);

			} else if (commentType == 3) {
				// 댓글 페이징

				// 수정할 댓글 존재 X
				// 수정할 권한 존재 X

				System.out.println("== 댓글 페이징 ==");

				int page = 1;
				int itemsInAPage = 5;

				while (true) {
					// 현재 게시물 id에 해당하는 댓글 리스트 가져오기
					List<Comment> pageComments = articleService.getCommentsByPage(articleId, page, itemsInAPage);

					if (pageComments.size() == 0) {
						System.out.println("댓글이 존재하지 않습니다.");
						break;
					}
					
					DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
					

					System.out.printf("번호  | 등록날짜              | 제목		| 내용			| 작성자 \n");
					System.out.println(
							"================================================================================================");
					for (Comment comment : pageComments) {
						
						String regDate = comment.getRegDate().format(formatter);
						
						System.out.printf("%-4d | %-19s | %-14s	| %-14s	| %s \n", comment.getId(), regDate, comment.getTitle(), comment.getBody(), comment.getExtra_writer());
					}
					System.out.println(
							"================================================================================================");

					// 해당하는 글에 대한 댓글 수 반환
					int commentsCnt = articleService.getCommentsCnt(articleId);
					int lastCommentPage = (int) Math.ceil(commentsCnt / (double) itemsInAPage);

					System.out.printf("현재 댓글 페이지: %d, 마지막 댓글 페이지: %d, 전체 댓글 수: %d\n", page, lastCommentPage,
							commentsCnt);
					System.out.printf(">> [댓글 페이지 이동] 번호, [종료] 0 미만의 수 입력 <<\n");

					System.out.printf("[article comment page] 명령어) ");
					page = scanner.nextInt();

					scanner.nextLine(); // 입력 버퍼 \n 이 남아있으므로 비워줍니다.

					if (page <= 0) {
						System.out.println("댓글 페이지 조회를 종료합니다.");
						break;
					}
				}

			} else if (commentType == 4) {
				
				if (session.getLoginedMember() == null) {
					System.out.println("로그인 후 이용해주세요.");
					continue;
				}

				// 댓글 삭제

				System.out.println("== 댓글 삭제 ==");

				int commentId;

				while (true) {
					try {
						System.out.printf(">> [삭제할 댓글의 id], [취소] 0 <<) ");
						commentId = new Scanner(System.in).nextInt();

						break;
					} catch (InputMismatchException e) {
						System.out.println("정상적인 숫자를 입력해주세요.");
					}
				}

				if (commentId == 0) {
					continue;
				}

				// 삭제할 댓글이 있는지?
				int commentCnt = articleService.getCommentCntById(commentId, articleId);

				if (commentCnt == 0) {
					System.out.println("삭제할 댓글이 존재하지 않습니다.");
					continue;
				}

				// 권한 체크
				Comment comment = articleService.getCommentById(commentId);

				if (comment.getMemberId() != session.getLoginedMemberId()) {
					System.out.println("댓글 삭제 권한이 없습니다.");
					continue;
				}

				articleService.deleteComment(commentId);

				System.out.printf("%d번 게시글에 %d번 댓글이 삭제되었습니다. \n", articleId, commentId);

			} else {
				System.out.println("표시된 명령어 숫자만 입력해주세요.");
			}
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

		if (likeType == 0) {
			return;
		}

		// 이미 추천/반대 했는지 여부 확인
		// 했다면 추천/반대 값을 리턴받음
		// 안했다면 0을 리턴받음
		int likeCheckCnt = articleService.likeCheck(id, session.getLoginedMemberId());

		if (likeCheckCnt == 0) {
			if (likeType == 1 || likeType == 2) {
				articleService.insertLike(id, likeType, session.getLoginedMemberId());

				String msg = (likeType == 1 ? "추천" : "비추천");
				System.out.println(msg + " 완료");
			} else if (likeType == 3) {
				System.out.println("해제할 추천/반대가 존재하지 않습니다.");
			} else {
				System.out.println(">> [추천] 1, [비추천] 2, [해제] 3, [나가기] 0 <<");
			}
		} else {
			if (likeType == 3) {
				articleService.deleteLike(id, session.getLoginedMemberId());
				System.out.println("추천/반대를 해제합니다.");
				return;
			}

			if (likeType == likeCheckCnt) {
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

		if (article.getMemberId() != session.getLoginedMemberId()) {
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

			
			System.out.println("번호 / 제목 / 작성자 [댓글 수]");
			for (Article article : articles) {
				int commentCnt = articleService.getCommentsCnt(article.getId());

				System.out.printf("%d / %s / %s / [%d]\n", article.getId(), article.getTitle(),
						article.getExtra_writer(), commentCnt);
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

			// 예외 처리
			while (true) {
				try {
					System.out.printf("[article list] 명령어) ");

					// scanner 객체를 재생성해줘야 합니다.
					// 예외로 인해 catch 방문하면 객체 사라지기 때문에 오류뜹니다.
					page = new Scanner(System.in).nextInt();

					break;
				} catch (InputMismatchException e) {
					System.out.println("정상적인 숫자를 입력해주세요.");
				}
			}


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

		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
		String regDate = article.getRegDate().format(formatter);

		System.out.println("== 게시글 상세페이지 ==");
		
		System.out.printf("* 게시글 상세보기===============================================================\n");
		System.out.printf("* 게시글 번호 : %d				작성자 : %s\n", article.getId(), article.getExtra_writer());
		System.out.printf("* 등록일자 : %s		갱신일자 : %s\n", article.getRegDate(), article.getUpdateDate());
		System.out.printf("* 제목 : %s\n", article.getTitle());
		System.out.printf("* 내용 =========================================================================\n");
		System.out.printf(
				"| >> %s \n* ======================================================== 추천 : %-3d 비추천 : %-3d\n",
				article.getBody(), likeVal, disLikeVal);

		System.out.println("\n== 게시글 댓글 ==");

		doComment(id);

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

		if (article.getMemberId() != session.getLoginedMemberId()) {
			System.out.println("권한이 없습니다.");
			return;
		}

		articleService.doDelete(id);

		System.out.printf("%d번 글이 삭제되었습니다. \n", id);

	}

}
