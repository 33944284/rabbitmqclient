package cn.com.sinosure.mq.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

public class AmqpMessage {

	private final AMQP.BasicProperties properties;
	private final byte[] body;
	private final String exchange;
	private final String routingKey;

	public AmqpMessage(String exchange, String routingKey,
			AMQP.BasicProperties properties, byte[] body) {
		this.exchange = exchange;
		this.routingKey = routingKey;
		this.properties = properties;
		this.body = body;
	}

	public BasicProperties getProperties() {
		return properties;
	}

	public byte[] getBody() {
		return body;
	}

	public String getExchange() {
		return exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}
}
