package cn.com.sinosure.mq.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.sinosure.mq.MQEnum;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MQPropertiesResolver {
	
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MQPropertiesResolver.class);

	private static Map<String,MQEnum> mqEnumMap = new HashMap<String,MQEnum>();
	
	private static Properties properties = new Properties() ;
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	private static final String location = "config-mq.properties";

	private static final String host_location = "host-mq.properties";

	
	private static void loadConfig() throws IOException{
		
		InputStream inputStream = MQPropertiesResolver.class.getClassLoader().getResourceAsStream(location);
		properties.load(inputStream);
		
		if(inputStream == null){
			inputStream = MQPropertiesResolver.class.getClassLoader().getResourceAsStream("WEB-INF/"+location);
		}
		
		if(inputStream == null){
			String path = MQPropertiesResolver.class.getResource("/").getPath();
			LOGGER.debug("path1==="+path);
			path = path.substring(1,path.indexOf("classes"));
			LOGGER.debug("path2==="+path);
			properties.load(new FileInputStream(path+location));
		}

		LOGGER.debug("properties1=="+properties.toString());
		//加载目标主机，将从客户端收回到mq端统一管理
		inputStream  = MQPropertiesResolver.class.getResourceAsStream(host_location);
		properties.load(inputStream);
		LOGGER.debug("properties2=="+properties.toString());
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
			System.out.println("rabbit.key=="+key);
			String value = (String) properties.get(key);
			
			type  = objectMapper.readValue(value, MQEnum.class);

			type.setRabbitKey(key);
		} catch (JsonParseException e) {
			LOGGER.error("解析配置文件时出现错误", e);
		} catch (JsonMappingException e) {
			LOGGER.error("解析配置文件时出现错误", e);
		} catch (IOException e) {
			LOGGER.error("解析配置文件时出现错误", e);
		}
		mqEnumMap.put(key, type);
		return mqEnumMap.get(key);
	}

	public static synchronized  String getMQHost(){
		if(properties.isEmpty()){
			try {
				loadConfig();
			} catch (IOException e) {
				LOGGER.error("解析配置文件时出现错误", e);
			}
		}
		return properties.getProperty("rabbit.host");
	}
	
	public static synchronized  String getMQPort(){
		if(properties.isEmpty()){
			try {
				loadConfig();
			} catch (IOException e) {
				LOGGER.error("解析配置文件时出现错误", e);
			}
		}
		return properties.getProperty("rabbit.port");
	}
	
	public static void main(String[] args){
//		MQPropertiesResolver instance = new MQPropertiesResolver();
		System.out.println("=="+MQPropertiesResolver.getMQProperties("rabbit.edoc-biz"));
	}
}
