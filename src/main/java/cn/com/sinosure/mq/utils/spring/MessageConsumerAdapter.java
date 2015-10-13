package cn.com.sinosure.mq.utils.spring;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import cn.com.sinosure.mq.consumer.MessageConsumer;


@Named
public abstract class MessageConsumerAdapter extends MessageConsumer implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MessageConsumerAdapter.class);
	@Inject
	@Named
	private ConsumerContainerAdapater consumerContainerAdapter;
	@Override
	public void afterPropertiesSet() throws Exception {
		
		consumerContainerAdapter.addConsumer(this);
		
		LOGGER.info("添加消费者至容器内"+this.toString()+"；isAutoAck="+this.isAutoAck());
	}

	
}
