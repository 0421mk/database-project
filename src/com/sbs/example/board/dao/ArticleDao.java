package com.sbs.example.board.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sbs.example.board.dto.Article;
import com.sbs.example.board.dto.Comment;
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
		sql.append(", hit = 0");
		// 조회수 기능 구현 후 쿼리 수정

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
		sql.append("SET updateDate = NOW()");
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);
		sql.append("WHERE id = ?", id);

		DBUtil.update(conn, sql);
		
	}

	public List<Article> getArticles(int limitFrom, int limitTake) {
		
		List<Article> articles = new ArrayList<>(); // 데이터베이스

		SecSql sql = new SecSql();
		
		sql.append("SELECT *, m.name AS extra_writer");
		sql.append("FROM article AS a");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON a.memberId = m.id"); // Select 쿼리시 DBUtil에서 쿼리 데이터 결과값에 해당하는 메소드를 적절히 사용해야 합니다.
		sql.append("ORDER BY a.id DESC");
		sql.append("LIMIT ?, ?", limitFrom, limitTake);
		
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

	public List<Article> getArticlesByKeyword(int limitFrom, int limitTake, String searchKeyword) {
		
		List<Article> articles = new ArrayList<>(); // 데이터베이스

		SecSql sql = new SecSql();
		
		sql.append("SELECT *, m.name AS extra_writer");
		sql.append("FROM article AS a");
		sql.append("LEFT JOIN `member` AS m");
		sql.append("ON a.memberId = m.id"); // Select 쿼리시 DBUtil에서 쿼리 데이터 결과값에 해당하는 메소드를 적절히 사용해야 합니다.
		sql.append("WHERE a.title LIKE CONCAT('%', ?, '%')", searchKeyword); // 작은 따옴표를 escape하려면 하나 더 붙여주면 되는데 그러다보면 헷갈려서 CONCAT 사용
		sql.append("ORDER BY a.id DESC");
		sql.append("LIMIT ?, ?", limitFrom, limitTake);
		
		List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql); // List<Article>이 사용하기 편합니다.
		// DB에서 데이터를 받을 때는 List<Map<String, Object>> 형태가 편하고 프로그램에서 사용하기에는 List<Article>
		// 형태가 편합니다.

		for (Map<String, Object> articleMap : articleListMap) {
			articles.add(new Article(articleMap));
		}
		
		return articles;
		
	}

	public void increaseHit(int id) {
		
		SecSql sql = new SecSql();

		sql.append("UPDATE article");
		sql.append("SET hit = hit + 1");
		sql.append("WHERE id = ?", id);

		DBUtil.update(conn, sql);
		
	}

	public int getArticleCnt(String searchKeyword) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*)");
		sql.append("FROM article");
		if(searchKeyword != "") {
			sql.append("WHERE title LIKE CONCAT('%', ?, '%')", searchKeyword);
		}
		
		return DBUtil.selectRowIntValue(conn, sql);
	}

	public void insertLike(int id, int likeType, int loginedMemberId) {
		
		SecSql sql = new SecSql();
		
		sql.append("INSERT INTO `like`");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", articleId = ?", id);
		sql.append(", memberId = ?", loginedMemberId);
		sql.append(", likeType = ?", likeType);
		
		DBUtil.insert(conn, sql);
		
	}

	public int likeCheck(int id, int loginedMemberId) {
		
		SecSql sql = new SecSql();

		sql.append("SELECT");
		sql.append("CASE WHEN COUNT(*) != 0");
		sql.append("THEN likeType ELSE 0 END");
		sql.append("FROM `like`");
		sql.append("WHERE articleId = ?", id);
		sql.append("AND memberId = ?", loginedMemberId);
		
		return DBUtil.selectRowIntValue(conn, sql);
	}

	public void modifyLike(int id, int likeType, int loginedMemberId) {
		
		SecSql sql = new SecSql();

		sql.append("UPDATE `like`");
		sql.append("SET updateDate = NOW()");
		sql.append(",likeType = ?", likeType);
		sql.append("WHERE articleId = ?", id);
		sql.append("AND memberId = ?", loginedMemberId);
		
		DBUtil.update(conn, sql);
		
	}

	public int getLikeVal(int id, int likeType) {

		SecSql sql = new SecSql();

		// 해당 글의 추천수와 반대수 가져오기
		sql.append("SELECT COUNT(*)");
		sql.append("FROM `like`");
		sql.append("WHERE articleId = ? AND likeType = ?", id, likeType);
		
		return DBUtil.selectRowIntValue(conn, sql);
		
	}

	public void deleteLike(int id, int loginedMemberId) {
		
		SecSql sql = new SecSql();

		sql.append("DELETE FROM `like`");
		sql.append("WHERE articleId = ?", id);
		sql.append("AND memberId = ?", loginedMemberId);

		DBUtil.delete(conn, sql);
		
	}

	public int writeComment(int id, String title, String body, int loginedMemberId) {
		
		SecSql sql = new SecSql();
		
		sql.append("INSERT INTO comment");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", articleId = ?", id);
		sql.append(", memberId = ?", loginedMemberId);
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);
		// 조회수 기능 구현 후 쿼리 수정
		
		return DBUtil.insert(conn, sql);
		
	}

	public List<Comment> getCommentsById(int id) {
		
		SecSql sql = new SecSql();
		
		sql.append("SELECT c.*, m.name AS extra_writer");
		sql.append("FROM `comment` AS c");
		sql.append("INNER JOIN `member` AS m");
		sql.append("ON c.memberId = m.id");
		sql.append("WHERE articleId = ?", id);
		
		List<Map<String, Object>> commentListMap = DBUtil.selectRows(conn, sql); // List<Article>이 사용하기 편합니다.
		// DB에서 데이터를 받을 때는 List<Map<String, Object>> 형태가 편하고 프로그램에서 사용하기에는 List<Article>
		// 형태가 편합니다.

		List<Comment> comments = new ArrayList<>(); // 데이터베이스
		
		for (Map<String, Object> commentMap : commentListMap) {
			comments.add(new Comment(commentMap));
		}
		
		return comments;
		
	}

	public int getCommentCntById(int commentId, int id) {
		
		SecSql sql = new SecSql();
		
		sql.append("SELECT COUNT(*)");
		sql.append("FROM `comment`");
		sql.append("WHERE id = ? AND articleId = ?", commentId, id);
		
		return DBUtil.selectRowIntValue(conn, sql);
		
	}

	public Comment getCommentById(int commentId) {
		
		SecSql sql = new SecSql();
		
		sql.append("SELECT *");
		sql.append("FROM `comment`");
		sql.append("WHERE id = ?", commentId);
		
		Map<String, Object> commentMap = DBUtil.selectRow(conn, sql); // List<Article>이 사용하기 편합니다.
		// DB에서 데이터를 받을 때는 List<Map<String, Object>> 형태가 편하고 프로그램에서 사용하기에는 List<Article>
		// 형태가 편합니다.
		
		return new Comment(commentMap);
		
	}

	public void modifyComment(int commentId, String title, String body) {
		
		SecSql sql = new SecSql();
				
		sql.append("UPDATE `comment`");
		sql.append("SET updateDate = NOW()");
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);
		sql.append("WHERE id = ?", commentId);
		// 조회수 기능 구현 후 쿼리 수정
		
		DBUtil.update(conn, sql);
		
	}

	public void deleteComment(int commentId) {
		
		SecSql sql = new SecSql();
		
		sql.append("DELETE FROM `comment`");
		sql.append("WHERE id = ?", commentId);
		// 조회수 기능 구현 후 쿼리 수정
		
		DBUtil.delete(conn, sql);
		
	}

	public List<Comment> getCommentsByPage(int id, int limitFrom, int limitTake) {
		
		SecSql sql = new SecSql();
		
		sql.append("SELECT c.*, m.name AS extra_writer");
		sql.append("FROM `comment` AS c");
		sql.append("INNER JOIN `member` AS m");
		sql.append("ON c.memberId = m.id");
		sql.append("WHERE articleId = ?", id);
		sql.append("LIMIT ?, ?", limitFrom, limitTake);
		
		List<Map<String, Object>> commentListMap = DBUtil.selectRows(conn, sql); // List<Article>이 사용하기 편합니다.
		// DB에서 데이터를 받을 때는 List<Map<String, Object>> 형태가 편하고 프로그램에서 사용하기에는 List<Article>
		// 형태가 편합니다.

		List<Comment> comments = new ArrayList<>(); // 데이터베이스
		
		for (Map<String, Object> commentMap : commentListMap) {
			comments.add(new Comment(commentMap));
		}
		
		return comments;
		
	}

	public int getCommentsCnt(int id) {
		
		SecSql sql = new SecSql();
		
		sql.append("SELECT COUNT(*) FROM `comment`");
		sql.append("WHERE articleId = ?", id);
		
		int count = DBUtil.selectRowIntValue(conn, sql);
		
		return count;
	}

}
