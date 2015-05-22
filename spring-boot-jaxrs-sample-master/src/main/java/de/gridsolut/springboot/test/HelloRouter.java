package de.gridsolut.springboot.test;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@Component
public class HelloRouter extends SpringRouteBuilder {
	
 
  @Override
  public void configure() throws Exception {
	 //enable tracer.
	getContext().setTracing(true);
	
	System.out.println("I am started HelloRouter ??????.....");  
    from("direct-vm:requestChannel").
    wireTap("seda:audit").
    process(new Processor() {
        public void process(Exchange exchange) throws Exception {
            System.out.println("I am invoked......" + exchange.toString());
        }
    })
    .to("helloBean")
    .to("seda:responseChannel");
    
    //Audit route the audit service will consume the message from the seda internal queue channel.
    from("seda:audit").bean(AuditService.class, "auditFile");
    
  }
  
  public static void main(String[] args) throws Exception {
      ConfigurableApplicationContext ctx = SpringApplication.run(HelloRouter.class);
      System.in.read();
      ctx.close();
  }
 
}
