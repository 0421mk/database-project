package com.sbs.example.board.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sbs.example.board.Article;

public class JDBCSelectTest {

	public static void main(String[] args) {

		Connection conn = null; // DB 접속 객체
		PreparedStatement pstat = null; // SQL 구문을 실행하는 역할
		ResultSet rs = null; // ResultSet은 executeQuery 쿼리의 결과값을 저장, next 함수를 통해 데이터 참조 가능

		List<Article> articles = new ArrayList<>();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/text_board?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

			conn = DriverManager.getConnection(url, "root", "");

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
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패");
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

		System.out.println("결과 : " + articles);
	}

}
