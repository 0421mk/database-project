package com.sbs.example.board.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sbs.example.board.dto.Article;
import com.sbs.example.board.util.DBUtil;
import com.sbs.example.board.util.SecSql;

public class ArticleDao {
	Connection conn;

	public ArticleDao(Connection conn) {
		this.conn = conn;
	}

	public int doWrite(String title, String body, int loginedMemberId) {
		
		SecSql sql = new SecSql();

		sql.append("INSERT INTO article");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", memberId = ?", loginedMemberId);
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);

		int id = DBUtil.insert(conn, sql);
		
		return id;
		
	}

	public int getArticleCntById(int id) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*)");
		sql.append("FROM article");
		sql.append("WHERE id = ?", id);
		
		return DBUtil.selectRowIntValue(conn, sql);
	}

	public void doModify(String title, String body, int id) {
		
		SecSql sql = new SecSql();

		sql.append("UPDATE article");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);
		sql.append("WHERE id = ?", id);

		DBUtil.update(conn, sql);
		
	}

	public List<Article> getArticles() {
		
		List<Article> articles = new ArrayList<>(); // 데이터베이스

		SecSql sql = new SecSql();
		
		
		sql.append("SELECT *, m.name AS extra_writer");
		sql.append("FROM article AS a");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON a.memberId = m.id"); // Select 쿼리시 DBUtil에서 쿼리 데이터 결과값에 해당하는 메소드를 적절히 사용해야 합니다.
		sql.append("ORDER BY a.id DESC");
		
		List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql); // List<Article>이 사용하기 편합니다.
		// DB에서 데이터를 받을 때는 List<Map<String, Object>> 형태가 편하고 프로그램에서 사용하기에는 List<Article>
		// 형태가 편합니다.

		for (Map<String, Object> articleMap : articleListMap) {
			articles.add(new Article(articleMap));
		}
		
		return articles;
		
	}

	public Article getArticle(int id) {
		SecSql sql = new SecSql();

		sql.append("SELECT *, m.name AS extra_writer");
		sql.append("FROM article AS a");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON a.memberId = m.id");
		sql.append("WHERE a.id = ?", id);
		sql.append("ORDER BY a.id DESC");

		Map<String, Object> articleMap = DBUtil.selectRow(conn, sql);
		Article article = new Article(articleMap);
		
		return article;
	}

	public void doDelete(int id) {
		
		SecSql sql = new SecSql();

		sql.append("DELETE FROM article");
		sql.append("WHERE id = ?", id);

		DBUtil.delete(conn, sql);
		
	}

}
