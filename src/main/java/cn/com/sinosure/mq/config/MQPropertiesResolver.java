package cn.com.sinosure.mq.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.com.sinosure.mq.MQEnum;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MQPropertiesResolver {
	
	private static Map<String,MQEnum> mqEnumMap = new HashMap<String,MQEnum>();
	
	private static Properties properties = new Properties() ;
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	private static final String location = "config-mq.properties";
	
	private static void loadConfig() throws IOException{
		InputStream inputStream = MQPropertiesResolver.class.getClassLoader().getResourceAsStream(location);
		properties.load(inputStream);
	}
	
	public static synchronized  MQEnum getMQProperties(String key){
		if(mqEnumMap.containsKey(key)){
			return mqEnumMap.get(key);
		}
		MQEnum type = null;
		try {
			if(properties.isEmpty()){
				loadConfig();
			}
			String value = (String) properties.get(key);
			type  = objectMapper.readValue(value, MQEnum.class);

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mqEnumMap.put(key, type);
		return mqEnumMap.get(key);
	}

	public static synchronized  String getMQHost(){
		if(properties.isEmpty()){
			try {
				loadConfig();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return properties.getProperty("rabbit.host");
	}
	
	public static synchronized  String getMQPort(){
		if(properties.isEmpty()){
			try {
				loadConfig();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return properties.getProperty("rabbit.port");
	}
	
	public static void main(String[] args){
//		MQPropertiesResolver instance = new MQPropertiesResolver();
		System.out.println("=="+MQPropertiesResolver.getMQProperties("rabbit.rbac-biz").getExchange());
	}
}
