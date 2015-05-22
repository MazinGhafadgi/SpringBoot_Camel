
package camelinaction;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.ClosedCorrelationKeyException;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

/**
 * The ABC example for using the Aggregator EIP.
 * <p/>
 * This example have 4 messages send to the aggregator, by which one
 * message is published which contains the aggregation of message 1,2 and 4
 * as they use the same correlation key.
 * <p/>
 * And this time we close the correlation keys which means that when we send
 * a message with a closed correlation key it should fail.
 * <p/>
 * See the class {@link MyAggregationStrategy} for how the messages
 * are actually aggregated together.
 *
 * @see MyAggregationStrategy
 * @version $Revision$
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = MyConfig.class)
public class AggregateABCCloseTest extends CamelSpringTestSupport{
	
	
	@Autowired
	EndPoint1 endPoint1;
	
	
	 @EndpointInject(uri = "mock:result")
	  protected MockEndpoint mockEndpoint;

	
	
    @Test
    @DirtiesContext
    public void testABCClose() throws Exception {
         getMockEndpoint("mock:result").expectedMessageCount(1);
         getMockEndpoint("mock:result").expectedBodiesReceived("Hi, How Are You ?");

        // send the first message
        template.sendBodyAndHeader("direct:start", "Hi, ", "myId", 1);
        // send the 2nd message with the same correlation key
        template.sendBodyAndHeader("direct:start", "How Are ", "myId", 1);
        // the F message has another correlation key
        template.sendBodyAndHeader("direct:start", "F", "myId", 2);
        // now we have 3 messages with the same correlation key
        // and the Aggregator should publish the message
        template.sendBodyAndHeader("direct:start", "You ?", "myId", 1);
   
        // sending with correlation id 1 should fail as its closed
        try {
            template.sendBodyAndHeader("direct:start", "A2", "myId", 1);
        } catch (CamelExecutionException e) {
            ClosedCorrelationKeyException cause = assertIsInstanceOf(ClosedCorrelationKeyException.class, e.getCause());
            assertEquals("1", cause.getCorrelationKey());
        }

        assertMockEndpointsSatisfied();
    }

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new AnnotationConfigApplicationContext(MyConfig.class);
	}

    

}
