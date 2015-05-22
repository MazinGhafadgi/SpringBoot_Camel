package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class MyConfig {
	
	@Bean
	public EndPoint1 endPoint1(){
		return new EndPoint1();
	}
	
	@Bean
	public EndPoint2 endPoint2(){
		return new EndPoint2();
	}
	
	@Bean
	public EndPoint3 endPoint3(){
		return new EndPoint3();
	}
	
	@Bean
    public RouteBuilder createRouteBuilder2() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    // do a little logging
                    .log("Sending ${body} with correlation key ${header.myId}")
                    // aggregate based on header correlation key
                    // use class MyAggregationStrategy for aggregation
                    // and complete when we have aggregated 3 messages
                    .aggregate(header("myId"), new MyAggregationStrategy()).completionSize(3)
                        // and close completed correlation keys, and remember back the last
                        // 2000 used keys
                        .closeCorrelationKeyOnCompletion(2000)
                        // do a little logging for the published message
                        .log("Sending out ${body}")
                        // and send it to the mock
                        .to("bean:endPoint1")
                        .to("mock:result");
            }
        };
    }
	
	
	@Bean
    public RouteBuilder createRouteBuilder1() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:serviceManager")
                        .to("bean:endPoint1");
                        
            }
        };
    }
	

}
