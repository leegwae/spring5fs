# CH7. AOP Programming(수정중)

- AOP(Aspect Oriented Programming)



## 7.1 프로젝트 준비

- `pom.xml`에 `asepectjweaver` 의존을 추가한다.

```
	<dependencies>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context</artifactId>
			<version>5.0.2.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.aspectj</groupId>
			<artifactId>aspectjweaver</artifactId>
			<version>1.8.13</version>
		</dependency>
	</dependencies>
```

- 스프링 프레임워크의 AOP 기능은 `spring-aop` 모듈이 제공한다.
- `spring-context` 모듈을 의존 대상에 추가하면 `spring-aop` 모듈도 의존 대상에 포함된다.
- `aspectjweaver` 모듈: AOP를 설정하는데 필요한 애노테이션을 제공한다.



## 7.2 프록시와 AOP

```java
// MyFactorial.java
public interface MyFactorial{
    public int factorial();
}
```

```java
// IterFactorial.java
public class IterFactorial implements MyFactorial {

	public long factorial(long num) {
		long result = 1;
		
		for (int i = 1; i < num + 1; i++) {
			result *= i;
		}
		
		return result;
	}

}

```

```java
// RecFactorial.java
public class RecFactorial implements MyFactorial {
	@Override
	public long factorial(long num) {
		if (num == 1) return 1;
		
		return num * factorial(num - 1);
	}

}

```

```java
// MyProxy.java
public class MyProxy implements MyFactorial {
    MyFactorial delegate;
    
    public MyProxy(MyFactorial delegate){
        this.delegate = delegate;
    }
    
    @Ovrride
    public long factorial(long num){
        long start = System.nanoTime();
		long result = delegate.factorial(num);
		long end = System.nanoTime();
        
		System.out.printf("%s.factorial(%d) 실행 시간=%d\n",
				delegate.getClass().getSimpleName(),
				num, (end-start));
        
        return result;
    }
}
```

- 프록시(proxy): 핵심 기능의 실행을 다른 객체에 위임하고 부가적인 기능을 제공하는 객체
  - 핵심 기능을 구현하지 않는다.
  - 여러 객체에 공통으로 적용할 수 있는 기능을 구현한다.
- 대상 객체: 핵심 기능을 실행하는 객체

- 여기서는 `MyProxy`가 프록시, `IterFactorial`과 `RecFactorial` 객체가 대상 객체이다.
  - `MyProxy`는 핵심 기능인 `factorial`을 구현하지 않는다. 단, `MyFactorial`을 상속받은 여러 객체가 공통으로 적용할 수 있는 기능을 구현한다.



#### 프록시와 데코레이터

- 프록시(proxy): 접근 제어의 관점
- 데코레이터(decorator): 기능 추가와 확장의 관점
- 따라서 위 예시의 프록시는 데코레이터에 가깝지만 스프링 레퍼런스가 AOP를 설명할 때 프록시라는 용어를 사용하므로 그것을 따르도록 함.



### 7.2.1 AOP

- AOP(Aspect Oriented Programming; 관점 지향 프로그래밍): 여러 객체에 공통으로 적용할 수 있는 기능과 핵심 기능의 구현을 분리하여 재사용성을 높여주는 프로그래밍 기법. <u>핵심 기능에 공통 기능을 삽입한다</u>.
- 핵심 기능에 공통 기능을 삽입하는 방법: (1), (2)는 AspectJ와 같은 AOP 전용 도구를 사용하여 적용할 수 있다. 스프링은 (3)을 이용하여 AOP 방식을 지원한다.
  - (1) 컴파일 시점에 코드에 공통 기능을 삽입하는 방법
  - (2) 클래스 로딩 시점에 바이트 코드에 공통 기능을 클래스에 삽입하는 방법
  - (3) 런타임에 프록시 객체를 생성하여 공통 기능을 삽입하는 방법
- 스프링 AOP는 프록시 객체를 자동으로 만들어주므로, 공통 기능을 구현한 클래스만 구현하면 된다.



#### AOP 주요 용어

| 용어        | 의미                                                         |
| ----------- | ------------------------------------------------------------ |
| `Advice`    | 언제 공통 관심 기능을 핵심 로직에 적용할 지 정의한다.<br />예) 메서드를 호출하기 전에 트랙잭션 시작 기능을 적용한다는 것을 정의한다. |
| `JoinPoint` | `Advice`가 적용 가능한 지점을 의미한다.<br />메서드 호출, 필드 값 변경 등이 이에 속한다.<br />스프링은 프록시를 이용하여 AOP를 구현하므로 메서드 호출에 대한 `JoinPoint`만 지원한다. |
| `Pointcut`  | `JoinPoint`의 부분 집합으로서, 실제 `Advice`가 적용되는 `JoinPoint`를 의미한다.<br />스프링에서는 정규 표현식이나 `AspectJ`의 문법을 이용하여 `Pointcut`을 정의할 수 있다. |
| `Weaving`   | `Advice`를 핵심 로직 코드에 적용하는 것을 의미한다.          |
| `Aspect`    | 여러 객체에 공통으로 적용되는 기능을 의미한다.<br />예) 트랜잭션, 보안 |



### 2.2 Advice의 종류

- 스프링은 프록시를 이용하여 메서드 호출 시점에 `Aspect`를 적용한다.



#### 스프링에서 구현 가능한 Advice 종류

| 종류                     | 공통 기능을 실행 시점하는 시점                        |
| ------------------------ | ----------------------------------------------------- |
| `Before Advice`          | 대상 객체의 메서드 호출 전                            |
| `After Returning Advice` | 대상 객체의 메서드가 예외 없이 실행된 이후            |
| `After Thrwoing Advice`  | 대상 객체의 메서드를 실행하는 도중 예외가 발생한 경우 |
| `After Advice`           | 예외 발생 여부에 관련 없이 대상 객체의 메서드 실행 후 |
| `Around Advice`          | 대상 객체의 메서드 실행 전후 혹은 예외 발생 시점      |

- 다양한 시점에 원하는 기능을 삽입할 수 있으므로 `Around Advice`를 널리 사용한다.
- `Around Advice`는 캐시, 성능 모니터링 기능과 같은 `Aspect`를 구현할 때 주로 이용한다.



## 7.3 스프링 AOP 구현

- 스프링 AOP를 이용하여 공통 기능을 구현하고 적용하는 방법
  - (1) `Aspect`로 사용할 클래스에 `@Aspect` 애노테이션 붙이기
  - (2) `@Pointcut` 애노테이션으로 공통 기능을 적용할 `Pointcut`을 정의하기
  - (3) 공통 기능을 구현한 메서드에 `@Around` 애노테이션 적용하기



### 7.3.1 @Aspect, @Pointcut, @Around를 이용한 AOP 구현

- `@Aspect`: 해당 클래스를 `Asepct`를 구현한 클래스로 지정한다.
- `@Pointcut`: 공통 기능을 적용할 대상을 설정한다.
- `@Around`: `Around Advice`를 설정한다.

 

?? 무슨 소린지 도통

```java
// factorial\Calculator.java

public interface Calculator {
    public long factorial(long num);
}
```

```java
// factorial\RecCalculator.java

public class RecCalculator implements Calculator {
    @Override
    public long factorial(long num){
        if (num == 1) return 1;
        return num * factorial(num - 1);
    }
}
```

```java
// aspect\MyAspect.java

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MyAspect {
    @Pointcut("execution(public * factorial..*(..))")
    private void publicTarget(){
        
    }
    
    @Around("publicTarget()")
    public Object measure(ProceedingJoinPoint joinPoint) thrwos Throwable {
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
```

- `@Pointcut("execution(public * factorial..*(..))")`
  `private void publicTarget(){/**/}`
  - `publicTarget()` 메서드의 `@Pointcut`은 `factorial` 패키지와 그 하위 패키지에 위치한 타입의 `public` 메서드를 `Pointcut`으로 설정
- `@Around("publicTarget()")`
  - `Pointcut`으로 `publicTarget()` 메서드를 설정

```java
// config\AppContext.java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import aspect.MyAspect;
import factorial.Calculator;
import factorial.RecCalculator;

@Configuration
@EnableAspectJAutoProxy
public class AppContext {
    @Bean
    public MyAspect myAspect(){
        return new MyAsepct();
    }
    
    @Bean
    public Calculator calculator(){
        return new Calculator();
    }
}
```

- `@EnableAspectJAutoProxy`: `@Aspect` 애노테이션이 붙인 빈 객체를 찾아 빈 객체의 `@Pointcut` 설정과 `@Around` 설정을 사용한다.
- `Calculator` 타입은 `factorial` 패키지에 속하므로 `calculator` 빈에 `MyAsepct` 클래스에 정의한 공통 기능인 `measure()`를 적용한다.

```java
// main\MainAspect.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import factorial.Calculator;
import config.AppContext;

public class MainAspect {
    public static void main(String[] args){
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        
        Calculator cal = ctx.getBean("calculator", Calculator.class);
        long fiveFact = cal.factorial(5);
        System.out.println("cal.factorial(5) = " + fivefact);
        System.out.println(cal.getClass().getName());
        ctx.close();
    }
}
```

```
RecCalculator.factorial([5]) 실행 시간: 72100 ns
cal.factorial(5) = 120
com.sun.proxy.$Proxy19
```

- Line 12 `long fiveFact = cal.factorial(5);`
  - `MyAspect.measure()` 클래스 실행됨.
- `com.sun.proxy.$Proxy19`: `Calculator`의 타입이 `RecCalculator` 클래스가 아닌 `$Proxy17`로 출력. 즉 스프링이 생성한 프록시 타입이 `cal.getClass`에서 반환됨.

