package cn.com.sinosure.mq.demo;

import java.io.IOException;

import javax.inject.Named;

import cn.com.sinosure.mq.consumer.AmqpMessage;
import cn.com.sinosure.mq.consumer.MessageConsumer;


@Named //非spring环境可以不用加入该注释
public class DefaultMessageHandler extends MessageConsumer {
	
	@Override
	public void handleMessage(AmqpMessage message) throws IOException {

		String bodyJSONStr = (String) message.getMessageBodyObject(String.class);

		System.out.println("json Str ===" + bodyJSONStr);
	}

	@Override
	public String getRabbitKey() {
		// TODO Auto-generated method stub
		return "rabbit.edoc-biz";
	}

}
