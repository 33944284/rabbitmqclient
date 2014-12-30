package cn.com.sinosure.mq;

public class MQException extends Exception {

	public MQException(String errorMsg, Throwable throwable) {
		super(errorMsg, throwable);
	}
}
