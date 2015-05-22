package de.gridsolut.springboot.test; 

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Path;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.spring.SpringResourceFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import de.gridsolut.springboot.test.service.HelloBean;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@ImportResource({"classpath:META-INF/cxf/cxf.xml", "classpath:META-INF/cxf/cxf-servlet.xml"})
public class CXFTest {

    @Autowired
    private ApplicationContext ctx;

    public static void main(String[] args) {
    	Object[] sources = new Object[2];
    	sources[0] = (HelloRouter.class);
    	sources[1] = (CXFTest.class);
        ApplicationContext ctx = SpringApplication.run(sources, args);
    }

    @Bean
    public ServletRegistrationBean cxfServletRegistrationBean(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CXFServlet(), "/services/*");
        registrationBean.setAsyncSupported(true);
        registrationBean.setLoadOnStartup(1);
        registrationBean.setName("CXFServlet");
        return registrationBean;
    }

    //http://stackoverflow.com/questions/13520821/autodiscover-jax-rs-resources-with-cxf-in-a-spring-application
    //http://svn.apache.org/viewvc/cxf/trunk/rt/frontend/jaxrs/src/main/java/org/apache/cxf/jaxrs/spring/SpringResourceServer.java?revision=1548504&view=co&pathrev=1548504
    @Bean
    public Server jaxRsServer() {
        List<ResourceProvider> resourceProviders = new LinkedList<ResourceProvider>();
        for (String beanName : ctx.getBeanDefinitionNames()) {
            if (ctx.findAnnotationOnBean(beanName, Path.class) != null) {
                SpringResourceFactory factory = new SpringResourceFactory(beanName);
                factory.setApplicationContext(ctx);
                resourceProviders.add(factory);
            }
        }
        if (resourceProviders.size() > 0) {
            JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();

            factory.setBus(ctx.getBean(SpringBus.class));
            factory.setProviders(Arrays.asList(new JacksonJsonProvider()));
            factory.setResourceProviders(resourceProviders);
            Server server = factory.create();
            return server;
        } else {
            return null;
        }
    }
    
    @Bean(name = "helloBean")
    public HelloBean createBean(){
       return new HelloBean();
    }

}
