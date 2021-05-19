# CH3 스프링 DI

## 3.1 의존이란?

- **DI(Dependency Injection)**: 의존 주입을 의미
- **의존(dependency)**: 객체 간의 의존을 의미.
  - 한 클래스에서 다른 클래스의 메서드를 실행할 때, "A 클래스는 B 클래스에 의존한다" 고 할 수 있다.
- 의존 대상을 구하는 방법: (1) 의존 대상 객체를 직접 생성하기 (2) DI (3)서비스 로케이터



### 예: 의존 대상 객체를 직접 생성하기

```java
public class MemberRegisterService{
    // 의존 객체를 직접 생성
    private MemberDao memberDao = new memberDao();
    
    public void regist(RegisterRquest req){
        // 이메일로 회원 데이터 조회
        Member member = memberDao.selectByEmail(req.getEmail());
    }
}
```

- 이때, `MemberRegisterService`의 객체를 생성하면 `MemberDao` 객체 또한 생성된다. 이는 유지보수 관점에서 문제가 될 수 있는 방법이다.



## 3.2 DI를 통한 의존 처리

- DI에서는 <u>의존 객체를 전달받는 방식으로</u> 의존하는 대상을 구한다.

```java
//MemberRegisterService.java
package spring;

import java.time.LocalDateTime;

public class MemberRegisterService{
    private MemberDao memberDao;
    
    public MemberRegisterService(MemberDao memberdao){
        this.memberdao = memberdao;
    }
    
    public Long regist(RegisterRequest req){
        Member member = memberDao.selectByEmail(req.getEmail());
        if (member != null) new throw DuplicatMemberException("dup eamil " + req.getEmail());
        
        Member newMember = new Member(
        	req.getEmail(), req.getPassword(), rq.getName(),
            LocalDateTime.now());
        memberDao.insert(newMember);
        
        return newMember.getId();
    }
}
```



```java
// MemberRegisterService.java의 7행부터 11행
private MemberDao memberDao;
    
public MemberRegisterService(MemberDao memberdao){
    this.memberdao = memberdao;
}
```

- 생성자를 통하여 의존 객체를 전달 받고 있다. 즉 <u>객체를 주입 받았다</u>.



## 3.3 DI와 의존 객체 변경의 유연함

### 의존 객체를 직접 생성하는 방식

```java
public class MemberRegisterService{
    private MemberDao memberDao = new MemberDao();
}
// ....
public class ChangePasswordService{
    private MemberDao memberDao = new MemberDao();
}
// ============= 클래스 이름 변경 ========================
// 회원 데이터의 빠른 조회를 위해 캐시 기능을 적용하여
// MebmerDao 클래스를 ChachedMemberDao로 새로 정의한다면,
// 위 두 클래스의 new MemberDao()를 모두 new ChacedMemberDao()로 변경해야 함.
public class ChachedMemberDao extends MemberDao{
    
}
```



### DI 패턴 사용하는 방식

```java
public class MemberRegisterService{
    private MemberDao memberDao;
    
    // 생성자로 객체 전달 받기
    public MemberRegisterService(MemberDao memberDao){
        this.memberDao = memberDao;
    }
}

public class ChangePasswordService{
    private MemberDao memberDao;
    
    // 생성자로 객체 전달 받기
    public ChangePasswordService(MemberDao memberDao){
        this.memberDao = memberDao;
    }
}

// 실제 사용시
// MemberDao memberDao = new MemberDao(); 에서 아래처럼 생성자만 바꾸면 된다.
MemberDao memberDao = new ChacnedMemberDao();
MemberRegisterService memberRegisterService = new MemberRegisterService(memberDao);
ChangePasswordService changePasswordService = new ChangePasswordService(memberDao);
```



## 3.4 예제 프로젝트 만들기

```
회원 데이터 관련 클래스
	ㄴ Member
	ㄴ WrongPasswordException
	ㄴ MemberDao
회원 가입 처리 관련 클래스
	ㄴ DuplicateMemberException
	ㄴ RegisterRequest
	ㄴ MemberRegisterService
암호 변경 관련 클래스
	ㄴ MemberNotFoundException
	ㄴ ChangePassordService
```



## 3.6 스프링의 DI 설정

- 스프링 역시 조립기이다.
- 다만 `Assembler`는 `MemberDao`와 같이 특정 타입의 클래스만 생성하고 있다. 이에 반해, 스프링은 범용 조립기이다.



### 3.6.1 스프링을 이용한 객체 조립과 사용

- (1) 설정 정보 작성: 스프링이 어떤 객체를 생성하고, 의존을 어떻게 주입할지 정의한다.
  - 23행 30행의 `memberDao()`는 `memberDao`라는 메서드가 생성한 빈 객체를 반환한다.

```java
// AppCtx.java
package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import spring.ChangePasswordService;
import spring.MemberDao;
import spring.MemberRegisterService;

@Configuration
public class Appctx {
    
    // member라는 이름의 빈 객체 생성
    @Bean
    public MemberDao memberDao() {
        return new MemberDao();
    }
    
    // mebmerRegSvc라는 이름의 빈 객체 생성
    @Bean
    public MemberRegisterRegisterService memberRegSvc() {
		return new MembrRegisterService(memberDao());
    }
    
    // changePwdSvc라는 이름의 빈 객체 생성
    @Bean
    public ChangePasswordService changePwdSvc() {
        ChangePasswordService pwdSvc = new ChangePasswordService();
        pwdSvc.setMemberDao(memberDao());
        
        return pwdSvc;
    }
}
```

- (2) 스프링 컨테이너 사용하기

```java
// MainForSpring.java : MainForAssembler.java의 수정
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//... In the MainForSpring class

private static ApplicationContext ctx = null;

// ... In the main Method
ctx = new AnnotationConfigApplicationContext(Appctx.class);

// ... In the processNewCommand Method
MemberRegisterService regSvc = ctx.getMBean("regSvc", MemberRgisterService.class);

// ... In the processChangeCommand Method
ChangePasswordService changePwdSvc = ctx.getBean("changePwdSvc", ChangePasswordService.class);


```



### 6.2 DI 방식 1 : 생성자 방식

- 생성자에 전달한 의존 객체가 두 개인 경우

```java
// MemberDao.java
import java.util.Collection;

// in the MemberDao class...
public Collection<Member> selectAll() {
    return map.values();
}
```

```java
// MemberPrinter.java
package spring;

public class MemberPrinter{
    
    public void print(Member member) {
        Systetm.out.printf("회원 정보: 아이디=%d, 이메일=%d, 이름=%s, 등록일=%tF\n", member.getId(), member.getEmail(), member.getName(), member.getRegisterDateTime());
    }
}
```

```java
// MemberListprinter.java
package spring;

import java.utill.Collection;

public class MemberListPrinter {
    
    private MemberDao memberDao;
    private MEmberPrintr printer;
    
    public MemberListPrinter(MemberDao memberDao, MemberPrintr printer) {
        this.memberDao = memberDao;
        this.printer = printer;
    }
    
    public void printAll() {
        Collection<Member> members = memberDao.selectAll();
        members.forEach(m -> printer.print(m));
    }
    
}
```

```java
// AppCtx.java

// In the AppCtx class...
@Bean
public MemberPrinter memberPrinter() {
    return new MemberPrinter();
}

@Bean
public MemberListPrinter listPrinter() {
    return new MemberListPrinter(memberDao(), memberPrinter());
}

​```java
// MainForSpring.java
import spring.MemberListPrinter;

// In the main Method...
if (command.startsWith("new ")) {}
else if (command.equlas("list")) {
    processListCommand();
    continue;
}

private static void processListCommand() {
    MemberListPrintr listPrinter = ctx.getBean("listprinter", MemberListPrinter.class);
    listPrinter.printAll();
}
```



### 6.3 DI 방식 2 : 세터 메서드 방식

- 세터는 자바빈 규칙에 따라 작성한다.
  - 메서드 이름이 `set`으로 시작한다.
  - `set` 뒤에 첫 글자는 대문자로 시작한다.
  - 파라미터가 1개이다.
  - 리턴 타입이 void이다.

```java
// MemberLinfoPrinter.java
package spring;

public class MemberInfoPrinter {
    
    private MemberDao memDao;
    private MemberPrinter printer;
    
    public void printMemberInfo(String email) {
        Member member = memberDao.selectByEmail(email);
        
        if (member == null) {
            System.out.println("데이터 없음");
            return;
        }
        
        printer.print(member);
        System.out.println();
    }
    
    public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
    }
    
    public void setPrinter(MemberPrinter printer) {
        this.printer = printer;
    }
}
```

```java
// AppCtx.java

// In the AppCtx class...

@Bean
public MemberInfoPrinter infoPrinter() {
    MemberInfoPrinter infoPriner = new MemberInfoPrinter();
    infoPrinter.setMemberDao(memberDao());
    infoPrinter.setPrinter(memberPrinter());
}
```



### 생성자 방식과 프로퍼티 방식

- 생성자 방식: 빈 객체를 생성하는 시점에 모든 의존 객체가 주입된다.
- 프로퍼티 방식: 세터 메서드 이름을 통해 어떤 의존 객체가 주입되는지 알 수 있다.



## 3.7 @Configuration 설정 클래스의 @Bean 설정과 싱글톤

- 스프링 컨테이너는 `@Bean` 애노테이션이 붙은 메서드에 대하여 한 개의 객체만을 생성하므로, 해당 메서드를 호출할 때마다 동일한 객체를 리턴한다.



## 3.8 두 개 이상의 설정 파일 사용하기

- 스프링은 한 개 이상의 설정 파일을 이용하여 컨테이너를 생성할 수 있다.



### 방법1: @Autowired 사용하기

```java
// AppContext1.java
// import 생략

@Configuration
public class AppContext1{
    @Bean
    public MyObject myObject(){
        return new MyObject();
    }
}
```

```java
// AppContext2.java
// import 생략

import org.springframework.beans.factory.annotation.Autowired;
@Configuration
public class AppContext2{
    @Autowired
    private MyObject myObject;
    
    @Bean
    public MyManager myManager(){
        return new MyManger(myObject);
    }
}
```

```java
// Main.java

public class Main{
    private static ApplicationContext ctx;
    
    public static void main(String[] args){
        ctx = new AnnotationConfigApplicationContext(AppContext1.class, AppContext2.class);
    }
}
```

- `@Autowried` 애노테이션: 스프링 설정 클래스의 필드에 붙이면, 해당 필드의 타입의 빈을 찾아 자동으로 필드에 할당한다.
- (1) 다른 설정 파일의 빈 객체를 담을 필드를 정의한다.
- (2) 필드에 `@Autowired` 애노테이션을 붙여 선언한다.

 

### 3.8.1 @Configuration 애노테이션, 빈, @Autowried 애노테이션

- 스프링은 `@Configuration` 애노테이션이 붙은 설정 클래스를 내부적으로 빈으로 등록한다.

```java
// Main.java
AbstractApplicationContext ctx = new AnnotationApplicaionContext(AppContext.class);

AppContext appConf = ctx.getBean(AppContext.class);
System.out.println(appConf == null); 	// false
```



### 3.8.2 방법2: @Import 사용하기

```java
// AppContext1.java

@Configuration
@import(AppContext2.class)
public class AppContext1 {
    
}
```

```java
// Main.java
private static ApplicationContext ctx = null;

public static void main(String[] args){
    ctx = new AnnotationConfigApplicationContext(AppContext1.class);
}
```



- 한 개의 설정 파일 가져오기

```java
@import(설정클래스.class)
```

- 두 개 이상의 설정 파일 가져오기: 배열을 이용하여 두 개 이상의 설정 클래스를 지정할 수 있다.

```java
@import({설정클래스1.class, 설정클래스2.class,...})
```



## 3.9 getBean() 메서드 사용하기

### 빈객체이름과 빈객체타입 전달하기

```java
getBean(빈객체이름, 빈객체타입.class)
```

### 빈객체타입 전달하기

```java
getBean(빈객체타입.class)
```

- `빈객체타입`의 빈 객체가 하나일 때 유효하다. 없거나, 두 개 이상이면 exception이 발생한다.



## 3.10 주입 대상 객체를 모두 빈 객체로 설정해야 하는가?

```java
// AppContext.java
@Configuration
public class AppContext{
    private Myobject myObject = new MyObject();
    
    @Bean
    public Foo foo(){
        return new Foo();
    }
}
```

- 주입할 객체를 반드시 빈 객체로 설정해야하는 것은 아니다. 다만 스프링이 관리할 수 있는 객체는 빈 객체이다.



