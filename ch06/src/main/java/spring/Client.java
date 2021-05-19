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
