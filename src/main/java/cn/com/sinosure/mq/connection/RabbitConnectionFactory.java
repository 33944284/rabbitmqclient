package cn.com.sinosure.mq.connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.com.sinosure.mq.MQEnum;
import cn.com.sinosure.mq.config.MQPropertiesResolver;

public class RabbitConnectionFactory {

	private static Map<String,SingleConnectionFactory> conFactoryMap = new ConcurrentHashMap<String,SingleConnectionFactory>();

	public synchronized static SingleConnectionFactory getConnectionFactory(String vhost,String user,String password){
		
		SingleConnectionFactory conFactory = null;
		
		if(conFactoryMap.containsKey(vhost+user)){
			conFactory = conFactoryMap.get(vhost+user);
		}
		
		if(conFactory == null){
			conFactory = new SingleConnectionFactory(MQPropertiesResolver.getMQHost(),Integer.valueOf(MQPropertiesResolver.getMQPort()),vhost,user,password);
			conFactoryMap.put(vhost+user, conFactory);
		}
		return conFactory;
	}
	
	public synchronized static SingleConnectionFactory getConnectionFactory(String key){
		
		MQEnum mqEnum = MQPropertiesResolver.getMQProperties(key);
		return getConnectionFactory(mqEnum.getVhost(),mqEnum.getUser(),mqEnum.getPassword());
	}
}
