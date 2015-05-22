package de.gridsolut.springboot.test.service;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//http://www.vogella.com/tutorials/REST/article.html
@Component
@Path("/hello")
public class Hello {

	@Autowired
	  private ProducerTemplate producerTemplate;
	  
	  @Autowired
	  private ConsumerTemplate consumerTemplate;
	
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        return "Hello Jersey";
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
    }
    
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/message/")
	public void createEstatement(String message) {
		producerTemplate.sendBody("direct-vm:requestChannel", message);
		System.out.println("Message recived is " + consumerTemplate.receiveBody("seda:responseChannel"));
		// String response = consumerTemplate.receiveBody("direct-vm:booz", String.class);
		//System.out.println(response);
	}

}
