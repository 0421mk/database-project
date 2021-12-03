package com.sbs.example.board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

	public void run() {

		Scanner scanner = new Scanner(System.in);
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

		while (true) {
			System.out.printf("명령어) ");
			String cmd = scanner.nextLine();
			cmd = cmd.trim();

			int actionResult = doAction(conn, scanner, cmd);
			
			if(actionResult == -1) {
				break;
			}
		}

		scanner.close();

	}

	private int doAction(Connection conn, Scanner scanner, String cmd) {
		
		if (cmd.equals("article write")) {
			String title;
			String body;

			System.out.println("== 게시글 작성 ==");
			System.out.printf("제목: ");
			title = scanner.nextLine();
			System.out.printf("내용: ");
			body = scanner.nextLine();

			PreparedStatement pstat = null; // SQL 구문을 실행하는 역할

			try {

				String sql = "INSERT INTO article";
				sql += " SET regDate = NOW()";
				sql += ", updateDate = NOW()";
				sql += ", title = \"" + title + "\"";
				sql += ", body = \"" + body + "\"";

				pstat = conn.prepareStatement(sql);
				int afftecRows = pstat.executeUpdate(); // 반환값이 실행된 쿼리의 데이터 수

				System.out.println("affectedRows : " + afftecRows);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null && !conn.isClosed()) {
						conn.close(); // 연결 종료
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if (pstat != null && !pstat.isClosed()) {
						pstat.close(); // 연결 종료
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		} else if (cmd.startsWith("article modify")) {

			int id = Integer.parseInt(cmd.split(" ")[2].trim());

			String title;
			String body;

			System.out.println("== 게시글 수정 ==");
			System.out.printf("새 제목: ");
			title = scanner.nextLine();
			System.out.printf("새 내용: ");
			body = scanner.nextLine();

			PreparedStatement pstat = null; // SQL 구문을 실행하는 역할

			try {

				String sql = "UPDATE article";
				sql += " SET regDate = NOW()";
				sql += ", updateDate = NOW()";
				sql += ", title = \"" + title + "\"";
				sql += ", body = \"" + body + "\"";
				sql += " WHERE id = " + id;

				pstat = conn.prepareStatement(sql);
				pstat.executeUpdate();

			} catch (SQLException e) {
				System.out.println("에러: " + e);
			} finally {
				try {
					if (conn != null && !conn.isClosed()) {
						conn.close(); // 연결 종료
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if (pstat != null && !pstat.isClosed()) {
						pstat.close(); // 연결 종료
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			System.out.printf("%d번 글이 수정되었습니다. \n", id);

		} else if (cmd.equals("article list")) {

			System.out.println("== 게시글 목록 ==");

			PreparedStatement pstat = null; // SQL 구문을 실행하는 역할
			ResultSet rs = null; // ResultSet은 executeQuery 쿼리의 결과값을 저장, next 함수를 통해 데이터 참조 가능

			List<Article> articles = new ArrayList<>(); // 데이터베이스

			try {

				String sql = "SELECT * FROM article";
				sql += " ORDER BY id DESC";

				pstat = conn.prepareStatement(sql);
				rs = pstat.executeQuery(sql);

				while (rs.next()) { // 데이터가 없을때까지 true
					int id = rs.getInt("id");
					String regDate = rs.getString("regDate");
					String updateDate = rs.getString("updateDate");
					String title = rs.getString("title");
					String body = rs.getString("body");

					Article article = new Article(id, regDate, updateDate, title, body);
					articles.add(article);
				}
			} catch (SQLException e) {
				System.out.println("에러: " + e);
			} finally {
				try {
					if (conn != null && !conn.isClosed()) {
						conn.close(); // 연결 종료
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if (pstat != null && !pstat.isClosed()) {
						pstat.close(); // 연결 종료
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (articles.size() == 0) {
				System.out.println("게시글이 존재하지 않습니다.");
				return 0;
			}

			System.out.println("번호 / 제목");
			for (Article article : articles) {
				System.out.printf("%d / %s\n", article.id, article.title);
			}

		} else if (cmd.equals("system exit")) {
			System.out.println("프로그램을 종료합니다.");
			return -1;
		} else {
			System.out.println("잘못된 명령어입니다.");
		}
		
		return 0;
	}

}
