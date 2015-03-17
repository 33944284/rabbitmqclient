package cn.com.sinosure.mq.utils.spring;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Named
public class RabbitMessageListenerContainer implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RabbitMessageListenerContainer.class);
	@Inject
	@Named
	private ConsumerContainerAdapater consumerContainerAdapter;
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.debug("hhahaha====="+event.toString());

		if(event.getApplicationContext().getParent() == null){
			LOGGER.info("启动消费者-----begin");
			try {
				consumerContainerAdapter.start();
			} catch (IOException e) {
				LOGGER.error("启动消费者时出现错误", e);
			}
			LOGGER.info("启动消费者成功-----end");
		}
		
	}
	
}
