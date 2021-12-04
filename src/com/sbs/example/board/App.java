package com.sbs.example.board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sbs.example.board.util.DBUtil;
import com.sbs.example.board.util.SecSql;

public class App {

	public void run() {

		Scanner scanner = new Scanner(System.in);

		// DB 연결 코드
		Connection conn = null; // DB 접속 객체

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/text_board?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";
			conn = DriverManager.getConnection(url, "root", "");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("에러: " + e);
		}
		// DB 연결 코드

		while (true) {
			System.out.printf("명령어) ");
			String cmd = scanner.nextLine();
			cmd = cmd.trim();

			int actionResult = doAction(conn, scanner, cmd);

			if (actionResult == -1) {
				break;
			}
		}

		scanner.close();

	}

	private int doAction(Connection conn, Scanner scanner, String cmd) {

		if (cmd.equals("member join")) {
			String loginId;
			String loginPw;
			String loginPwConfirm;
			String name;

			SecSql sql = new SecSql();
			
			System.out.println("== 회원가입 ==");

			while (true) {
				System.out.printf("로그인 아이디: ");
				loginId = scanner.nextLine();

				if (loginId.length() == 0) {
					System.out.println("아이디를 입력해주세요.");
					continue;
				}
				
				sql.append("SELECT COUNT(*)");
				sql.append("FROM member");
				sql.append("WHERE loginId = ?", loginId);
				
				int memberCnt = DBUtil.selectRowIntValue(conn, sql);
				
				if(memberCnt > 0) {
					System.out.println("이미 존재하는 아이디입니다.");
					continue;
				}

				break;
			}

			while (true) {
				System.out.printf("로그인 비밀번호: ");
				loginPw = scanner.nextLine();

				if (loginPw.length() == 0) {
					System.out.println("비밀번호를 입력해주세요.");
					continue;
				}

				while (true) {
					System.out.printf("로그인 비밀번호 확인: ");
					loginPwConfirm = scanner.nextLine();

					if (loginPwConfirm.length() == 0) {
						System.out.println("비밀번호 확인을 입력해주세요.");
						continue;
					}

					break;
				}
				
				if (!loginPw.equals(loginPwConfirm)) {
					System.out.println("입력된 비밀번호가 일치하지 않습니다.");
					continue;
				}

				break;
			}
			
			while (true) {
				System.out.printf("이름: ");
				name = scanner.nextLine();

				if (name.length() == 0) {
					System.out.println("이름을 입력해주세요.");
					continue;
				}
				
				break;
			}
			
			sql = new SecSql(); // SecSql 객체 초기화
			sql.append("INSERT INTO member");
			sql.append("SET regDate = NOW()");
			sql.append(", updateDate = NOW()");
			sql.append(", loginId = ?", loginId);
			sql.append(", loginPw = ?", loginPw);
			sql.append(", name = ?", name);
			
			DBUtil.insert(conn, sql);
						
			System.out.printf("%s님 환영합니다. \n", name);

		} else if (cmd.equals("article write")) {
			String title;
			String body;

			System.out.println("== 게시글 작성 ==");
			System.out.printf("제목: ");
			title = scanner.nextLine();
			System.out.printf("내용: ");
			body = scanner.nextLine();

			SecSql sql = new SecSql();

			sql.append("INSERT INTO article");
			sql.append("SET regDate = NOW()");
			sql.append(", updateDate = NOW()");
			sql.append(", title = ?", title);
			sql.append(", body = ?", body);

			int id = DBUtil.insert(conn, sql);

			System.out.printf("%d번 게시물이 생성되었습니다. \n", id);

		} else if (cmd.startsWith("article modify")) {

			int id = Integer.parseInt(cmd.split(" ")[2].trim());

			SecSql sql = new SecSql();

			sql.append("SELECT COUNT(*)");
			sql.append("FROM article");
			sql.append("WHERE id = ?", id);

			int articlesCount = DBUtil.selectRowIntValue(conn, sql);

			if (articlesCount == 0) {
				System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
				return 0;
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

		} else if (cmd.equals("article list")) {

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
				return 0;
			}

			System.out.println("번호 / 제목");
			for (Article article : articles) {
				System.out.printf("%d / %s\n", article.id, article.title);
			}

		} else if (cmd.startsWith("article detail")) {

			int id = Integer.parseInt(cmd.split(" ")[2].trim());

			SecSql sql = new SecSql();

			sql.append("SELECT COUNT(*)");
			sql.append("FROM article");
			sql.append("WHERE id = ?", id);

			int articlesCount = DBUtil.selectRowIntValue(conn, sql);

			if (articlesCount == 0) {
				System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
				return 0;
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

		} else if (cmd.startsWith("article delete")) {

			int id = Integer.parseInt(cmd.split(" ")[2].trim());

			System.out.println("== 게시글 삭제 ==");

			SecSql sql = new SecSql();

			sql.append("SELECT COUNT(*)");
			sql.append("FROM article");
			sql.append("WHERE id = ?", id);

			int articlesCount = DBUtil.selectRowIntValue(conn, sql);

			if (articlesCount == 0) {
				System.out.printf("%d번 게시글이 존재하지 않습니다.\n", id);
				return 0;
			}

			sql = new SecSql();

			sql.append("DELETE FROM article");
			sql.append("WHERE id = ?", id);

			DBUtil.delete(conn, sql);

			System.out.printf("%d번 글이 삭제되었습니다. \n", id);

		} else if (cmd.equals("system exit")) {
			System.out.println("프로그램을 종료합니다.");
			return -1;
		} else {
			System.out.println("잘못된 명령어입니다.");
		}

		return 0;
	}

}
