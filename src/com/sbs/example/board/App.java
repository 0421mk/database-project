package com.sbs.example.board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import com.sbs.example.board.controller.ArticleController;
import com.sbs.example.board.controller.Controller;
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
				
				if (cmd.equals("system exit")) {
					System.out.println("프로그램을 종료합니다.");
					break;
				}
				
				String[] cmdBits = cmd.split(" ");
				
				if (cmdBits.length < 2) {
					System.out.println("존재하지 않는 명령어입니다.");
				}
				
				String controllerName = cmdBits[0];
				
				Controller controller = null;
				
				MemberController memberController = new MemberController(conn, scanner, cmd, session);
				ArticleController articleController = new ArticleController(conn, scanner, cmd, session);
				
				if(controllerName.equals("article")) {
					controller = articleController;
				} else if(controllerName.equals("member")) {
					controller = memberController;
				} else {
					System.out.println("잘못된 명령어입니다.");
				}
				
				controller.doAction();
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

}
