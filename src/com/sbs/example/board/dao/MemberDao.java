package com.sbs.example.board.dao;

import java.sql.Connection;
import java.util.Map;

import com.sbs.example.board.dto.Member;
import com.sbs.example.board.util.DBUtil;
import com.sbs.example.board.util.SecSql;

public class MemberDao {
	Connection conn;

	public MemberDao(Connection conn) {
		this.conn = conn;
	}

	public int getMemberCntByLoginId(String loginId) {
		SecSql sql = new SecSql();

		sql.append("SELECT COUNT(*)");
		sql.append("FROM member");
		sql.append("WHERE loginId = ?", loginId);

		return DBUtil.selectRowIntValue(conn, sql);
	}

	public void doJoin(String loginId, String loginPw, String name) {

		SecSql sql = new SecSql(); // SecSql 객체 초기화
		sql.append("INSERT INTO member");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", loginId = ?", loginId);
		sql.append(", loginPw = ?", loginPw);
		sql.append(", name = ?", name);

		DBUtil.insert(conn, sql);

	}

	public Member getArticleByLoginId(String loginId) {
		SecSql sql = new SecSql(); // SecSql 객체 초기화

		sql.append("SELECT * FROM member");
		sql.append("WHERE loginId = ?", loginId);

		Map<String, Object> memberMap = DBUtil.selectRow(conn, sql);
		Member member = new Member(memberMap);
		
		return member;
	}
}
