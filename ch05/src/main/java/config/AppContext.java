package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;

import spring.MemberManager;
import spring.MemberRegisterService;
import spring.ChangePasswordService;
import spring.MemberPrinter;
import spring.MemberListPrinter;
import spring.MemberPrinterWithProperty;

@Configuration
@ComponentScan(basePackages={"spring"},
excludeFilters=@Filter(type=FilterType.REGEX, pattern="spring\\..*Manager"))
public class AppContext {

	@Bean
	public MemberPrinter memberPrinter() {
		return new MemberPrinter();
	}
}
