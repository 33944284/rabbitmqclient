package cn.com.sinosure.mq.spring;

import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.context.annotation.Scope;

import cn.com.sinosure.mq.MQTypeEnum;

@Scope("prototype")
public class MessagePublisherSpringTemplate {

	private static final Logger logger = LoggerFactory
			.getLogger(MessagePublisherSpringTemplate.class);

	@Inject
	private RabbitTemplate rabbitTemplate;

	public RabbitTemplate getRabbitTemplate() {
		return rabbitTemplate;
	}

	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	private boolean isAcl = false;

	public void sendMessage(final Object messageBody, final MQTypeEnum type) {

		if (!isAcl) {
			this.initACL(type);
			isAcl = true;
		}
		this.rabbitTemplate.setConfirmCallback(new ConfirmCallback() {

			@Override
			public void confirm(CorrelationData correlationData, boolean ack,
					String cause) {
				if (!ack) {
					logger.debug("message id " + correlationData.getId()
							+ " is sended failure!");

				}
			}

		});

		final String messageId = UUID.randomUUID().toString();

		this.rabbitTemplate.convertAndSend(type.getExchange(),
				type.getRoutingKey(), messageBody, new MessagePostProcessor() {

					@Override
					public Message postProcessMessage(Message message)
							throws AmqpException {

						message.getMessageProperties().setMessageId(messageId);
						message.getMessageProperties().setType(type.toString());
						return message;
					}

				}, new CorrelationData(messageId));

	}

	protected void initACL(final MQTypeEnum type) {
		CachingConnectionFactory conFactory = ((CachingConnectionFactory) this.rabbitTemplate
				.getConnectionFactory());
		conFactory.setUsername(type.getUser());
		conFactory.setPassword(type.getPassword());
		conFactory.setVirtualHost(type.getVhost());
	}

}
