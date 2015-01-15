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
import org.springframework.util.StringUtils;

import cn.com.sinosure.mq.MQEnum;

@Scope("prototype")
public class MessagePublisherSpringTemplate {

	private static final Logger logger = LoggerFactory
			.getLogger(MessagePublisherSpringTemplate.class);

	//默认路由关键字
	private final String DEFAULT_ROUTING_KEY = "";
	@Inject
	private RabbitTemplate rabbitTemplate;

	public RabbitTemplate getRabbitTemplate() {
		return rabbitTemplate;
	}

	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	private boolean isAcl = false;

	public void sendMessage(final Object messageBody, final MQEnum type) {

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

		String routingKey = null;
		
		if(StringUtils.isEmpty(type.getRoutingKey())){
			routingKey = DEFAULT_ROUTING_KEY;
		}else{
			routingKey = type.getRoutingKey();
		}
		this.rabbitTemplate.convertAndSend(type.getExchange(),
				routingKey, messageBody, new MessagePostProcessor() {

					@Override
					public Message postProcessMessage(Message message)
							throws AmqpException {
						message.getMessageProperties().setMessageId(messageId);
						return message;
					}

				}, new CorrelationData(messageId));

	}

	protected void initACL(final MQEnum type) {
		CachingConnectionFactory conFactory = ((CachingConnectionFactory) this.rabbitTemplate
				.getConnectionFactory());
		conFactory.setUsername(type.getUser());
		conFactory.setPassword(type.getPassword());
		conFactory.setVirtualHost(type.getVhost());
	}

}
