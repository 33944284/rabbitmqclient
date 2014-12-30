package cn.com.sinosure.mq.spring;

import javax.inject.Inject;
import javax.inject.Named;

import cn.com.sinosure.mq.MQTypeEnum;
import cn.com.sinosure.mq.producer.MessagePublisher;

@Named
public class MessagePublisherSpringFactory {

	@Inject
	@Named("edoc2BIZMessagePublisher")
	MessagePublisher edoc2Biz;

	public MessagePublisher getMessagePublisher(MQTypeEnum businessType) {
		if (businessType == MQTypeEnum.EDOC2BIZ) {
			return edoc2Biz;
		} else {
			return null;
		}
	}

}
