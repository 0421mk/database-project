package com.sbs.example.board.service;

import java.sql.Connection;
import java.util.List;

import com.sbs.example.board.dao.ArticleDao;
import com.sbs.example.board.dto.Article;

public class ArticleService {
	private ArticleDao articleDao;

	public ArticleService(Connection conn) {
		articleDao = new ArticleDao(conn);
	}

	public int doWrite(String title, String body) {
		
		return articleDao.doWrite(title, body);
		
	}

	public int getArticleCntById(int id) {
		
		return articleDao.getArticleCntById(id);
		
	}

	public void doModify(String title, String body, int id) {
		
		articleDao.doModify(title, body, id);
		
	}

	public List<Article> getArticles() {

		return articleDao.getArticles();
		
	}

	public Article getArticle(int id) {

		return articleDao.getArticle(id);
		
	}

	public void doDelete(int id) {
		
		articleDao.doDelete(id);
		
	}

}
