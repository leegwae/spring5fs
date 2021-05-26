package aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect // 해당 클래스를 Aspect를 구현한 클래스로 지정
public class ExecuteTimeAspect {
	
	// Advice: 언제 공통 관심 기능을 핵심 로직에 적용할지 정의
	// Pointcut: 실제 Advice가 적용되는 지점
	// @Pointcut: 공통 기능을 적용할 대상을 설정한다.
	// @Pointcut: ch07 패키지와 그 하위 패키지에 위치한 타입의 public 메서드를 Pointcut으로 설정한다.
	@Pointcut("execution(public * ch07..*(..))")
	private void publicTarget() {

	}
	
	// Around: Advice를 Around Advice로 설정한다.
	// @Around: publicTarget() 메서드에 정의한 Pointcut에 공통 기능을 적용한다.
	// ch07 패키지와 그 하위 패키지에 위치한 타입의 빈 객체의 public 메서드에 @Around가 붙은 메서드를 적용한다.
	// ProceddingJointPoint 파라미터: 프록시 대상 객체의 메서드를 호출할 때 사용(28행)
	@Around("publicTarget()")
	public Object measure(ProceedingJoinPoint joinPoint) throws Throwable{
		long start = System.nanoTime();
		
		try {
			Object result = joinPoint.proceed();
			return result;
		} finally {
			long finish = System.nanoTime();
			Signature sig = joinPoint.getSignature();
			System.out.printf("%s.%s(%s) 실행 시간: %d ns\n",
					joinPoint.getTarget().getClass().getSimpleName(),
					sig.getName(),
					Arrays.toString(joinPoint.getArgs()),
					(finish - start));
		}
	}

}
