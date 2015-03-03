/**
 * 
 */
package cn.com.sinosure.mq.producer;

import cn.com.sinosure.mq.MQException;

/**
 * @author zhang_tao@sinosure.com.cn
 * 
 */
public interface MessagePublisher {

	/**
	 * 发送消息
	 * @param routingKey 消息的关键字，依靠此关键字进行消息的路由
	 * @param messageBody 具体消息内容
	 * @throws MQException
	 */
	void sendMessageWithConfirm(String routingKey,Object messageBody) throws MQException;
	
	/**
	 * 发送消息
	 * @param messageBody 具体消息内容
	 * @throws MQException
	 */
	void sendMessageWithConfirm(Object messageBody) throws MQException;
	
	

	void sendMessage(Object messageBody) throws MQException;
	
	void sendMessage(String routingKey,Object messageBody) throws MQException;
	

}
