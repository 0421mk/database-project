package com.sbs.example.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		List<Article> articles = new ArrayList<>();
		int lastArticleId = 0;

		while (true) {
			System.out.printf("명령어) ");
			String cmd = scanner.nextLine();
			cmd = cmd.trim();

			if (cmd.equals("article write")) {
				int id = lastArticleId + 1;
				String title;
				String body;

				System.out.println("== 게시글 작성 ==");
				System.out.printf("제목: ");
				title = scanner.nextLine();
				System.out.printf("내용: ");
				body = scanner.nextLine();
				
				Article article = new Article(id, title, body);
				articles.add(article);
				
				lastArticleId++;

			} else if (cmd.equals("system exit")) {
				System.out.println("프로그램을 종료합니다.");
				break;
			} else {
				System.out.println("잘못된 명령어입니다.");
			}
		}

	}

}
