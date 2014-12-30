package cn.com.sinosure.mq.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.rabbitmq.client.Channel;

public class MessageManualAckDispatcher implements ChannelAwareMessageListener {

	private static final Logger logger = LoggerFactory
			.getLogger(MessageManualAckDispatcher.class);

	private final Set<String> received = Collections
			.synchronizedSet(new HashSet<String>());

	private static Map<String, Object> invokedObjectMap = new HashMap<String, Object>();

	private static Map<String, Method> invokedMethodMap = new HashMap<String, Method>();

	// @Value("#{'${mq.handlers}'.split(';')}")
	private List<String> invokedTarget;//

	public void init() {

		invokedTarget = new ArrayList<String>();
		invokedTarget.add("test-cn.com.sinosure.common.demo.mq.Foo-listen");
		for (String target : invokedTarget) {
			String[] args = target.split("-");
			String key = args[0].trim();
			String invokedClass = args[1].trim();
			String invokedMethod = args[2].trim();
			try {
				Class clazz = Class.forName(invokedClass);
				Object instance = clazz.newInstance();
				invokedObjectMap.put(key, instance);
				Method method = clazz.getMethod(invokedMethod, String.class);
				invokedMethodMap.put(key, method);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {

		// 完成后，向MQ发送ack，确认消息已收到
		try {

			MessageProperties messageProperties = message
					.getMessageProperties();
			// Map<String,Object> messageHeader =
			// messageProperties.getHeaders();
			String businessType = (String) messageProperties.getType();
			// 业务处理
			invokedMethodMap.get(businessType).invoke(
					invokedObjectMap.get(businessType),
					String.valueOf(message.getBody()));
			logger.info("Acking: "
					+ message.getMessageProperties().getMessageId());
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),
					false);

		} finally {
			if (!this.received.add(message.getMessageProperties()
					.getMessageId())) {
				logger.info(message.getMessageProperties().getMessageId()
						+ " already received, redelivered="
						+ message.getMessageProperties().isRedelivered());
			}
		}
	}

}
