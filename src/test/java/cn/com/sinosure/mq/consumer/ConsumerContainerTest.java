package cn.com.sinosure.mq.consumer;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.connection.SingleConnectionFactory;
import cn.com.sinosure.mq.producer.MessagePublisher;
import cn.com.sinosure.mq.producer.MessagePublisherFactory;

import com.rabbitmq.client.Connection;


public class ConsumerContainerTest {

    private static Logger LOGGER = LoggerFactory.getLogger(ConsumerContainerTest.class);
    
    private static final int MESSAGE_AMOUNT = 1000;
    
     private MessagePublisher publisher;
    private SingleConnectionFactory connectionFactory;
    
    @Before
    public void before() throws Exception {
//        brokerSetup = new TestBrokerSetup();
        
        connectionFactory = new SingleConnectionFactory(MQEnum.EDOC2RBAC);
        connectionFactory.setHost("10.1.95.144");
        connectionFactory.setPort(5670);
        publisher = MessagePublisherFactory.getMessagePublisher(MQEnum.EDOC2RBAC);
    }
    
    @After
    public void after() throws Exception {
//        brokerSetup.tearDown();
        connectionFactory.close();
    }
    
    @Test
    public void shouldReturnSameConnection() throws Exception {
        Connection connectionOne = connectionFactory.newConnection();
        Connection connectionTwo = connectionFactory.newConnection();
        Assert.assertTrue(connectionOne == connectionTwo);
    }
    
   
    
    
    @Test
    public void shouldActivateAllConsumers() throws Exception {
//     
//        ConsumerContainer consumerContainer = prepareConsumerContainer(
//            new DefaultMessageHandler(), "edoc2rbac");
        ConsumerContainer consumerContainer = new ConsumerContainer(connectionFactory);
        consumerContainer.addConsumer( new DefaultMessageHandler(), MQEnum.EDOC2RBAC);
        consumerContainer.startAllConsumers();
        Thread.sleep(1000);
        int activeConsumerCount = consumerContainer.getActiveConsumers().size();
        Assert.assertEquals(1, activeConsumerCount);
       
    }
    
    @Test
    public void shouldReActivateAllConsumers() throws Exception {
    
        ConsumerContainer consumerContainer = prepareConsumerContainer(
            new DefaultMessageHandler(), MQEnum.EDOC2RBAC);
        consumerContainer.startAllConsumers();
        Thread.sleep(1000);
        int activeConsumerCount = consumerContainer.getActiveConsumers().size();
        Assert.assertEquals(1, activeConsumerCount);
        Connection connection = connectionFactory.newConnection();
        connectionFactory.setPort(15670);
        connection.close();
        int waitForReconnects = SingleConnectionFactory.CONNECTION_ESTABLISH_INTERVAL_IN_MS + SingleConnectionFactory.CONNECTION_TIMEOUT_IN_MS  * 2;
        Thread.sleep(waitForReconnects);
        connectionFactory.setPort(5670);
        Thread.sleep(waitForReconnects);
        activeConsumerCount = consumerContainer.getActiveConsumers().size();
        Assert.assertEquals(1, activeConsumerCount);
    }
    
    @Test
    public void shouldReceiveAllMessages() throws Exception {
       
        MessageConsumer consumer1 = new DefaultMessageHandler();
        MessageConsumer consumer2 = new DefaultMessageHandler();
        MessageConsumer consumer3 = new DefaultMessageHandler();
        ConsumerContainer consumerContainer = prepareConsumerContainer(consumer1, MQEnum.EDOC2RBAC, 100);
        consumerContainer.addConsumer(consumer2, MQEnum.EDOC2RBAC.getTargetQueue(), 100,  1);
        consumerContainer.addConsumer(consumer3,MQEnum.EDOC2RBAC.getTargetQueue(), 100,  1);
        //        ConsumerContainer consumerContainer = prepareConsumerContainer(consumer2, "edoc2rbac", 1000);
        consumerContainer.startAllConsumers();
//        Thread.sleep(1000);
        for (int i=1; i<=MESSAGE_AMOUNT; i++) {
           
            publisher.sendMessage(i);
        }
        // Sleep depending on the amount of messages sent but at least 100 ms, and at most 1 sec
        Thread.sleep(Math.max(10000, Math.min(10000, MESSAGE_AMOUNT * 10)));
//        List<Message> receivedMessages = testConsumer.getReceivedMessages();
//        Assert.assertEquals(MESSAGE_AMOUNT, receivedMessages.size());
//        for (int i=1; i<=MESSAGE_AMOUNT; i++) {
//            Message receivedMessage = receivedMessages.get(i-1);
//            Assert.assertNotNull(receivedMessage);
//            Assert.assertEquals(i, (int)receivedMessage.getBodyAs(Integer.class));
//        }
    }

    @Test
    public void shouldReceiveAllMessagesWithLimitedPrefetchCount() throws Exception {
      
//        MessageConsumer consumer1 = new DefaultMessageHandler();
//        ConsumerContainer consumerContainer = prepareConsumerContainer(testConsumer, "edoc2rbac", 10);
//        consumerContainer.startAllConsumers();
//        for (int i=1; i<=MESSAGE_AMOUNT; i++) {
//            Message message = new Message()
//                    .exchange(TestBrokerSetup.TEST_EXCHANGE)
//                    .routingKey(TestBrokerSetup.TEST_ROUTING_KEY)
//                    .body("" + i);
//            publisher.publish(message);
//        }
//        // Sleep depending on the amount of messages sent but at least 100 ms, and at most 1 sec
//        Thread.sleep(Math.max(100, Math.min(1000, MESSAGE_AMOUNT * 10)));
//        List<Message> receivedMessages = testConsumer.getReceivedMessages();
//        Assert.assertEquals(MESSAGE_AMOUNT, receivedMessages.size());
//        for (int i=1; i<=MESSAGE_AMOUNT; i++) {
//            Message receivedMessage = receivedMessages.get(i-1);
//            Assert.assertNotNull(receivedMessage);
//            Assert.assertEquals(i, (int)receivedMessage.getBodyAs(Integer.class));
//        }
    }

    @Test(expected = IOException.class)
    public void shouldFailToStartConsumers() throws Exception {
     
        MessageConsumer consumer1 = new DefaultMessageHandler();
        ConsumerContainer consumerContainer = prepareConsumerContainer(consumer1, "edoc2rbac1");
        consumerContainer.startAllConsumers();
    }
    
    @Test
    public void shouldActivateConsumersUsingHighAvailability() throws Exception {
       
        MessageConsumer consumer1 = new DefaultMessageHandler();
        ConsumerContainer consumerContainer = prepareConsumerContainer(consumer1, MQEnum.EDOC2RBAC);
        consumerContainer.startAllConsumers();
        Thread.sleep(1000);
        int activeConsumerCount = consumerContainer.getActiveConsumers().size();
        Assert.assertEquals(1, activeConsumerCount);
    }
    
    private ConsumerContainer prepareConsumerContainer(MessageConsumer consumer, String queue) {
        ConsumerContainer consumerContainer = new ConsumerContainer(connectionFactory);
        consumerContainer.addConsumer(consumer, queue);
        return consumerContainer;
    }

    private ConsumerContainer prepareConsumerContainer(MessageConsumer consumer, MQEnum businessType) {
        ConsumerContainer consumerContainer = new ConsumerContainer(connectionFactory);
        consumerContainer.addConsumer(consumer, businessType.getTargetQueue());
        return consumerContainer;
    }
    
    private ConsumerContainer prepareConsumerContainer(MessageConsumer consumer, MQEnum businessType, int prefetchMessageCount) {
        ConsumerContainer consumerContainer = new ConsumerContainer(connectionFactory);
        consumerContainer.addConsumer(consumer, businessType.getTargetQueue(), prefetchMessageCount, 1);
        return consumerContainer;
    }
    private ConsumerContainer prepareConsumerContainer(MessageConsumer consumer, String queue, int prefetchMessageCount) {
        ConsumerContainer consumerContainer = new ConsumerContainer(connectionFactory);
        consumerContainer.addConsumer(consumer, queue, prefetchMessageCount, 1);
        return consumerContainer;
    }

}
