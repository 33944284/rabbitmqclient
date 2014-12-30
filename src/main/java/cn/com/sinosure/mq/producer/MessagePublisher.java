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

	void sendMessageWithConfirm(Object messageBody) throws MQException;

	void sendMessage(Object messageBody) throws MQException;

}
