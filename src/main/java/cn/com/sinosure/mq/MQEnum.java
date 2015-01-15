package cn.com.sinosure.mq;


public class MQEnum {
//
//	EDOC2BIZ("EDOC-BIZ", "EDOC-BIZ", "EDOC-BIZ", "EDOC-BIZ", "EDOC-BIZ",""),
//	
//	QUOTA2EDOC("QUOTA-EDOC", "QUOTA-EDOC", "QUOTA-EDOC", "QUOTA-EDOC", "QUOTA-EDOC",""), 
//	
//	DMC2BIZ("DMC-BIZ", "DMC-BIZ", "DMC-BIZ", "DMC-BIZ", "DMC-BIZ",""), 
//	
//	EDOC2RBAC("EDOC-RBAC", "edoc-rbac", "edoc-rbac", "EDOC-RBAC", "EDOC-RBAC","edoc2rbac"), 
//	
//	MASTERDATA2ALL("MASTER-ALL","MASTER-ALL", "MASTER-ALL", "MASTER-ALL", "MASTER-ALL","");
//	
////	MASTERDATA2EDOC("MASTER-ALL","MASTER-ALL", "MASTER-ALL", "MASTER-ALL", "MASTER-ALL","master2edoc");
////	
////	MASTERDATA2EDOC("MASTER-ALL","MASTER-ALL", "MASTER-ALL", "MASTER-ALL", "MASTER-ALL","master2edoc");

	public String getVhost() {
		return vhost;
	}

	public void setVhost(String vhost) {
		this.vhost = vhost;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String vhost;
	private String user;
	private String password;
	private String exchange;
	private String routingKey;
	private String targetQueue;
	
	public String getTargetQueue() {
		return targetQueue;
	}


	public MQEnum(String vhost, String user, String password, String exchange,
			String routingKey,String targetQueue) {
		this.vhost = vhost;
		this.user = user;
		this.password = password;
		this.exchange = exchange;
		this.routingKey = routingKey;
		this.targetQueue = targetQueue;
	}
	
	public MQEnum(){
		
	}
}
