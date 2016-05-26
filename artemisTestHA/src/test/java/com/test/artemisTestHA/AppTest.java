package com.test.artemisTestHA;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws JMSException {
        Map<String, Object> transportConfig = new HashMap<>();
        transportConfig.put(TransportConstants.HOST_PROP_NAME, "linux-6rrn");
        transportConfig.put(TransportConstants.PORT_PROP_NAME, "61617");
        transportConfig.put(TransportConstants.SSL_ENABLED_PROP_NAME, false);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), transportConfig);

        ConnectionFactory cf = ActiveMQJMSClient.createConnectionFactoryWithHA(JMSFactoryType.CF,transportConfiguration);

        Queue orderQueue = ActiveMQJMSClient.createQueue("TestQueue");

        Connection connection = cf.createConnection("admin", "admin");

        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        MessageProducer producer = session.createProducer(orderQueue);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        connection.start();

        System.out.println("Publishing messages.");
        long starttime = System.currentTimeMillis();
        IntStream.range(0, 100).forEach(t -> {

                    TextMessage message = null;
                    try {
                        message = session.createTextMessage(String.format("Message %d of 100.", t));
                        message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
                        producer.send(message);
                        System.out.printf("Send message %d of 100.\n", t);
                    } catch (JMSException e) {
                        throw new IllegalStateException("Send message failed", e);
                    }
                }
        );

        producer.close();
        connection.close();
        long endtime = System.currentTimeMillis() - starttime;
        Assert.assertTrue("Publishing the messages should take at least 3000 ms", endtime >= 3000);
    }
}
