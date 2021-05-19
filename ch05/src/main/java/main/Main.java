package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.AppContext;

import spring.ChangePasswordService;
import spring.DuplicateMemberException;
import spring.MemberNotFoundException;
import spring.MemberRegisterService;
import spring.RegisterRequest;
import spring.WrongIdPasswordException;
import spring.MemberListPrinter;
import spring.MemberPrinterWithProperty;

public class Main {

	private static ApplicationContext ctx;
	
	public static void main(String[] args) throws IOException {
		ctx = new AnnotationConfigApplicationContext(AppContext.class);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String command;

		while (true) {
			System.out.println("명령어를 입력하세요:");
			command = reader.readLine();
			
			if (command.equalsIgnoreCase("exit")) {
				System.out.println("시스템을 종료합니다.");
				break;
			}
			
			if (command.startsWith("new ")) {
				processNewCommand(command.split(" "));
			} else if (command.startsWith("change ")) {
				processChangeCommand(command.split(" "));
				continue;
			} else if (command.startsWith("list")) {
				processListCommand();
			} else if (command.startsWith("property ")) {
				processPropertyCommand(command.split(" "));
			} else {
				printHelp();
			}

		}
	}
	
	public static void processNewCommand(String[] arg) {
		if (arg.length != 5) {
			printHelp();
			return;
		}
		
		MemberRegisterService regSvc = 
				ctx.getBean("memberRegisterService", MemberRegisterService.class);
		RegisterRequest req = new RegisterRequest();
		req.setEmail(arg[1]);
		req.setName(arg[2]);
		req.setPassword(arg[3]);
		req.setConfirmPassword(arg[4]);
		
		if (!req.isPasswordEqualToConfirmPassword()) {
			System.out.println("암호와 확인이 일치하지 않습니다.");
			return;
		}
		
		try {
			regSvc.regist(req);
			System.out.println("등록 성공!");
		} catch (DuplicateMemberException e){
			System.out.println("이미 존재하는 이메일입니다.");
		}
		
	}
	
	private static void processChangeCommand(String[] arg) {
		if (arg.length != 4) {
			printHelp();
			return;
		}
	
		ChangePasswordService changePwSvc = 
				ctx.getBean("changePasswordService", ChangePasswordService.class);
		
		try {
			changePwSvc.changePassword(arg[1], arg[2], arg[3]);
			System.out.println("암호 변경 성공!");
		} catch (MemberNotFoundException e) {
			System.out.println("존재하지 않는 이메일입니다.");
		} catch (WrongIdPasswordException e) {
			System.out.println("이메일과 암호가 일치하지 않습니다.");
		}
	}
	
	private static void processListCommand() {
		MemberListPrinter printer = ctx.getBean("listPrinter", MemberListPrinter.class);
		
		printer.printAll();
	}
	
	private static void processPropertyCommand(String args[]) {
		if (args.length != 2) {
			printHelp();
			return;
		}
		
		String email = args[1];
		MemberPrinterWithProperty printer = ctx.getBean("propertyPrinter", MemberPrinterWithProperty.class);
		printer.printByEmail(email);
	}

	private static void printHelp() {
		System.out.println();
		System.out.println("잘못된 명령입니다. 아래 명령어 사용법을 확인하세요.");
		System.out.println("명령어 사용법");
		System.out.println("new 이메일 이름 암호 암호확인");
		System.out.println("change 이메일 현재비번 변경비번");
		System.out.println("property 이메일");
		System.out.println();
	}

}
