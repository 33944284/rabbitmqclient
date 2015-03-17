package cn.com.sinosure.mq.utils.spring;

import java.io.IOException;

import javax.inject.Named;

import cn.com.sinosure.mq.consumer.ConsumerContainer;
import cn.com.sinosure.mq.consumer.MessageConsumer;

@Named
public class ConsumerContainerAdapater {
	
	
	private ConsumerContainer consumerContainer ;
	
	public ConsumerContainerAdapater(){
		this.consumerContainer = new ConsumerContainer();
	}
	
	
	/**
	 * 增加消费者
	 * @param consumer
	 */
	public void addConsumer(MessageConsumer consumer){
		consumerContainer.addConsumer(consumer);
	}
	
	/**
	 * 启动消费者
	 * @throws IOException
	 */
	public void start() throws IOException{
		consumerContainer.startAllConsumers();
	}
	
	/**
	 * 关闭消费者
	 */
	public void stop(){
		consumerContainer.stopAllConsumers();
	}

}
