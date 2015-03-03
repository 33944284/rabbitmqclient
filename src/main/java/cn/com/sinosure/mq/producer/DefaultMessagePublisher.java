package cn.com.sinosure.mq.producer;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.sinosure.mq.MQException;
import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.log.RabbitLOG;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class DefaultMessagePublisher implements MessagePublisher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultMessagePublisher.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	private ConnectionFactory conFactory;
	
	private MQEnum businessType;

	//默认路由关键字
	private final String DEFAULT_ROUTING_KEY = "";
	
	//默认消息路由器
	private final String DEFAULT_EXCHANGE = "";
	
//	private String routingKey = "";
	
	public DefaultMessagePublisher(MQEnum businessType,
			ConnectionFactory conFactory) {
		this.businessType = businessType;
		this.conFactory = conFactory;
	}
	
	public DefaultMessagePublisher(ConnectionFactory conFactory) {
		this.conFactory = conFactory;
	}

	@Override
	public void sendMessageWithConfirm(Object messageBody) throws MQException {
		this.sendMessageWithConfirm(businessType.getRoutingKey(), messageBody);
	}

	@Override
	public void sendMessage(Object messageBody) throws MQException {
		this.sendMessage(businessType.getRoutingKey(), messageBody);
	}

	private String sendMessage(String routingKey,Object messageBody, Channel channel)
			throws IOException {
	
		String messageJson = objectMapper.writeValueAsString(messageBody);

		String messageId = UUID.randomUUID().toString();
		
		BasicProperties.Builder propsBuilder = new BasicProperties().builder()
				.contentType(businessType.getRabbitKey())// 
				.deliveryMode(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode())//持久化消息
				.messageId(messageId);// 

		
		if(routingKey == null || routingKey.equals("") || routingKey.equals("null") ){
			routingKey = DEFAULT_ROUTING_KEY;
		}
		
		channel.basicPublish(businessType.getExchange(),
				routingKey, propsBuilder.build(),
				messageJson.getBytes("UTF-8"));

		return messageId;
	}
	
	


	protected Channel createChannel() throws IOException {
		LOGGER.info("Creating channel");
		Connection connection = this.conFactory.newConnection();
		Channel channel = connection.createChannel();
		LOGGER.info("Created channel"+channel.getChannelNumber());
		return channel;
	}

	@Override
	public void sendMessageWithConfirm(String routingKey, Object messageBody)
			throws MQException {
		Channel channel = null;
		try {
			channel = this.createChannel();
			channel.confirmSelect();
			String messageId = this.sendMessage(routingKey,messageBody,channel);
			channel.waitForConfirmsOrDie();
			RabbitLOG.log(this.businessType.getRabbitKey(), messageId, true, messageBody);
		} catch (InterruptedException e) {
			RabbitLOG.log(this.businessType.getRabbitKey(), "", false, messageBody);
			throw new MQException("发送后,等待确认消息时报错", e);
		} catch (IOException e) {
			throw new MQException("发送前，设置confirm监听器时出错", e);
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				LOGGER.error("关闭channel时出现错误", e);
			}
		}

		
	}

	@Override
	public void sendMessage(String routingKey, Object messageBody)
			throws MQException {
		

		Channel channel = null;
		try {
			channel = this.createChannel();
			String messageId  = this.sendMessage(routingKey,messageBody, channel);
			RabbitLOG.log(this.businessType.getRabbitKey(), messageId, true, messageBody);
		} catch (IOException e) {
			RabbitLOG.log(this.businessType.getRabbitKey(), "", false, messageBody);
			throw new MQException("发送消息时出现错误", e);
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				LOGGER.error("关闭channel时出现错误", e);
			}
		}
		
	}

	

}
