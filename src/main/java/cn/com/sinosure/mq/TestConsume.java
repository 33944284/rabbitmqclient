package cn.com.sinosure.mq;

import java.io.IOException;

import cn.com.sinosure.mq.consumer.AmqpMessage;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

public class TestConsume {
	public static void main(String[] args) {
		TestConsume kk = new  TestConsume();
	ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("192.168.1.113");
    connectionFactory.setPort(5672);
    connectionFactory.setUsername("qiurunze");
    connectionFactory.setPassword("123456");
    Connection conn =null;
    Channel consumerChannel = null;
    try {
        conn = connectionFactory.newConnection();
        consumerChannel = conn.createChannel();
     // 开始监控消息，（ack是手动的）
        QueueingConsumer queueingConsumer = null;
        try {
            queueingConsumer = new QueueingConsumer(consumerChannel);
            // 设置消费者订阅的消息队列名：test_queues
            consumerChannel.queueDeclare("test_queues", false, false, false, null);  
            
            System.out.println("等待消息产生：");  

            /* 创建消费者对象，用于读取消息 */  
            QueueingConsumer consumer = new QueueingConsumer(consumerChannel);  
            consumerChannel.basicConsume("test_queue", true, consumer);  
            int i=1;
            /* 读取队列，并且阻塞，即在读到消息之前在这里阻塞，直到等到消息，完成消息的阅读后，继续阻塞循环 */  
            while (true) {  
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();  
                String message = new String(delivery.getBody());  
                System.out.println("第"+i+"个消息！");
                System.out.println("收到消息'" + message + "'");  
                i++;
                Thread.sleep(1000*5);
            }  
        } catch (IOException e) {
            System.exit(-1);
        }
        // 消息的唯一编号
    } catch (Exception e) {
    	System.out.println(e);
        System.exit(-1);
    }
	}
	
	   public void handleDelivery(String consumerTag , Envelope envelope,
				BasicProperties properties, byte[] body) throws IOException {
		AmqpMessage message =
				new AmqpMessage("test_exchange", "test_routingKey", properties, body);
		
		System.out.println(message);

	    }
    
}
