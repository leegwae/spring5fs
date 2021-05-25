package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import aspect.ExecuteTimeAspect;
import ch07.Calculator;
import ch07.IterCalculator;
import ch07.RecCalculator;


@Configuration
@EnableAspectJAutoProxy // @Aspect 애노테이션이 붙인 빈 객체를 찾아 빈 객체의 @Pointcut 설정과 @Around 설정을 사용
public class AppContext {

	@Bean
	public ExecuteTimeAspect excuteTimeAsepct() {
		return new ExecuteTimeAspect();
	}
	
	@Bean
	public Calculator calculator() {
		return new RecCalculator();
	}

}
