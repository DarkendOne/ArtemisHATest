package com.test.artemisTestHA;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Hello world!
 *
 */
public class App 
{

    public static final HashSet<String> jmsMsgIds = new HashSet<>(100);


    public static void main( String[] args ) throws JMSException, IOException, InterruptedException {
        sendMessages();
        killServer();
        System.out.println("Waiting 10 seconds for backup to come up.");
        Thread.sleep(10000);
        receiveMessages();
    }

    private static void sendMessages() throws JMSException {
        Map<String, Object> transportConfig = new HashMap<>();
        transportConfig.put(TransportConstants.HOST_PROP_NAME, "artemis-master");
        transportConfig.put(TransportConstants.PORT_PROP_NAME, "61616");
        transportConfig.put(TransportConstants.SSL_ENABLED_PROP_NAME, false);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), transportConfig);

        ConnectionFactory cf = ActiveMQJMSClient.createConnectionFactoryWithHA(JMSFactoryType.CF, transportConfiguration);

        Queue orderQueue = ActiveMQJMSClient.createQueue("exampleQueue");

        Connection connection = cf.createConnection("admin", "password");

        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        MessageProducer producer = session.createProducer(orderQueue);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        connection.start();

        System.out.println("Publishing messages.");
        long starttime = System.currentTimeMillis();
        IntStream.range(0, 100).forEach(t -> {

                    TextMessage message = null;
                    try {
                        String randomMsg = RandomStringUtils.randomAlphanumeric(80000);

                        message = session.createTextMessage(String.format("%s", t, randomMsg));
                        message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
                        producer.send(message);
                        jmsMsgIds.add(message.getJMSMessageID());
                        session.commit();
                        System.out.printf("Send message %d of 100.\n", t);
                    } catch (JMSException e) {
                        throw new IllegalStateException("Send message failed", e);
                    }
                }
        );

        producer.close();
        connection.close();
        long endtime = System.currentTimeMillis() - starttime;
        System.out.printf("Publishing messages took %d ms.\n", endtime);
    }

    private static void killServer() throws IOException, InterruptedException {
        System.out.printf("Killing master.\n");
        Process p = Runtime.getRuntime().exec("jps");
        List<String> output = IOUtils.readLines(p.getInputStream(), Charset.defaultCharset());
        Optional<String> artemisOutput = output.stream().filter(t -> t.contains("Artemis")).findFirst();
        if (!artemisOutput.isPresent()) {
            throw new IllegalStateException("Artemis process could not be found running.");
        }
        String processId = artemisOutput.get().substring(0, artemisOutput.get().indexOf(' '));

        System.out.printf("Found Artemis process at %s\n", processId);
        System.out.printf("Killing process %s\n", processId);

        Process killProcess = Runtime.getRuntime().exec("kill -9 " + processId);
        killProcess.waitFor(5, TimeUnit.SECONDS);
        if (killProcess.exitValue() == 0) {
            System.out.printf("Artemis killed successfully.\n");
        }
    }

    private static void receiveMessages() throws JMSException {
        Map<String, Object> transportConfig = new HashMap<>();
        transportConfig.put(TransportConstants.HOST_PROP_NAME, "artemis-slave");
        transportConfig.put(TransportConstants.PORT_PROP_NAME, "61616");
        transportConfig.put(TransportConstants.SSL_ENABLED_PROP_NAME, false);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), transportConfig);

        ConnectionFactory cf = ActiveMQJMSClient.createConnectionFactoryWithHA(JMSFactoryType.CF, transportConfiguration);

        Queue orderQueue = ActiveMQJMSClient.createQueue("exampleQueue");

        Connection connection = cf.createConnection("admin", "password");

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        MessageConsumer consumer = session.createConsumer(orderQueue);


        connection.start();

        System.out.println("Consuming messages.");
        long starttime = System.currentTimeMillis();


        TextMessage message = (TextMessage) consumer.receiveNoWait();

        while (message != null) {
            System.out.printf("Consuming message %s.\n", message.getJMSMessageID());
            jmsMsgIds.remove(message.getJMSMessageID());

            message = (TextMessage) consumer.receiveNoWait();
        }



        consumer.close();
        connection.close();
        long endtime = System.currentTimeMillis() - starttime;
        System.out.printf("Consuming messages took %d ms.\n", endtime);

        System.out.printf("%d messages not replicated.\n", jmsMsgIds.size());
    }
}
