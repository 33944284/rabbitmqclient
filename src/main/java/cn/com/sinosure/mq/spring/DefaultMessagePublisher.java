package cn.com.sinosure.mq.spring;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.MQException;
import cn.com.sinosure.mq.producer.MessagePublisher;

@Named
@Scope("prototype")
public class DefaultMessagePublisher implements MessagePublisher {

	
	private MQEnum type;
	public MQEnum getType() {
		return type;
	}

	public void setType(MQEnum type) {
		this.type = type;
	}

	@Autowired
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

		this.sendMessage(messageBody);
	}

	@Override
	public void sendMessage(Object messageBody) {
		this.template.sendMessage(messageBody, type);
	}

	@Override
	public void sendMessageWithConfirm(String routingKey, Object messageBody)
			throws MQException {
		this.sendMessage(routingKey,messageBody);
		
	}

	@Override
	public void sendMessage(String routingKey, Object messageBody)
			throws MQException {

		type.setRoutingKey(routingKey);
		this.template.sendMessage(messageBody, type);
		
	}

}