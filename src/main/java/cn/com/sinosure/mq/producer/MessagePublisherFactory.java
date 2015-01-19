package cn.com.sinosure.mq.producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.connection.RabbitConnectionFactoryUtil;
import cn.com.sinosure.mq.connection.SingleConnectionFactory;

public class MessagePublisherFactory {

	private static Map<MQEnum, MessagePublisher> messagePublisherMap = new ConcurrentHashMap<MQEnum, MessagePublisher>();

	/**
	 * 创建消息发送器
	 * 
	 * @param businessType
	 *            EDOC2BIZ
	 * @return
	 */
	public static synchronized MessagePublisher getMessagePublisher(
			MQEnum businessType) {
		if (messagePublisherMap.containsKey(businessType)) {
			return messagePublisherMap.get(businessType);
		}
				
		SingleConnectionFactory conFactory = RabbitConnectionFactoryUtil.getConnectionFactory(businessType.getVhost(), businessType.getUser(), businessType.getPassword());
		
		messagePublisherMap.put(businessType, new DefaultMessagePublisher(
				businessType,conFactory));

		return messagePublisherMap.get(businessType);
	}

}
