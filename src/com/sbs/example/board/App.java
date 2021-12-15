package com.sbs.example.board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import com.sbs.example.board.controller.ArticleController;
import com.sbs.example.board.controller.MemberController;
import com.sbs.example.board.session.Session;

public class App {

	public void run() {

		Scanner scanner = new Scanner(System.in);
		Session session = new Session();

		// DB 연결 코드
		Connection conn = null; // DB 접속 객체

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/text_board?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";
			conn = DriverManager.getConnection(url, "root", "");
			
			while (true) {
				System.out.printf("명령어) ");
				String cmd = scanner.nextLine();
				cmd = cmd.trim();

				int actionResult = doAction(conn, scanner, cmd, session);

				if (actionResult == -1) {
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("에러: " + e);
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close(); // 연결 종료
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// DB 연결 코드

		scanner.close();

	}

	private int doAction(Connection conn, Scanner scanner, String cmd, Session session) {
		
		MemberController memberController = new MemberController(conn, scanner, cmd, session);
		ArticleController articleController = new ArticleController(conn, scanner, cmd, session);

		if (cmd.equals("member join")) {
			
			memberController.doJoin();

		} else if (cmd.equals("member login")) {
			
			memberController.doLogin();

		} else if (cmd.equals("member logout")) {
			
			memberController.doLogout();

		} else if (cmd.equals("whoami")) {
			
			memberController.whoami();

		} else if (cmd.equals("article write")) {
			
			articleController.doWrite();

		} else if (cmd.startsWith("article modify ")) {

			articleController.doMoidfy();

		} else if (cmd.equals("article list")) {

			articleController.showList();

		} else if (cmd.startsWith("article detail ")) {

			articleController.showDetail();

		} else if (cmd.startsWith("article delete ")) {

			articleController.doDelete();

		} else if (cmd.equals("system exit")) {
			System.out.println("프로그램을 종료합니다.");
			return -1;
		} else {
			System.out.println("잘못된 명령어입니다.");
		}

		return 0;
	}

}
