# CH4. Autowired DI

-  의존 자동 주입(autowired dependency injection)
  - `@Autowired` 애노테이션 사용하기
  - `@Resource` 애노테이션 사용하기



## 4.2 @Autowired로 의존 자동 주입하기

```java
// MyObject.java
public class MyObject {
    public String name;
    
    public MyObject(String name){
        this.name = name;
    }
}
```

```java
// MyManager.java
import org.springpramework.beans.factory.annotation.Autowired;

public class MyManager {
    private MyObject myObject;
    
    @Autowired
    public void setMyObject(MyObject myObject){
        this.myObject = myObejct;
    }
    
    public void printName(){
        System.out.println("객체 이름: " + myObject.name");
    }
}
```

```java
// AppContext.java
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

public class AppContext {
    
    @Bean
    public MyObject myObject() {
        return new MyObject("My Object");
    }
    @Bean
    public MyManager myManager(){
        return new MyManager();
    }
}
```

- `@Autowired` 애노테이션을 필드나 세터 메서드에 붙이면 스프링은 타입이 일치하는 빈 객체를 찾아 주입한다.



### 2.1 일치하는 빈이 없는 경우

- 일치하는 빈이 없거나, 일치하는 빈이 두 개 이상이면 exception이 발생한다.



## 4.3 @Qualifier 애노테이션을 이용한 의존 객체 선택

- `@Qualifier`: 자동 주입이 가능한 빈이 두 개 이상일 때, 자동 주입 대상을 한정할 수 있다.
  - (1) `@Bean` 애노테이션을 붙인 빈 설정 메서드에 붙이기
  - (2) `@Autowired` 애노테이션을 붙인 곳에서 사용하기



```java
// AppContext.java
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

@Configuration
public class AppContext{
    @Bean
    @Qualifier("myBean")
    public MyBean myBean1(){
        return new MyBean();
    }
    
    @Bean
    public ByBean myBean2(){
        return new MyBean();
    }
}
```

```java
// MyManager.java

public class MyManager{
    private MyBean myBean;
    
    @Autowired
    @Qualifier("myBean")
    public void setMyBean(MyBean myBean){
        this.myBean = myBean;
    }
}
```



### 4.3.1 빈 이름과 기본 한정자

- 빈 설정에 `@Quantifier` 애노테이션이 없으면 빈의 이름(메서드의 이름)을 한정자로 지정한다.
- `@Autowired` 애노테이션은 `Quantifier` 애노테이션이 없으면 
  - 필드의 경우, 필드의 이름이 한정자가 된다.
  - 세터 메서드의 경우, 파라미터의 이름이 한정자가 된다.



## 4.4 상속과 자동 주입

```java
// DerivedBean.java
public class DerivedBean extends MyBean{
    
}
```

```java
// AppContext.java
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

@Configuration
public class AppContext{
    @Bean
    public MyBean myBean1(){
        return new MyBean();
    }
    
    @Bean
    public ByBean myBean2(){
        return new DerivedBean();
    }
}
```

```java
// MyManager.java

public class MyManager{
    private MyBean myBean;
    
    @Autowired
    public void setMyBean(MyBean myBean){
        this.myBean = myBean;
    }
}
```

- 이 경우, `DerivedBean`이 `MyBean`을 상속하여 `MyBean` 타입의 빈 객체가 두 개이다. 따라서 자동 주입 대상이 한 개로 한정되지 않아 exception이 발생한다.
- 따라서 `@Qulifier` 애노테이션을 사용하거나, `DerivedBean`으로 한정하고 싶은 경우  `MyManager#setMyBean`의 파라미터에 `DerivedBean`을 사용한다.



## 4.5 @Autowired 애노테이션의 필수 여부

- 자동 주입할 대상을 찾지 못할 경우 반드시 exception이 발생한다. 이를 방지하기 위하여 다음과 같은 방법들로 필수 여부를 지정할 수 있다.
  - (1) `@Autowired`의 `required` 속성 사용하기
  - (2) 자바 8의 `Optional` 사용하기
  - (3) `@Nullable` 애노테이션 사용하기



### 방법1: @Autowired의 required 속성 사용하기

```java
// MyManager.java
// 세터 메서드의 경우
public class MyManager {
    private MyBean myBean;
    
    @Autowired(required=false)
    public void setMyBean(MyBean myBean){
        this.myBean = myBean;
    }
}
```

```java
// MyManager.java
// 필드의 경우
public class MyManager {
    @Autowired(required=false)
    private MyBean myBean;
 
}
```

- `@Autowired(required=false)`:  자동 주입할 대상을 찾지 못했어도 exception을 일으키지 않으며, 세터 메서드를 실행하지 않는다(자동 주입을 수행하지 않는다).



### 방법2: 자바 8의 Optional 사용하기

```java
// MyManager.java
// 세터 메서드의 경우
public class MyManager {
    private MyBean myBean;
    
    @Autowired
    public void setMyBean(Optional<MyBean> myBean){
        if (myBean.isPresent()){
            this.myBean = myBean;
        } else {
            this.myBean = null;
        }
    }
}
```

```java
// MyManager.java
// 필드의 경우
public class MyManager {
    @Autowired
    private Optional<MyBean> myBean;
    
}
```

- 자동 주입 대상이 `Optional`인 경우,
  - 일치하는 빈이 존재하지 않으면 값이 없는 `Optional`을 인자로 전달한다.
  - 일치하는 빈이 존재하면 해당 빈을 값으로 갖는 `Optional`을 인자로 전달한다.



### 방법3: @Nullable 애노테이션 사용하기

```java
// MyManager.java
// 세터 메서드의 경우
import org.springframework.Nullable;

public class MyManager {
    private MyBean myBean;
    
    @Autowired
    public void setMyBean(@Nullable MyBean myBean){
        this.myBean = myBean;
    }
}

```

```java
// MyManager.java
// 필드의 경우
import org.springframework.Nullable;

public class MyManager {

    @Autowired
    @Nullable
    private MyBean myBean;

}

```

- 스프링 컨테이너는 세터 메서드를 호출할 때
  - 자동 주입할 빈이 존재하지 않으면 인자로 `null`을 전달한다.
  - 자동 주입할 빈이 존재하면 해당 빈을 인자로 전달한다.
- 자동  주입할 빈이 존재하지 않으면
  - `@Autowired` 애노테이션의 경우 세터 메서드를 호출하지 않는다.
  - `@Nullable` 애노테이션의 경우 세터 메서드를 호출한다.



### 4.5.1 생성자 초기화외 필수 여부 지정 방식 동작의 이해

```java
// MyManager.java
public class MyManager{
    private MyObject myObject;
    
    public MyMaganger(){
        myObject = new MyObject();
    }
    
    @Autowired(required=false)
    public void setMyObject(MyObject myObject) {
        this.myObject = myObject;
    }
    
    public void printIsNull(){
        System.out.println("null:" + myObject == null);
    }
}
```

```java
// AppContext.java
public class AppContext{
    @Bean
    public MyManager myManager(){
        return new MyManager();
    }
}
```

```java
// Main.java
public class Main{
    private static ApplicationContext ctx = null;

	private static void main(String[] args){
        ctx = new AnnotationConfigApplicationContext(Appctx.class);
        
        ctx.getBean("myObject", MyObject.class).printIsNull();
    }

}
```

- `MyObject` 타입의 빈이 존재하지 않는다면, 기본 생성자에서 초기화된 객체가 `myObject`에 할당되어있다. 따라서 실행 결과는 다음과 같다.

```
null: false
```





```java
// MyManager.java
public class MyManager{
    private MyObject myObject;
    
    public MyMaganger(){
        myObject = new MyObject();
    }
    
    @Autowired
    public void setMyObject(@Nullable MyObject myObject) {
        this.myObject = myObject;
    }
    
    public void printIsNull(){
        System.out.println("null:" + myObject == null);
    }
}
```

- `@Nullable` 애노테이션을 사용한 경우, 스프링 컨테이너는 의존 주입 대상이 존재하지 않으면 `null`을 값으로 전달한다. 기본 생성자에서 객체를 초기화하였어도 `null`을 인자로 세터 메서드를 호출했기 때문이다. 따라서 실행 결과는 다음과 같다.

```
null: true
```

- `Optional` 타입을 사용한 경우, 빈이 없으면 값이 없는 `Optional`을 할당한다.



## 4.6 자동 주입과 명시적 의존 주입의 관계

```java
// MyManager.java
public class MyManager {
    private static MyObject myObject;
    
    @Autowired
    public void setMyObject(MyObject myObject){
        this.myObject = myObject;
    }
}
```

```java
// AppContext.java

@Configuration
public class AppContext {
    @Bean
    public MyObject myObject(){
        return new MyObject();
    }

    @Bean
    public MyManager myManager(){
        MyManager myManager = new MyManager();
        myManager.setMyObject(myObject());
        return myManager();
    }
}
```

- `@Autowired` 애노테이션을 세터 메서드의 붙여 자동 주입을 한 동시에 빈 객체를 인자로 세터 메서드를 호출한 경우, 자동 주입을 통해 일치한 빈이 주입된다. 따라서 `@Autowired` 애노테이션을 사용한 경우 설정 클래스에서 객체를 주입하기보다 자동 주입 기능을 사용하는 편이 좋다.

