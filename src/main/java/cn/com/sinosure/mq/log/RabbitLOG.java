package cn.com.sinosure.mq.log;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class RabbitLOG {

	 private static final String RABBIT_LOGGER_NAME = "rabbitmq";

     private static ObjectMapper mapper               = new ObjectMapper();
	 
	 
	 public static void log(String rabbitKey,String messageId,boolean isOK,Object messageBody){
		 StringBuilder builder = new StringBuilder();
		 builder.append(rabbitKey).append("|");
//		 builder.append(isPublisher).append("|");
	     builder.append(messageId).append("|");
	     builder.append(isOK).append("|");

	     if (messageBody instanceof String) {
	        builder.append(messageBody);
	     } else {
	        String dataJsonString = "";
	        try {
	            dataJsonString = mapper.writeValueAsString(messageBody);
	        } catch (JsonProcessingException e) {
	            dataJsonString = e.getMessage();
	        }
	        builder.append(dataJsonString);
	     }
		 LoggerFactory.getLogger(RABBIT_LOGGER_NAME).info(builder.toString());
	 }
}
