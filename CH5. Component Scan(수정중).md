# CH5. Component Scan(수정중)

- 컴포넌트 스캔: 스프링이 직접 클래스를 검색하여 빈으로 등록하는 기능



## 5.1 @Component 애노테이션으로 스캔 대상 지정하기

```java
@Component
```

- 클래스 이름의 첫 글자를 소문자로 바꾼 이름을 빈 이름으로 사용한다.

```java
@Component("빈객체이름")
```

- `빈객체이름`을 빈 이름으로 사용한다.



```java
// MyObject.java
import org.springframework.stereotype.Component;

@Component
public class MyObject {
    
}
```



## 5.2 @ComponentScan 애노테이션으로 스캔 설정하기

```java
// AppContext.java

import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan(basePackages={"spring"})
public class AppContext{
    
}
```

- `@ComponoentScan`의 `basePacakge` 속성은 지정한 `패키지`와 그 하위 패키지에 속한 클래스를 속성 대상으로 설정한다.

```java
@Component(basePackages={'패키지이름1', '패키지이름2', ...})
```



## 5.4 스캔 대상에서 제외하거나 포함하기

```java
@ComponentScan(basePackages={"패키지이름"},
excludeFilters=@Filter(type=필터타입, pattern=패턴))
```

- `필터타입`: 여러 가지 필터가 있다.
- `패턴`: `String[]` 타입이므로 패턴을 한 개 이상 지정할 수 있다.



### FilterType.REGEX 사용하기

```java
@ComponentScan(basePackages={"패키지이름"},
excludeFilters=@Filter(type=FilterType.REGEX, pattern="정규표현식"))
```

- `@ComponentScan`의 `excludeFilters` 속성으로 특정 대상을 자동 등록 대상에서 제외할 수 있다.
  - `정규표현식` => `패키지\\..*이름`: `패키지`에서 이름이 `이름`으로 끝나는 클래스를 컴포넌트 스캔 대상에서 제외한다.



```java
// AppContext.java

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;

@Configuration
@ComponentScan(basePackages={"spring"},
excludeFilters=@Filter(type=FilterType.REGEX, pattern="spring\\..*Manager"))
public class AppContext{
    
}
```

- `pattern="spring\\..*Manager"`: `spring`으로 시작하고 `Manager`로 끝나는 정규표현식을 `pattern`에 지정하였다. (이는 `spring.****Manager`를 컴포넌트 스캔 대상에서 제외할 것이다.)



### FilterType.ASPECTJ 사용하기

```java
@ComponentScan(basePackages={"패키지이름"},
excludeFilters=@Filter(type=FilterType.ASPECTJ, pattern="ASPECTJ 패턴"))
```

- `ASPECTJ 패턴` => `패키지.*이름`: `패키지`에서 이름이 `이름`으로 끝나는 클래스를 컴포넌트 스캔 대상에서 제외한다.



```java
// AppContext.java

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;

@Configuration
@ComponentScan(basePackages={"spring"},
excludeFilters=@Filter(type=FilterType.REGEX, pattern="spring.*Dao"))
public class AppContext{
    
}
```



- AspectJ 패턴을 사용하기 위하여 의존 대상에 `aspectjweaver` 모듈을 추가해야 한다.

P.136 ??



### 특정 애노테이션을 붙인 클래스를 컴포넌트 대상에서 제외하기

p.136 p 137 ??



### 5.4.1 기본 스캔 대상

- 다음 애노테이션을 붙인 클래스가 컴포넌트 스캔 대상에 포함된다.

| 애노테이션       | 패키지                                   | 기타           |
| ---------------- | ---------------------------------------- | -------------- |
| `@Component`     | `org.springframework.stereotype`         |                |
| `@Contoller`     |                                          | 웹 MVC와 관련  |
| `@Service`       |                                          |                |
| `@Repository`    |                                          | DB 연동과 관련 |
| `@Asepct`        | `org.aspectj.lang.annotation`            |                |
| `@Configuration` | `org.springframework.context.annotation` |                |

- `@Aspect` 애노테이션을 제외한 나머지 애노테이션은 `@Component` 애노테이션에 대한 특수 애노테이션이다.



## 5.5 컴포넌트 스캔에 따른 충돌 처리

### 5.5.1 빈 이름 충돌

```java
@Configuration
@ComponentScan(basePackages={"패키지이름1", "패키지이름2"})
```

- 두 패키지에 존재하는 클래스의 이름이 같고, 해당 클래스들에 `@Component` 애노테이션을 붙였을 경우, `ComponentScan`으로 두 패키지를 지정하면 exception이 발생한다. 둘 중 하나에 명시적으로 빈 일므을 지정해서 이름 충돌을 피해야 한다.



### 5.5.2 수동 등록한 빈과 충돌

- `@Component` 애노테이션으로 컴포넌트 스캔 대상으로 지정한 클래스를 직접 `@Bean` 애노테이션으로 빈 객체를 등록할 경우, 후자로 등록한 빈이 우선하여 빈 객체는 한 개만 존재한다.

```java
// mypackage.MyBean.java
@Copmonent
public class MyBean{
    
}
```

```java
// AppContext.java
@Configuration
@ComponentScan(basePackages={"mypackage"})
public class AppContext {
    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

- 위에서 `myBean`이 아니라 다른 이름으로 빈 객체를 등록했다면, `MyBean` 타입의 객체가 두 개 존재하므로 자동 주입을 할 경우 `@Qualifier` 애노테이션으로 빈을 지정한다.