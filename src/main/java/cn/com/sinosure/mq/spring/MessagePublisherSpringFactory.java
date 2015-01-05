package cn.com.sinosure.mq.spring;

import javax.inject.Inject;
import javax.inject.Named;

import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.producer.MessagePublisher;

@Named
public class MessagePublisherSpringFactory {

	@Inject
	@Named("edoc2BIZMessagePublisher")
	MessagePublisher edoc2Biz;

	public MessagePublisher getMessagePublisher(MQEnum businessType) {
		if (businessType == MQEnum.EDOC2BIZ) {
			return edoc2Biz;
		} else {
			return null;
		}
	}

}
