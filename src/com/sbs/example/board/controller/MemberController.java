package com.sbs.example.board.controller;

import java.sql.Connection;
import java.util.Scanner;

import com.sbs.example.board.dto.Member;
import com.sbs.example.board.service.MemberService;
import com.sbs.example.board.session.Session;

public class MemberController {
	
	private MemberService memberService;
	
	Scanner scanner;
	String cmd;
	Session session;

	public MemberController(Connection conn, Scanner scanner, String cmd, Session session) {
		this.scanner = scanner;
		this.cmd = cmd;
		this.session = session;
		
		memberService = new MemberService(conn);
	}

	public void doJoin() {
		
		String loginId;
		String loginPw;
		String loginPwConfirm;
		String name;
		
		System.out.println("== 회원가입 ==");

		while (true) {
			System.out.printf("로그인 아이디: ");
			loginId = scanner.nextLine();

			if (loginId.length() == 0) {
				System.out.println("아이디를 입력해주세요.");
				continue;
			}
			
			int memberCnt = memberService.getMemberCntByLoginId(loginId);
			
			if(memberCnt > 0) {
				System.out.println("이미 존재하는 아이디입니다.");
				continue;
			}

			break;
		}

		while (true) {
			System.out.printf("로그인 비밀번호: ");
			loginPw = scanner.nextLine();

			if (loginPw.length() == 0) {
				System.out.println("비밀번호를 입력해주세요.");
				continue;
			}

			while (true) {
				System.out.printf("로그인 비밀번호 확인: ");
				loginPwConfirm = scanner.nextLine();

				if (loginPwConfirm.length() == 0) {
					System.out.println("비밀번호 확인을 입력해주세요.");
					continue;
				}

				break;
			}
			
			if (!loginPw.equals(loginPwConfirm)) {
				System.out.println("입력된 비밀번호가 일치하지 않습니다.");
				continue;
			}

			break;
		}
		
		while (true) {
			System.out.printf("이름: ");
			name = scanner.nextLine();

			if (name.length() == 0) {
				System.out.println("이름을 입력해주세요.");
				continue;
			}
			
			break;
		}
		
		memberService.doJoin(loginId, loginPw, name);
					
		System.out.printf("%s님 환영합니다. \n", name);
		
	}

	public void doLogin() {
		
		if (session.loginedMemberId != -1) {
			System.out.println("로그아웃 후 이용해주세요.");
			return;
		}
		
		String loginId;
		String loginPw;
		
		System.out.println("== 로그인 ==");
		
		int loginTry = 0;

		while (true) {
			if(loginTry >= 3) {
				System.out.println("로그인을 다시 시도해주세요.");
				return;
			}
			
			System.out.printf("로그인 아이디: ");
			loginId = scanner.nextLine();

			if (loginId.length() == 0) {
				System.out.println("아이디를 입력해주세요.");
				loginTry++;
				continue;
			}
			
			int memberCnt = memberService.getMemberCntByLoginId(loginId);
			
			if(memberCnt == 0) {
				System.out.println("아이디가 존재하지 않습니다.");
				loginTry++;
				continue;
			}

			break;
		}
		
		loginTry = 0;
		
		Member member;

		while (true) {
			if(loginTry >= 3) {
				System.out.println("로그인을 다시 시도해주세요.");
				return;
			}
			
			System.out.printf("로그인 비밀번호: ");
			loginPw = scanner.nextLine();

			if (loginPw.length() == 0) {
				System.out.println("비밀번호를 입력해주세요.");
				loginTry++;
				continue;
			}
			
			member = memberService.getArticleByLoginId(loginId);

			if(!member.loginPw.equals(loginPw)) {
				System.out.println("비밀번호가 일치하지 않습니다.");
				loginTry++;
				continue;
			}
			
			break;
		}
					
		System.out.printf("%s님 환영합니다. \n", member.name);
		
		// 로그인 처리
		session.loginedMemberId = member.id;
		session.loginedMember = member;
		
	}

	public void doLogout() {
		
		if (session.loginedMember == null) {
			System.out.println("로그인 후 이용해주세요.");
			return;
		}
		
		session.loginedMemberId = -1;
		session.loginedMember = null;

		System.out.println("로그아웃 완료");
		
	}

	public void whoami() {
		
		if (session.loginedMember == null) {
			System.out.println("로그인 유저가 없습니다.");
			return;
		}
		System.out.printf("현재 로그인 유저: %s\n", session.loginedMember.name);
		
	}
	
	

}
