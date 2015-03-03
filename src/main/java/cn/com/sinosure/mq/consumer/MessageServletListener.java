package cn.com.sinosure.mq.consumer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageServletListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MessageServletListener.class);
	ConsumerContainer consumerContainer = new ConsumerContainer();
	Map<String,MessageConsumer> consumerMap ;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		consumerContainer.stopAllConsumers();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		
		
		String key1 = "rabbit.pageTrack";//配置文件里的定义字段
		ConsumerContainer consumerContainer = new ConsumerContainer();
		if(consumerMap.isEmpty()){
			return;
		}
		Iterator<Entry<String,MessageConsumer>> iterator = consumerMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String,MessageConsumer> entry = iterator.next();
			consumerContainer.addConsumer(entry.getValue(),entry.getKey());
		}
		
		try {
			consumerContainer.startAllConsumers();
		} catch (IOException e) {
			LOGGER.error("启动消息消费者时出错", e);
		}
	}

}
