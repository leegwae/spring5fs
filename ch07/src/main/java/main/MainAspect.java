package main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ch07.Calculator;
import config.AppContext;

public class MainAspect {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx =
				new AnnotationConfigApplicationContext(AppContext.class);
		
		Calculator cal = ctx.getBean("calculator", Calculator.class);
		long fiveFact = cal.factorial(5);
		System.out.println("cal.factorial(5) = "+ fiveFact);
		System.out.println(cal.getClass().getName());
		ctx.close();
	}

}
