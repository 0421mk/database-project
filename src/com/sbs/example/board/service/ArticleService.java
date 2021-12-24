package com.sbs.example.board.service;

import java.sql.Connection;
import java.util.List;

import com.sbs.example.board.dao.ArticleDao;
import com.sbs.example.board.dto.Article;
import com.sbs.example.board.dto.Comment;

public class ArticleService {
	private ArticleDao articleDao;

	public ArticleService(Connection conn) {
		articleDao = new ArticleDao(conn);
	}

	public int doWrite(String title, String body, int loginedMemberId) {
		
		return articleDao.doWrite(title, body, loginedMemberId);
		
	}

	public int getArticleCntById(int id) {
		
		return articleDao.getArticleCntById(id);
		
	}

	public void doModify(String title, String body, int id) {
		
		articleDao.doModify(title, body, id);
		
	}

	public List<Article> getArticles(int page, int itemsInAPage) {
		
		int limitFrom = (page - 1) * itemsInAPage;
		int limitTake = itemsInAPage;
		
		return articleDao.getArticles(limitFrom, limitTake);
		
	}

	public Article getArticle(int id) {

		return articleDao.getArticle(id);
		
	}

	public void doDelete(int id) {
		
		articleDao.doDelete(id);
		
	}

	public List<Article> getArticlesByKeyword(int page, int itemsInAPage, String searchKeyword) {
		
		int limitFrom = (page - 1) * itemsInAPage;
		int limitTake = itemsInAPage;
		
		return articleDao.getArticlesByKeyword(limitFrom, limitTake, searchKeyword);
		
	}

	public void increaseHit(int id) {
		
		articleDao.increaseHit(id);
		
	}

	public int getArticlesCnt(String searchKeyword) {
		return articleDao.getArticleCnt(searchKeyword);
	}

	public void insertLike(int id, int likeType, int loginedMemberId) {
		
		articleDao.insertLike(id, likeType, loginedMemberId);
		
	}

	public int likeCheck(int id, int loginedMemberId) {
		
		return articleDao.likeCheck(id, loginedMemberId);
		
	}

	public void modifyLike(int id, int likeType, int loginedMemberId) {
		
		articleDao.modifyLike(id, likeType, loginedMemberId);
		
	}

	public int getLikeVal(int id, int likeType) {
		return articleDao.getLikeVal(id, likeType);
	}

	public void deleteLike(int id, int loginedMemberId) {
		
		articleDao.deleteLike(id, loginedMemberId);
		
	}

	public int wirteComment(int id, String title, String body, int loginedMemberId) {
		
		return articleDao.writeComment(id, title, body, loginedMemberId);
	}

	public List<Comment> getCommentsById(int id) {
		
		return articleDao.getCommentsById(id);
		
	}

	public int getCommentCntById(int commentId, int id) {
		
		return articleDao.getCommentCntById(commentId, id);
		
	}

	public Comment getCommentById(int commentId) {
		// TODO Auto-generated method stub
		return articleDao.getCommentById(commentId);
	}

	public void modifyComment(int commentId, String title, String body) {
		
		articleDao.modifyComment(commentId, title, body);
		
	}

	public void deleteComment(int commentId) {
		
		articleDao.deleteComment(commentId);
		
	}

	public List<Comment> getCommentsByPage(int id, int page, int itemsInAPage) {

		int limitFrom = (page - 1) * itemsInAPage;
		int limitTake = itemsInAPage;
		
		return articleDao.getCommentsByPage(id, limitFrom, limitTake);
	}

	public int getCommentsCnt(int id) {
		return articleDao.getCommentsCnt(id);
	}

}
