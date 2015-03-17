package cn.com.sinosure.mq.demo;

import java.io.Serializable;

public class TrackPageLog
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final String STATIC_UPDATE = "0";
  public static final String DYNAMIC_UPDATE = "1";
  public static final String INIT_STR = "^.^.^.^";
  private String initStrFlag;
  private String serialNo;
  private String userName;
  private String instanceID;
  private String sessionID;
  private String pageURI;
  private String startTime;
  private String endTime;
  private String loadTime;
  private String trackInfo;

  public TrackPageLog()
  {
    this.initStrFlag = "";

    this.serialNo = "";
    this.userName = "";
    this.instanceID = "";
    this.sessionID = "";
    this.pageURI = "";
    this.startTime = "";
    this.endTime = "";
    this.loadTime = "";
    this.trackInfo = "";

    this.initStrFlag = "0";
  }

  public TrackPageLog(String userName, String instanceID, String sessionId, String pageURI, String startTime, String endTime, String loadTime, String trackInfo)
  {
    setUserName(userName);
    setInstanceID(instanceID);
    setSessionID(sessionId);
    setPageURI(pageURI);
    setStartTime(startTime);
    setEndTime(endTime);
    setLoadTime(loadTime);
    setTrackInfo(trackInfo);
  }

  public TrackPageLog(String str)
  {
    this.initStrFlag = "";

    this.serialNo = "";
    this.userName = "";
    this.instanceID = "";
    this.sessionID = "";
    this.pageURI = "";
    this.startTime = "";
    this.endTime = "";
    this.loadTime = "";
    this.trackInfo = "";

    this.initStrFlag = "1";
    this.serialNo = "^.^.^.^";
    this.userName = "^.^.^.^";
    this.instanceID = "^.^.^.^";
    this.sessionID = "^.^.^.^";
    this.pageURI = "^.^.^.^";
    this.startTime = "^.^.^.^";
    this.endTime = "^.^.^.^";
    this.loadTime = "^.^.^.^";
    this.trackInfo = "^.^.^.^";
  }

  public String getInitStrFlag()
  {
    return this.initStrFlag;
  }

  public String toString()
  {
    StringBuffer str = new StringBuffer();
    str.append("TrackPageLog:\n");
    str.append("\t序号\t[serialNo = ").append(this.serialNo).append("]\n");
    str.append("\t用户名\t[userName = ").append(this.userName).append("]\n");
    str.append("\t实例ID\t[instanceID = ").append(this.instanceID).append("]\n");
    str.append("\tSession ID\t[sessionID = ").append(this.sessionID).append("]\n");
    str.append("\t页面链接\t[pageURI = ").append(this.pageURI).append("]\n");
    str.append("\t开始时间\t[startTime = ").append(this.startTime).append("]\n");
    str.append("\t结束时间\t[endTime = ").append(this.endTime).append("]\n");
    str.append("\t加载时间,毫秒\t[loadTime = ").append(this.loadTime).append("]\n");
    str.append("\t页面信息\t[trackInfo = ").append(this.trackInfo).append("]\n");
    return str.toString();
  }

  public void setSerialNo(String serialNo)
  {
    this.serialNo = serialNo;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public void setInstanceID(String instanceID)
  {
    this.instanceID = instanceID;
  }

  public void setSessionID(String sessionID)
  {
    this.sessionID = sessionID;
  }

  public void setPageURI(String pageURI)
  {
    this.pageURI = pageURI;
  }

  public void setStartTime(String startTime)
  {
    this.startTime = startTime;
  }

  public void setEndTime(String endTime)
  {
    this.endTime = endTime;
  }

  public void setLoadTime(String loadTime)
  {
    this.loadTime = loadTime;
  }

  public void setTrackInfo(String trackInfo)
  {
    this.trackInfo = trackInfo;
  }

  public String getSerialNo()
  {
    return this.serialNo;
  }

  public String getUserName()
  {
    return this.userName;
  }

  public String getInstanceID()
  {
    return this.instanceID;
  }

  public String getSessionID()
  {
    return this.sessionID;
  }

  public String getPageURI()
  {
    return this.pageURI;
  }

  public String getStartTime()
  {
    return this.startTime;
  }

  public String getEndTime()
  {
    return this.endTime;
  }

  public String getLoadTime()
  {
    return this.loadTime;
  }

  public String getTrackInfo()
  {
    return this.trackInfo;
  }
}