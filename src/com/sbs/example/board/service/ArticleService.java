package com.sbs.example.board.service;

import java.sql.Connection;

import com.sbs.example.board.dao.ArticleDao;
import com.sbs.example.board.util.DBUtil;
import com.sbs.example.board.util.SecSql;

public class ArticleService {
	private ArticleDao articleDao;

	public ArticleService(Connection conn) {
		articleDao = new ArticleDao(conn);
	}

	public int doWrite(String title, String body) {
		
		return articleDao.doWrite(title, body);
		
	}

}
