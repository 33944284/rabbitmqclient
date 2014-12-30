package cn.com.sinosure.mq.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogDirect {
	private static final String EXCHANGE_NAME = "direct_logsTopic";

	public static void main(String[] argv) throws java.io.IOException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("10.1.95.65");
		factory.setPort(5672);
		factory.setUsername("edoc-rbac");
		factory.setPassword("edoc-rbac");
		factory.setVirtualHost("EDOC-RBAC");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		// channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		// channel.queueDeclare("testExTopic-1", true, false, false, null);
		// // channel.queueDeclare("testExTopic-2", true, false, false, null);
		//
		// channel.queueBind("testExTopic-1", EXCHANGE_NAME, "info");
		// channel.queueBind("testExTopic-1", EXCHANGE_NAME, "error");
		// channel.queueBind("testExTopic-2", EXCHANGE_NAME, "error");

		// channel.ex
		String severity = "info";
		String message = "hello ttttttt";

		channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes());
		System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
		channel.basicPublish(EXCHANGE_NAME, "error", null,
				"woaibjtam".getBytes());
		System.out.println(" [x] Sent ' error  ':'woaibjtam'");
		channel.basicPublish(EXCHANGE_NAME, "error", null,
				"woaibjtam2".getBytes());
		System.out.println(" [x] Sent ' error  ':'woaibjtam2'");
		channel.basicPublish(EXCHANGE_NAME, "error", null,
				"woaibjtam3".getBytes());
		System.out.println(" [x] Sent ' error  ':'woaibjtam3'");
		channel.basicPublish(EXCHANGE_NAME, "error", null,
				"woaibjtam4".getBytes());
		System.out.println(" [x] Sent ' error  ':'woaibjtam4'");
		channel.close();
		connection.close();
	}

}
