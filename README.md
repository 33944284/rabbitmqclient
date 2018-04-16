##rabbitMQ 封装的工具类 欢迎大家修改批评 未完待续 ....

博客最近会更新关于MQ的知识！

约定：

###文件名称 

config-mq*.properties配置文件


rabbit.XXX = {"vhost":"XXX","user":"XXX","password":"XXX","exchange":"XXX","routingKey":"XXX.*","targetQueue":"XXX"}

文件放置在config配置文件目录或web-inf 具体目录解析可以代码里手动修改

1.左侧key是代码里调用的，可以修改，需保证跟代码里一致

2.右侧value，禁止手动修改，需协商后调整

生产者使用方法：
    
     String key = "rabbit.xxx";//配置文件里的定义字段
     MessagePublisher publisher =       MessagePublisherFactory.getMessagePublisher(key)；
     
     publisher.sendMessageWithConfirm("我是消息体");
     // publisher.sendMessageWithConfirm("我是路由关键字","我是消 息体"); 自定义路由关键字，适合topic exchange，即1条消息有选择性的发送到多个队列，即不是点对点，也不是广播


消费者使用方法：

  类中继承： extends MessageConsumerAdapter

  重写：

	//发送信息解析
	@Override
	public void handleMessage(AmqpMessage paramAmqpMessage) throws Throwable {
		String jsonNode = paramAmqpMessage.getMessageBodyObject(String.class);
	}

	@Override
	public String getRabbitKey() {
		return "claimtrace.rabbit.mil_channelEvaluate";
     //return "rabbit.xxx";
	}
	
	//消息确认机制  是否发生异常进入垃圾队列
	@Override
	public boolean isAutoAck() {
		return false;
	}
    


