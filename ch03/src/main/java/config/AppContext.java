package config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import spring.MemberManager;
import spring.MemberRegisterService;
import spring.ChangePasswordService;
import spring.MemberPrinter;
import spring.MemberListPrinter;

@Configuration
public class AppContext {
	@Bean
	public MemberManager memberManager() {
		return new MemberManager();
	}
	
	@Bean
	public MemberRegisterService memberRegisterService() {
		return new MemberRegisterService(memberManager());
	}
	
	@Bean
	public ChangePasswordService changePasswordService() {
		ChangePasswordService changePwSvc = new ChangePasswordService();
		changePwSvc.setMemberManager(memberManager());

		return changePwSvc;
	}
	
	@Bean
	public MemberPrinter memberPrinter() {
		return new MemberPrinter();
	}
	@Bean
	public MemberListPrinter listPrinter() {
		return new MemberListPrinter(memberManager(), memberPrinter());
	}
	
}
