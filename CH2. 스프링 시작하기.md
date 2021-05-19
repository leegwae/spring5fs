# CH2. 스프링 시작하기

## 2.1 스프링 프로젝트 시작하기

- 프로젝트 폴더 생성

```
ch02 : 프로젝트 폴더
	ㄴ src
		ㄴ main
			ㄴ java : 자바 소스 폴더
```

- 메이븐 프로젝트 생성
  - (1) `프로젝트 폴더`에 `pom.xml` 작성
  - (2) `프로젝트 폴더`에서 `mvn compile`
  - (3) 이클립스 > File > Import > Maven > Existing Maven Projects > Next > 프로젝트 폴더



### 예제 프로그램

```java
// Greeter.java : 콘솔에 간단한 메시지 출력하기
public class Greeter{
	private String format;
    public String greet(String guest){
        return String.format(format, guest);
    }
    
    public void setFormat(String format){
        this.format = foramt;
    }
}
```

```java
// AppContext.java : 스프링 설정 파일
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppContext{
    @Bean
    public Greeter greeter(){
        Greeter g = new Greeter();
        g.setFormat("%s, 안녕하세요!");
        
        return g;
    }
}
```

- `@Configuration` 애노테이션: 해당 클래스를 스프링 설정 클래스로 지정
- 스프링: 객체을 생성하고 초기화하는 기능을 제공
- Bean 객체: 스프링이 생성하는 객체
- `@Bean` 애노테이션: 해당 메서드가 생성한 객체를 스프링이 관리하는 bean 객체로 등록
  - `greeter()` 메서드: bean 객체에 대한 정보를 담고 있다.
- 메서드의 이름으로 Bean 객체를 구분



```java
// Main.java : main 메서드를 통해 스프링과 Greeter를 실행함
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main{
    public static void main(String[] args){
        AnnotationConfigApplicationContext ctx = new AnotationConfigApplicationContext(AppContext.class);
    	Greeter g = ctx.getBean("greeter", Greeter.class);
        // greeter() 메서드의 리턴 타입이 Greeter이므로,
        // getBean() 메서드는 위 메서드가 생성한 Greeter 객체를 리턴한다.
        String msg = g.greet("스프링");
        System.out.println(msg);
        ctx.close();
    }
}
```

- `AnnotationConfigApplicationContext` 클래스: 자바 설정에서 정보를 읽어와 Bean 객체를 생성하고 관리



```java
// Main.java 6행
AnnotationConfigApplicationContext ctx = new AnotationConfigApplicationContext(AppContext.class);
```

- 파라미터로 `AppContext` 클래스를 전달. 
- `AppContext`에서 정의한 `@Bean` 설정 정보를 읽어와 `Greeter` 객체를 생성하고 초기화.



```java
// Main.java 7행
Greeter g = ctx.getBean("greeter", Greeter.class);
```

- `getBean()` 메서드: `AnnotationConfigApplicationContext`가 자바 설정을 읽어와 생성한 Bean 객체를 검색할 때 사용.
  - 첫번째 파라미터: 검색할 Bean 객체의 이름(메서드 이름).
  - 두번째 파라미터: 검색할 Bean 객체의 타입
  - 반환값: 생성된 Bean 객체



```
Greeter.java		// 빈 객체로 생성할 클래스 정의
AppContext.java		// 스프링 설정 클래스
Main.java			// 스프링 설정 클래스의 정보를 읽어와 빈 객체를 생성하고 관리
```





## 2.2 스프링은 객체 컨테이너

- 스프링의 핵심 기능은 객체를 생성하고 초기화하는 것이다.
- `ApplicationContext` 인터페이스: 객체의 생성과 초기화 기능을 정의
- `AnnotationConfigApplicationContext` 클래스
  - `ApplicationContext` 인터페이스를 구현
  - 자바 클래스에서 정보를 읽어와 객체 생성과 초기화를 수행한다.



### 메이븐의 의존 그래프

*기울임*은 인터페이스. ...는 생략

*BeanFactory / ... / ApplicationContext / ConfigurableApplicationConext* / ..... / AnnotationConfigApplicationContext, ...



- 의존 그래프 상에서, `BeanFactory`가 가장 상단에, 하단에 `AnnotationConfigApplicationContext`가 있다.
- `BeanFactory` 인터페이스: 객체 생성과 검색에 대한 기능을 정의한다.
  - `getBean()` 메서드 정의
- `ApplicationContext` 인터페이스: 메시지, 프로필/환경 변수 등을 처리할 수 있는 기능을 추가로 정의
- 가장 하단의 세 클래스: `BeanFactory` 인터페이스와 `ApplicationContext` 인터페이스에 정의된 기능을 구현한 구현 클래스
  - `AnnotationConfigApplicationContext`: 자바 annotation을 이용하여 클래스로부터 객체 설정 정보를 가져온다.
  - `GenericXmlApplicationContext`: XML로부터 객체 설정 정보를 가져온다.
  - `GenericGroovyApplicationContext`: 그루비 코드를 이용해 설정 정보를 가져온다.
- 위 구현 클래스는
  - 설정 정보로부터 bean 객체를 생성하고 해당 객체를 내부에 보관한다.
  - `getBean()` 메서드: 생성한 bean 객체를 반환한다.
- `ApplicationContext` 혹은 `BeanFactory`는 <u>빈 객체의 생성, 초기화, 보관, 제거 등을 관리하므로 (스프링) 컨테이너(container)</u>라고도 부른다.



**스프링 컨테이너의 빈 객체 관리**

- 스프링 컨테이너는 내부적으로 bean 객체와 bean 이름을 연결짓는 정보를 갖는다.



### 2.2.1 싱글톤(Singleton) 객체

```java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main{
    public static void main(String[] args){
        AnnotationConfigApplicationContext ctx = new AnotationConfigApplicationContext(AppContext.class);
    	Greeter g1 = ctx.getBean("greeter", Greeter.class);
        Greeter g2 = ctx.getBean("greeter", Greeter.class);
        System.out.println("(g1 == g2) = " + (g1 == g2));
        ctx.close();
    }
}
```

```
true
```

- 별도의 설정을 하지 않는 경우, 스프링은 한 개의 `@Bean` annotation에 대하여 한 개의 bean 객체만을 생성한다.
- 이 객체는 "싱글톤(singleton) 범위를 갖는다"고 표현한다.
- **싱글톤**은 단일 객체(single object)를 의미하는 단어이다.



#### 동일한 타입의 bean 객체 여러 개 만들기

```java
@Bean
public Greeter greeter1(){
    Greeter g = new Greeter();
    g.setFormat("%s, 안녕하세요!");
        
    return g;
}

@Bean
public Greeter greeter2(){
    Greeter g = new Greeter();
    g.setFormat("%s, 안녕하세요!");
        
    return g;
}
```

- 원하는 타입의 객체를 리턴하는 메서드를 여러 개 정의하면 된다.

