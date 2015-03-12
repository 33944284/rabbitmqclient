package cn.com.sinosure.mq.utils.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.com.sinosure.mq.consumer.ConsumerContainer;
import cn.com.sinosure.mq.consumer.MessageConsumer;


public abstract class MessageConsumeListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MessageConsumeListener.class);
	
	private  ConsumerContainer consumerContainer = new ConsumerContainer();

	private static WebApplicationContext appContext;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		consumerContainer.stopAllConsumers();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		
		List<String> beanNames = this.getConsumerHandlerBeanNames();
		if(beanNames == null || beanNames.isEmpty()){
			return;
		}
	
		if(appContext == null){
			appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(arg0.getServletContext());
		}
	
		for(String beanName : beanNames){
			MessageConsumer messageHandler = (MessageConsumer)appContext.getBean(beanName);
			consumerContainer.addConsumer(messageHandler);
		}
		try {
			consumerContainer.startAllConsumers();
		} catch (IOException e) {
			LOGGER.error("启动消息消费者时出错", e);
		}
	}
	/**
	 * spring bean的名称列表
	 * @return
	 */
	public abstract List<String> getConsumerHandlerBeanNames(); 

	
	

}
