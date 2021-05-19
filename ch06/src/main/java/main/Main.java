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
