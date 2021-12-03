package com.sbs.example.board.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SecSql {
	private StringBuilder sqlBuilder;
	private List<Object> datas;

	@Override
	public String toString() {
		return "sql=" + getFormat() + ", data=" + datas;
	}

	public SecSql() {
		sqlBuilder = new StringBuilder();
		datas = new ArrayList<>();
	}

	public boolean isInsert() {
		return getFormat().startsWith("INSERT");
	}

	public SecSql append(Object... args) {
		if (args.length > 0) {
			String sqlBit = (String) args[0];
			sqlBuilder.append(sqlBit + " "); // 공백 포함해서 sqlBuilder에 append
		}

		for (int i = 1; i < args.length; i++) {
			datas.add(args[i]); // ?로 치환할 데이터의 List
		}

		return this;
	}

	public PreparedStatement getPreparedStatement(Connection dbConn) throws SQLException { // SQLException 포함
		PreparedStatement stmt = null;

		if (isInsert()) {
			stmt = dbConn.prepareStatement(getFormat(), Statement.RETURN_GENERATED_KEYS);
		} else {
			stmt = dbConn.prepareStatement(getFormat());
		}

		for (int i = 0; i < datas.size(); i++) {
			Object data = datas.get(i);
			int parameterIndex = i + 1;

			if (data instanceof Integer) {
				stmt.setInt(parameterIndex, (int) data); // ?에 int 데이터 치환
			} else if (data instanceof String) {
				stmt.setString(parameterIndex, (String) data); // ?에 String 데이터 치환
			}
		}

		return stmt;
	}

	public String getFormat() {
		return sqlBuilder.toString();
	}

	public static SecSql from(String sql) {
		return new SecSql().append(sql);
	}
}