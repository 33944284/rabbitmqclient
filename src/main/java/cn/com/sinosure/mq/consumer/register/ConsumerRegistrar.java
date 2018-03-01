package cn.com.sinosure.mq.consumer.register;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import cn.com.sinosure.mq.consumer.ConsumerContainer;
import cn.com.sinosure.mq.consumer.MessageConsumer;

/**
 * 消费者注册器，为 BIZ5 写的 MQ 消费者注册方案。<br >
 *
 * BIZ5 系统只要在 spring 中配置本注册器，即可应用此方案：
 * <pre> &lt;bean class="cn.com.sinosure.mq.consumer.registe.ConsumerRegistrar"&gt;&lt;/bean&gt; </pre>
 * 该类会监听 spring 容器的关键事件：
 * <li>容器初始化完成时，自动注册 MQ 消费者；
 * <li>容器销毁时，自动注销消费者。<br ><br >
 * 被注册的消费者，也必须受 spring 管理，且位于 spring 根容器中。<br ><br >
 *
 * 注意，BIZ6 环境下有专门的消费者注册方案，不要配置这个类，
 * 否则可能会导致消费者重复注册。
 *
 * @author sinosure
 *
 */
public class ConsumerRegistrar implements ApplicationListener<ApplicationEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerRegistrar.class);

	private ConsumerContainer consumerContainer = new ConsumerContainer();

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		if(event instanceof ContextRefreshedEvent){

			ContextRefreshedEvent refreshedEvent = (ContextRefreshedEvent)event;
			ApplicationContext context = refreshedEvent.getApplicationContext();
			LOGGER.info("mq.consumer.registrar: <" + context.getDisplayName() + "> Refreshed.");

			if(context.getParent() == null){
				LOGGER.info("mq.consumer.registrar: Start registering consumers...");
				registeConsumer(context);
			}

		}else if(event instanceof ContextClosedEvent){

			ContextClosedEvent closedEvent = (ContextClosedEvent)event;
			ApplicationContext context = closedEvent.getApplicationContext();
			LOGGER.info("mq.consumer.registrar: <" + context.getDisplayName() + "> closed.");

			if(context.getParent() == null){
				consumerContainer.stopAllConsumers();
				LOGGER.info("mq.consumer.registrar: Stop all consumers.");
			}

		}
	}

	private void registeConsumer(ApplicationContext context){
		Map<String, MessageConsumer> consumers = context.getBeansOfType(MessageConsumer.class);
		for (String key : consumers.keySet()) {
			MessageConsumer consumer = consumers.get(key);
			LOGGER.info("mq.consumer.registrar: adding consumer[" + key + ", " + consumer.getRabbitKey() + "]...");
			consumerContainer.addConsumer(consumer);
		}
		try {
			consumerContainer.startAllConsumers();
			LOGGER.info("mq.consumer.registrar: All consumers started! ");
		} catch (IOException e) {

			LOGGER.error("mq.consumer.registrar: Start consumers failed! ", e);
			throw new RuntimeException("mq.consumer.registrar: Start consumers failed! ", e);
		}
	}

}
