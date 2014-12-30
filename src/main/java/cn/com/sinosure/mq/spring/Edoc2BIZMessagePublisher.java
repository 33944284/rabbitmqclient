package cn.com.sinosure.mq.spring;

import javax.inject.Inject;
import javax.inject.Named;

import cn.com.sinosure.mq.MQTypeEnum;
import cn.com.sinosure.mq.producer.MessagePublisher;

@Named
public class Edoc2BIZMessagePublisher implements MessagePublisher {

	@Inject
	@Named("rabbitMQTemplate")
	private MessagePublisherSpringTemplate template;

	public MessagePublisherSpringTemplate getTemplate() {
		return template;
	}

	public void setTemplate(MessagePublisherSpringTemplate template) {
		this.template = template;
	}

	@Override
	public void sendMessageWithConfirm(Object messageBody) {

		this.template.sendMessage(messageBody, MQTypeEnum.EDOC2BIZ);
	}

	@Override
	public void sendMessage(Object messageBody) {
		this.template.sendMessage(messageBody, MQTypeEnum.EDOC2BIZ);
	}

}