package de.gridsolut.springboot.test.service;


public class HelloBean {
	
	public HelloBean(){
		
	}

	public String sayHello(String message){
		System.out.println("Hello Bean..." + message);
		return "This is my response Good response!!!!";
	}
	
}
