package cn.com.sinosure.mq.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;

import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.producer.MessagePublisher;

@Named
public class MessagePublisherSpringFactory {

	private static Map<MQEnum, MessagePublisher> messagePublisherMap = new ConcurrentHashMap<MQEnum, MessagePublisher>();

	@Inject
	@Named("springContextUtil")
	SpringContextUtil  springContextUtil;

	public MessagePublisher getMessagePublisher(MQEnum businessType) {
		if (messagePublisherMap.containsKey(businessType)) {
			return messagePublisherMap.get(businessType);
		}
		MessagePublisher messagePublisher = (MessagePublisher) springContextUtil.getBean("defaultMessagePublisher");
		((DefaultMessagePublisher)messagePublisher).setType(businessType);
		messagePublisherMap.put(businessType,messagePublisher);

		return messagePublisherMap.get(businessType);
	}

}
