package cn.com.sinosure.mq.producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.config.MQPropertiesResolver;
import cn.com.sinosure.mq.connection.SingleConnectionFactory;

public class MessagePublisherFactory {

	private static Map<MQEnum, MessagePublisher> messagePublisherMap = new ConcurrentHashMap<MQEnum, MessagePublisher>();

	private static Map<String,SingleConnectionFactory> conFactoryMap = new ConcurrentHashMap<String,SingleConnectionFactory>();
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
				
		SingleConnectionFactory conFactory = null;
		
		if(conFactoryMap.containsKey(businessType.getVhost()+businessType.getUser())){
			conFactory = conFactoryMap.get(businessType.getVhost()+businessType.getUser());
		}
		
		if(conFactory == null){
			conFactory = new SingleConnectionFactory(businessType.getVhost(),businessType.getUser(),businessType.getPassword());
			
			conFactory.setHost(MQPropertiesResolver.getMQHost());
			
			conFactory.setPort(Integer.valueOf(MQPropertiesResolver.getMQPort()));
			
			conFactoryMap.put(businessType.getVhost()+businessType.getUser(), conFactory);
		}
		
		messagePublisherMap.put(businessType, new DefaultMessagePublisher(
				businessType, conFactoryMap.get(businessType.getVhost()+businessType.getUser())));

		return messagePublisherMap.get(businessType);
	}

}
