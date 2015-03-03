package cn.com.sinosure.mq.consumer;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultMessageHandler extends MessageConsumer {
	
	private ObjectMapper objectMapper = new ObjectMapper();

	public void handleMessage(AmqpMessage message) throws IOException {
		byte[] body = message.getBody();
		String bodyJSONStr = (String) objectMapper.readValue(body, String.class);

//		throw new IOException("aaaa");
		System.out.println("json Str ===" + bodyJSONStr);
	}

}
