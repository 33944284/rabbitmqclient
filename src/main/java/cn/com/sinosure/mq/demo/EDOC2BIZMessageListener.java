package cn.com.sinosure.mq.demo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebListener;

import cn.com.sinosure.mq.utils.web.MessageConsumeListener;

@WebListener
public class EDOC2BIZMessageListener extends MessageConsumeListener {

	@Override
	public List<String> getConsumerHandlerBeanNames() {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		list.add("defaultMessageHandler");
		return list;
	}

}
