package cn.com.sinosure.mq.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

public class AmqpMessage {

	private final Envelope envelope;
	private final AMQP.BasicProperties properties;
	private final byte[] body;

	public AmqpMessage(Envelope envelope, AMQP.BasicProperties properties,
			byte[] body) {
		this.envelope = envelope;
		this.properties = properties;
		this.body = body;
	}

	public Envelope getEnvelope() {
		return envelope;
	}

	public BasicProperties getProperties() {
		return properties;
	}

	public byte[] getBody() {
		return body;
	}

}
