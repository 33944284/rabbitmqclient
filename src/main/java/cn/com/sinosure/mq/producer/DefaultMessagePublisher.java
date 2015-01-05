package cn.com.sinosure.mq.producer;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.sinosure.mq.MQException;
import cn.com.sinosure.mq.MQEnum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class DefaultMessagePublisher implements MessagePublisher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultMessagePublisher.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	private ConnectionFactory conFactory;
	private MQEnum businessType;

	public DefaultMessagePublisher(MQEnum businessType,
			ConnectionFactory conFactory) {
		this.businessType = businessType;
		this.conFactory = conFactory;
	}

	@Override
	public void sendMessageWithConfirm(Object messageBody) throws MQException {
		// TODO Auto-generated method stub

		Channel channel = null;
		try {
			channel = this.createChannel();
			channel.confirmSelect();
			this.sendMessage(messageBody);
			channel.waitForConfirmsOrDie();
		} catch (InterruptedException e) {
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
	public void sendMessage(Object messageBody) throws MQException {

		Channel channel = null;
		try {
			channel = this.createChannel();
			this.sendMessage(messageBody, channel);
		} catch (IOException e) {
			throw new MQException("发送消息时出现错误", e);
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				LOGGER.error("关闭channel时出现错误", e);
			}
		}

	}

	private void sendMessage(Object messageBody, Channel channel)
			throws IOException {
		String messageJson = objectMapper.writeValueAsString(messageBody);

		BasicProperties.Builder propsBuilder = new BasicProperties().builder()
				.contentType(businessType.toString())//  
				.messageId(UUID.randomUUID().toString());// 

		channel.basicPublish(businessType.getExchange(),
				businessType.getRoutingKey(), propsBuilder.build(),
				messageJson.getBytes());

	}

	protected Channel createChannel() throws IOException {
		LOGGER.debug("Creating channel");
		Connection connection = this.conFactory.newConnection();
		Channel channel = connection.createChannel();
		LOGGER.debug("Created channel");
		return channel;
	}

}
