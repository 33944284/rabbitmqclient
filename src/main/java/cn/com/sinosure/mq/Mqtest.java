package cn.com.sinosure.mq;

import java.util.Date;
import java.util.UUID;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Mqtest {

	public static void main(String[] args) {
		ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.113");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("qiurunze");
        connectionFactory.setPassword("123456");
        Connection conn =null;
        Channel producerChannel = null;
        try {
            conn = connectionFactory.newConnection();
            producerChannel = conn.createChannel();
            // 消息的唯一编号
            String uuid = UUID.randomUUID().toString();
            String message = uuid + ";time=" + new Date().getTime();
            // 设置一些参数
            BasicProperties properties = new BasicProperties().builder().type("String").contentType("text")
                    .contentEncoding("UTF-8").messageId(uuid).build();
                // 第一个参数是exchange交换器的名字
                // 第二个参数是进行消息路由的关键key
                producerChannel.basicPublish("test_topic", "a.topic.pppp", properties, message.getBytes());
        } catch (Exception e) {
        	System.out.println(e);
            System.exit(-1);
        }
	}
}
