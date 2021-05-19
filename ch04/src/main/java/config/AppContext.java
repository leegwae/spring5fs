package config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import spring.MemberManager;
import spring.MemberRegisterService;
import spring.ChangePasswordService;
import spring.MemberPrinter;
import spring.MemberListPrinter;
import spring.MemberPrinterWithProperty;

@Configuration
public class AppContext {
	@Bean
	public MemberManager memberManager() {
		return new MemberManager();
	}
	
	@Bean
	public MemberRegisterService memberRegisterService() {
		return new MemberRegisterService();
	}
	
	@Bean
	public ChangePasswordService changePasswordService() {
		return new ChangePasswordService();
	}
	
	@Bean
	public MemberPrinter memberPrinter() {
		return new MemberPrinter();
	}
	
	@Bean
	public MemberListPrinter listPrinter() {
		return new MemberListPrinter();
	}
	
	@Bean
	public MemberPrinterWithProperty propertyPrinter() {
		return new MemberPrinterWithProperty();
	}
}
