package cn.com.sinosure.mq.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class ReceiveLogsDirect2 {

	private static final String EXCHANGE_NAME = "direct_logsTopic2";

	public static void main(String[] argv) throws java.io.IOException,
			java.lang.InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("10.1.95.144");
		factory.setPort(5670);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		String queueName = "testExTopic-1";// channel.queueDeclare().getQueue();

		channel.queueBind("testExTopic-1", "direct_logsTopic2", "error");
		// if (argv.length < 1){
		// System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
		// System.exit(1);
		// }

		// for(String severity : argv){
		// channel.queueBind(queueName, EXCHANGE_NAME, severity);
		// }

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			String routingKey = delivery.getEnvelope().getRoutingKey();

			System.out.println(" [x] Received ERROR'" + routingKey + "':'"
					+ message + "'");
		}
	}
}
