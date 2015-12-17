package cn.com.sinosure.mq.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
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

	private static final String CONFIG_FILE_DIR = "WEB-INF";
	
	private static final String CLASS_DIR = "classes";

	private static final String CONFIG_FILE_PREFIX = "config-mq";
	
	private static final String host_location = "host-mq.properties";

	
	private static void loadConfig() throws IOException{
		
		
		String path = MQPropertiesResolver.class.getResource("/").getPath();
		LOGGER.info("path1=="+path.toString());
		if(path.contains(CONFIG_FILE_DIR)){
			path = path.substring(0,path.indexOf(CLASS_DIR));
		}
		LOGGER.info("path2=="+path.toString());
		File sourceFileDir = new File(path);
		
		File[] configList =	sourceFileDir.listFiles(new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name) {
                if(name.startsWith(CONFIG_FILE_PREFIX)){
                   	return true;
                }else{
                	
                	return false;
                }
            }
        });
		
		if(configList == null || configList.length <= 0){
			throw new IOException("没有找到config-mq开头的配置文件");
		}
		
		InputStream inputStream = null;
		for(File file : configList){
			inputStream = new FileInputStream(file);
			if(inputStream!=null){
				properties.load(inputStream);
			}
		}
		
		
		LOGGER.info("properties1=="+properties.toString());
		
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
			LOGGER.info("rabbit.key=="+key);
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
//		System.out.println("=="+MQPropertiesResolver.getMQProperties("rabbit.edoc-biz"));
		
		
		File sourceFileDir = new File(CONFIG_FILE_DIR);
		
		File[] configList =	sourceFileDir.listFiles(new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name) {
                if(name.startsWith(CONFIG_FILE_PREFIX)){
                	System.out.println("===="+name);
                	return true;
                }else{
                	System.out.println("false===="+name);

                	return false;
                }
            }
        });
		
		System.out.println(configList.length);
	}
}
