# CH6. Bean Lifecycle

## 6.1 컨테이너 초기호와 종료

- 스프링 컨테이너는 초기화와 종료라는 라이프사이클을 갖는다.

  - 컨테이너 초기화

    ```java
    private static ApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class);
    ```

  - 컨테이너 사용: 컨테이너에 보관된 빈 객체를 구하는 것

    ```java
    ctx.getBean("myBean", MyBean.class);
    ```

  - 컨테이너 종료

    ```java
    ctx.close();
    ```



- 컨테이너를 초기화하고 종료할 때 다음의 작업도 수행된다.

| 라이프사이클    | 함께 수행하는 작업                        |
| --------------- | ----------------------------------------- |
| 컨테이너 초기화 | 빈 객체의 생성<br />의존 주입<br />초기화 |
| 컨테이너 종료   | 빈 객체의 소멸                            |



## 6.2 스프링 빈 객체의 라이프사이클

- 스프링 컨테이너는 빈 객체의 라이프사이클을 관리한다.
- 빈 객체의 라이프사이클
  - (1) 객체 생성
  - (2) 의존 설정: 의존 자동 주입을 통한 의존 설정
  - (3) 초기화: 빈 객체의 지정된 메서드 호출
  - (4) 소멸: 빈 객체의 지정된 메서드 호출



### 6.2.1 빈 객체의 초기화와 소멸: 스프링 인터페이스

- 스프링 컨테이너는 다음의 두 인터페이스에 정의된 빈 객체의 지정한 메서드를 호출하여 빈 객체를 초기화하거나 소멸시킨다.
  - `org.springframework.beans.factory.InitializingBean`
  - `org.springframework.beans.factory.DisposableBean`
- 데이터베이스 커넥션 풀, 채팅 클라이언트 등에서 초기화, 소멸 과정이 필요하다.



#### InitializingBean 인터페이스

```java
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
```

- `InitializingBean` 인터페이스를 구현한 빈 객체에 대해, 스프링 컨테이너는 초기화 과정에서 빈 객체의 `afterPropertiesSet()` 메서드를 실행한다.



#### DisposableBean 인터페이스

```java
public interface DisposableBean {
    void destroy() throws Exception;
}
```

- `DisposableBean` 인터페이스를 구현한 빈 객체에 대해, 스프링 컨테이너는 소멸 과정에서 빈 객체의 `destroy()` 메서드를 실행한다.



```java
// Client.java
package spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.DisposableBean;

public class Client implements InitializingBean, DisposableBean{
	
	public String host;
	
	public void setHost(String host) {
		this.host = host;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception{
		System.out.println("afterPropertiesSet() 메서드 실행");
	}
	
	public void send() {
		System.out.println("send() to " + host);
	}
	
	@Override
	public void destroy() throws Exception {
		System.out.println("destroy() 메서드 실행");
	}
}

```

```java
// AppContext.java
package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import spring.Client;

@Configuration
public class AppContext {
	@Bean
	public Client client() {
		Client client = new Client();
		client.setHost("host");
		return client;
	}
	
}

```

```java
// Main.java
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.AppContext;
import spring.Client;

public class Main {

	private static AbstractApplicationContext ctx;
	
	public static void main(String[] args) throws IOException {
		ctx = new AnnotationConfigApplicationContext(AppContext.class);
		
		Client client = ctx.getBean(Client.class);
		client.send();
		
		ctx.close();
	}
}

```

```
afterPropertiesSet() 메서드 실행
send() to host
5월 20, 2021 8:25:35 오전 org.springframework.context.support.AbstractApplicationContext doClose
INFO: Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@73f792cf: startup date [Thu May 20 08:25:34 KST 2021]; root of context hierarchy
destroy() 메서드 실행
```



### 6.2.2 빈 객체의 초기화와 소멸 : 커스텀 메서드

```java
@Bean(initMethod="초기화메서드", destroy="소멸메서드")
```

- `InitializingBean`, `DisposableBean` 인터페이스를 구현할 수 없거나 사용하지 않는 경우, `@Bean` 애노테이션의 `initMethod` 속성과 `destroyMethod` 속성에 각각 초기화 메서드와 소멸 메서드를 지정한다.
  - `초기화메서드`와 `소멸메서드`에는 파라미터가 없어야 한다.



```java
public class MyBean {
    public void printInit() {
		System.out.println("Init");
    }
    
    public void printDestroy() {
        System.out.println("Destroy");
    }
}
```

```java
@Bean(initMethod="printInit", destroy="printDestroy")
public MyBean myBean() {
    return new MyBean();
}
```



## 6.3 빈 객체의 생성과 관리 범위

- 빈 객체는 디폴트로 싱글톤(singleton) 범위를 갖는다.

```java
MyBean b1 = ctx.getBean("myBean", MyBean.class);
MyBean b2 = ctx.getBean("myBean", MyBean.class);
System.out.println(b1 == b2);	// true
```

- 빈 객체를 프로토타입으로 지정하면 `getBean()`를 할 때마다 새로운 객체를 생성한다.
  - 프로토타입 범위를 갖는 빈은 완전한 라이프사이클을 따르지 않는다. 스프링 컨테이너가 종료될 때 프로토타입 빈 객체의 소멸 메서드를 실행하지 않기 때문이다.

```java
MyBean b1 = ctx.getBean("myBean", MyBean.class);
MyBean b2 = ctx.getBean("myBean", MyBean.class);
System.out.println(b1 == b2);	// false
```



## 빈 객체의 범위 지정하기

- `@Scope("prototype")`:  빈 객체의 범위를 프로토타입 범위로 지정한다.

```java
@Bean
@Scope("prototype")
public MyBean myBean(){
    return new MyBean();
}
```

- `@Scope("singleton")`: 빈 객체의 범위를 싱글톤으로 지정한다.

```
@Bean
@Scope("sigleton")
public MyBean myBean(){
    return new MyBean();
}
```

