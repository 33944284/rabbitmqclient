package cn.com.sinosure.mq.consumer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

public class AmqpMessage {

	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
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

	public <T> T getMessageBodyObject(Class<T> clazz){
		try {
			return objectMapper.readValue(body, clazz);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public String getExchange() {
		return exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	
}
