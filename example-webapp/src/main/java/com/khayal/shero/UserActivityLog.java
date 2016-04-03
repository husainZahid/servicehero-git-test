package com.khayal.shero;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.sdl.dxa.modules.generic.utilclasses.UAgentInfo;
import com.sdl.webapp.main.controller.core.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class UserActivityLog {
	/*
	Actions logs: 
	id, shUserId, 
	sessionId
    actionType(register,weblogin,fblogin,gplogin,profileUpdate,activate,vote,unsubscribe,fbWallPost,inviteFriends,PasswordReset), 
    actionId, actionTable,
    mediaChannel(website,mobile,phone),
    platform(website,mobile,app,fb app), 
    userAgent, CampaignId, IPAddress, countryCode, GPSInfo, actionTime
    
    Create table UserActivityLog (
    	id number constraint pk_ualId_UserActivityLog primary key,
    	shUserId number,
		sessionId varchar2(100),
    	actionType varchar2(100),
    	actionTypeId number,
    	actionTypeTable varchar2(100),
    	mediaChannel varchar2(100),
    	platform varchar2(100),
    	userAgent varchar2(512), 
    	CampaignId varchar2(100), 
    	IPAddress varchar2(100), 
    	countryName varchar2(100), 
    	GPSInfo varchar2(100), 
    	actionTime TIMESTAMP(6) with time zone,
    	serverTime TIMESTAMP(6) with time zone,
    	userActionGMTTime TIMESTAMP(6) with time zone
    );
    insert into UserActivityLog values(10000, 0, 'N/A', 'N/A', 0, 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 0, 0, 0);
   	CREATE SEQUENCE userActivityLog_Seq MINVALUE 10001 MAXVALUE 9999999999999999 INCREMENT BY 1 START WITH 10001 CACHE 20 NOORDER NOCYCLE;
    */

	public static final String ACTIVITY_WEB_REGISTER = "webRegister";
	public static final String ACTIVITY_FB_REGISTER = "fbRegister";
	public static final String ACTIVITY_GP_REGISTER = "GPRegister";
	public static final String ACTIVITY_WEB_LOGIN = "webLogin";
	public static final String ACTIVITY_FB_LOGIN = "fbLogin";
	public static final String ACTIVITY_GP_LOGIN = "gpLogin";
	public static final String ACTIVITY_ACTIVATE = "activate";
	public static final String ACTIVITY_UNSUBSCRIBE = "unsubscribe";
	public static final String ACTIVITY_VOTE = "vote";
	public static final String ACTIVITY_COMMENT = "comment";
	public static final String ACTIVITY_COMMENT_AGREE = "agreeComment";
	public static final String ACTIVITY_COMMENT_AGREE_REMOVE = "agreeCommentRemove";
	public static final String ACTIVITY_COMMENT_DISAGREE = "disagreeComment";
	public static final String ACTIVITY_COMMENT_DISAGREE_REMOVE = "disagreeCommentRemove";
	public static final String ACTIVITY_COMMENT_HELPFUL = "helpfulComment";
	public static final String ACTIVITY_COMMENT_NOT_HELPFUL = "nothelpfulComment";
	public static final String ACTIVITY_CONTACT_US = "contactUs";
	public static final String ACTIVITY_FBWALLPOST = "fbWallPost";
	public static final String ACTIVITY_SURVEY_TABLE = "USERACTIVITYLOG";
	public static final String ACTIVITY_DASHBOARD_TABLE = "BRANDUSERACTIVITYLOG";
	public static final String ACTIVITY_DASHBOARD_COMMENT_LOCK ="commentlock";
	public static final String ACTIVITY_DASHBOARD_COMMENT_UNLOCK ="commentunlock";
	public static final String ACTIVITY_DASHBOARD_COMMENT_FLAGGED ="flagged";
	public static final String ACTIVITY_DASHBOARD_COMMENT_REPLY ="reply";
	public static final String ACTIVITY_DASHBOARD_COMMENT_OKAY ="okay";
	public static final String ACTIVITY_DASHBOARD_COMMENT_PRIVATE   ="private";
	
	

	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

	private long id;
    private long shUserId;
	private String sessionId;
	private String actionType;
    private long actionTypeId;
    private String actionTypeTable;
	private String mediaChannel;
    private String platform;
    private String userAgent;
    private String campaignId;
    private String ipAddress;
    private String countryName;
    private String gpsInfo;
    private long actionTime;
    private long serverTime;
    private long userActionTime;
    
	public UserActivityLog() {
    	id = 0;
    	shUserId = 0;
    	sessionId = "N/A";
    	actionType = "N/A";
    	actionTypeId = 0;
    	actionTypeTable = "N/A";
    	mediaChannel = "N/A";
        platform = "N/A";
        userAgent = "N/A";
        campaignId = "N/A";
        ipAddress = "N/A";
        countryName = "N/A";
        gpsInfo = "N/A";
        actionTime = 0;
        serverTime = 0;
        userActionTime = 0;
    }
	
    public long getId() {
		return id;
	}
	
    public void setId(long id) {
		this.id = id;
	}
	
    public long getShUserId() {
		return shUserId;
	}
	
    public void setShUserId(long shUserId) {
		this.shUserId = shUserId;
	}
	
    public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getActionType() {
		return actionType;
	}
	
    public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
    public long getActionTypeId() {
		return actionTypeId;
	}

	public void setActionTypeId(long actionTypeId) {
		this.actionTypeId = actionTypeId;
	}

	public String getActionTypeTable() {
		return actionTypeTable;
	}

	public void setActionTypeTable(String actionTypeTable) {
		this.actionTypeTable = actionTypeTable;
	}

	public String getMediaChannel() {
		return mediaChannel;
	}
	
    public void setMediaChannel(String mediaChannel) {
		this.mediaChannel = mediaChannel;
	}
	
    public String getPlatform() {
		return platform;
	}
	
    public void setPlatform(String platform) {
		this.platform = platform;
	}
	
    public String getUserAgent() {
		return userAgent;
	}
	
    public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
    public String getCampaignId() {
		return campaignId;
	}
	
    public void setCampaignId(String campaignId) {
    	this.campaignId = campaignId;
	}
	
    public String getIpAddress() {
		return ipAddress;
	}
	
    public void setIpAddress(String ipAddress) {
    	this.ipAddress = ipAddress;
	}
	
    public String getCountryName() {
		return countryName;
	}
	
    public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
    public String getGpsInfo() {
		return gpsInfo;
	}
	
    public void setGpsInfo(String gpsInfo) {
    	this.gpsInfo = gpsInfo;
	}
	
    public long getActionTime() {
		return actionTime;
	}
	
    public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}
    
    public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public long getUserActionTime() {
		return userActionTime;
	}

	public void setUserActionTime(long userActionTime) {
		this.userActionTime = userActionTime;
	}


	public int insertRecord(HttpServletRequest request, Connection connection, String updateActivityTable, String actionType, long shUserId, long actionTypeId, String actionTypeTable) {

    	int result = 0;
    	try {
    		ServiceHeroUser shUserData =  (ServiceHeroUser) request.getSession().getAttribute("shUserData");
    		if(shUserData != null) {
    			this.shUserId = shUserData.getId();
    		} else 
    			this.shUserId = shUserId;
    		this.actionType = actionType;
    		this.actionTypeId = actionTypeId;
    		this.actionTypeTable = actionTypeTable;
    		this.sessionId = request.getSession().getId();
    		

    		mediaChannel =  (String) request.getSession().getAttribute("mediaChannel");	//Set mediaChannel value to session 
            if(mediaChannel == null || mediaChannel.length() == 0)
            	mediaChannel = "website";
            String strPCSurvey = (String) request.getSession().getAttribute("pcSHUserId");	//Set pcSHUserId value to session
            if(strPCSurvey == null || strPCSurvey.length() == 0)
                strPCSurvey = "N/A";
            if(!strPCSurvey.equals("N/A"))
            	mediaChannel = "phone";

            platform =  (String) request.getSession().getAttribute("platform");	//Set platform value to session
            if(platform == null || platform.length() == 0)
            	platform = "website";
    		
    		campaignId = (String) request.getSession().getAttribute("campaignId");	//Set campaignId value to session
            if(campaignId == null || campaignId.length() == 0)
            	campaignId = "N/A";

            /*if (request.getHeader("HTTP_X_FORWARDED_FOR") == null)
            	ipAddress = request.getRemoteAddr();
            else
            	ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
            if(ipAddress == null || ipAddress.length() == 0)
            	ipAddress = "N/A";

    		userAgent = request.getHeader("User-Agent");*/
    		
            userAgent = (String) request.getSession().getAttribute("userAgent");	
            if(userAgent == null || userAgent.length() == 0)
            	userAgent = "N/A";

            ipAddress = (String) request.getSession().getAttribute("ipAddress");	
            if(ipAddress == null || ipAddress.length() == 0)
            	ipAddress = "N/A";

    		countryName = (String) request.getSession().getAttribute("ipAddressCountry");	
            if(countryName == null || countryName.length() == 0)
            	countryName = "N/A";

    		gpsInfo = (String) request.getSession().getAttribute("gpsInfo");	//Set gpsInfo value to session
            if(gpsInfo == null || gpsInfo.length() == 0)
            	gpsInfo = "N/A";


            PreparedStatement ps = connection.prepareStatement("Select " + updateActivityTable+"_Seq.nextVal from dual");
		    //PreparedStatement ps = connection.prepareStatement("Select USERACTIVITYLOG_Seq.nextVal from dual");
		    ResultSet objRs = ps.executeQuery();
            if(objRs.next())
            	id = objRs.getLong(1);
            objRs.close();
            ps.close();
            
            ps = connection.prepareStatement("insert into "+updateActivityTable+" values(?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?)");
    		ps.setLong(1, id);
    		ps.setLong(2, this.shUserId);
    		ps.setString(3, this.sessionId);
    		ps.setString(4, this.actionType);
    		ps.setLong(5, this.actionTypeId);
    		ps.setString(6, this.actionTypeTable);
    		ps.setString(7, mediaChannel);
    		ps.setString(8, platform);
    		ps.setString(9, userAgent);
    		ps.setString(10, campaignId);
    		ps.setString(11, ipAddress);
    		ps.setString(12, countryName);
    		ps.setString(13, gpsInfo);
    		
    		Calendar cal = Calendar.getInstance();
    		ps.setTimestamp(15, new Timestamp(cal.getTimeInMillis()));
    		
    		long gmtTime = cal.getTime().getTime();
    		long localTime = gmtTime - TimeZone.getTimeZone("Asia/Kuwait").getRawOffset();
    		Calendar localTime1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    		localTime1.setTimeInMillis(localTime);
    		ps.setTimestamp(16, new Timestamp(localTime1.getTimeInMillis()));

    		if(request.getSession().getAttribute("userCountry").equals("kw")) {
    			ps.setTimestamp(14, new Timestamp(cal.getTimeInMillis()));
    		} else {
    			localTime = localTime + TimeZone.getTimeZone("Asia/Dubai").getRawOffset();
        		localTime1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dubai"));
        		localTime1.setTimeInMillis(localTime);
        		ps.setTimestamp(14, new Timestamp(localTime1.getTimeInMillis()));
    		}
		result = ps.executeUpdate();
		    LOG.error("activateEmail updateActivityTable " + result);

			ps.close();
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}finally {
		}
    	return result;
    	
    }
	
	public static void setActivityVariables(HttpServletRequest request) {
		String sessionValue = "";
		HttpSession session =  request.getSession();
		if(session.getAttribute("mediaChannel") == null) {
			//if(session.getAttribute("mediaChannel") == null) {
				UAgentInfo objMDetect = new UAgentInfo(request.getHeader("user-agent"), request.getHeader("accept"));
				if(objMDetect.detectSmartphone()) {
					session.setAttribute("mediaChannel", "Mobile");
					session.setAttribute("platform", "Mobile");
				} else {
					session.setAttribute("mediaChannel", "Desktop");
					session.setAttribute("platform", "Website");
				}
			//}
	
			//if(session.getAttribute("ipAddress") == null) {
				getIPLocation(request);
			//}
	
			//if(session.getAttribute("userAgent") == null) {
				sessionValue = request.getHeader("user-agent");
				if(sessionValue == null || sessionValue.length() == 0)
					sessionValue = "N/A";
				if(!sessionValue.equals("N/A"))
					session.setAttribute("userAgent", sessionValue);	
			//}
			
			//if(session.getAttribute("campaignId") == null) {
				sessionValue = request.getParameter("campaignId");
				if(sessionValue == null || sessionValue.length() == 0)
					sessionValue = "N/A";
				if(!sessionValue.equals("N/A"))
					session.setAttribute("campaignId", sessionValue);
			//}
		}
	}
	
    
    public static int updateDatabaseFields(Connection connection, String tableName, TreeMap columnNameValues, TreeMap identifierNameValues) {
    	int result = 0;
    	String strQuery = "", strTemp = "";
    	strQuery = "update " + tableName + " set ";
    	Iterator objIt = columnNameValues.keySet().iterator();
    	while(objIt.hasNext()) {
    		strTemp = (String) objIt.next();
    		if(((String)columnNameValues.get(strTemp)).startsWith("s~"))
    			strQuery += strTemp + " = '" + ((String)columnNameValues.get(strTemp)).substring(2) + "',";
    		else 
    			strQuery += strTemp + " = " + ((String)columnNameValues.get(strTemp)).substring(2) + ",";
    	}
    	strQuery = strQuery.substring(0, strQuery.length() - 1) + " where ";
    	objIt = identifierNameValues.keySet().iterator();
    	while(objIt.hasNext()) {
    		strTemp = (String) objIt.next();
    		if(((String)identifierNameValues.get(strTemp)).startsWith("s~"))
    			strQuery += strTemp + " = '" + ((String)identifierNameValues.get(strTemp)).substring(2) + "' and ";
    		else 
    			strQuery += strTemp + " = " + ((String)identifierNameValues.get(strTemp)).substring(2) + ", and ";
    	}
    	strQuery = strQuery.substring(0, strQuery.length() - 6);
    	try {
    		PreparedStatement ps = connection.prepareStatement(strQuery);
    		result = ps.executeUpdate();
    		ps.close();
    		connection.commit();
    	} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}finally {
		}
    	return result;
    }

	public static long getNextSequence(String sequenceName, Connection connection )     {
		long id = 0;
		try {
			String sequenceSqlStatement = "Select "+sequenceName+".nextVal from dual";
			PreparedStatement ps = connection.prepareStatement(sequenceSqlStatement);
			ResultSet objRs = ps.executeQuery();
			if (objRs.next())
				id = objRs.getLong(1);
			objRs.close();
			ps.close();
		} catch (Exception e){
			//TODO: later handle exception handling
			LOG.error("Exception occured : ");
			LOG.error(e.getMessage(), e);
		}
		return id;
	}
	
	
	public static void getIPLocation(HttpServletRequest request) {
		HttpSession session =  request.getSession();
		try {
			IP2Location ip2Location;
	        ip2Location = new IP2Location();
	        IP2Location.IPDatabasePath = "D:\\Apache\\tomcat-sh-web1\\webapps\\ROOT\\others\\IP2Location\\IP-COUNTRY.BIN";
	        IP2Location.IPLicensePath = "D:\\Apache\\tomcat-sh-web1\\webapps\\ROOT\\others\\IP2Location\\License.key";
	        String ipAddress;
	        if (request.getHeader("HTTP_X_FORWARDED_FOR") == null)
	            ipAddress = request.getRemoteAddr();
	        else
	            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
	        IPResult result = ip2Location.IPQuery(ipAddress);
			session.setAttribute("ipAddress",  ipAddress);
			session.setAttribute("ipAddressCountry", result.getCountryLong());
	        if(request.getSession().getAttribute("shortcutPage") == null) {
				if(result.getCountryLong().toLowerCase().indexOf("arab emirates") > -1)
					session.setAttribute("userCountry", "ae");
				else 
					session.setAttribute("userCountry", "kw");
				if(request.getRequestURI().toLowerCase().indexOf("/ar/") > -1)
					session.setAttribute("userLanguage", "ar");
				else 
					session.setAttribute("userLanguage", "en");
			}
		} catch (Exception e){
			LOG.error("Exception occured : ");
			LOG.error(e.getMessage(), e);
		}
    }


	public static String generateErrorLogs(HttpServletRequest request) {
		String errorString = "";
		HttpSession session =  request.getSession();
		try {
			Enumeration enames;
		    String attributeName;
		    enames = session.getAttributeNames();
		    while (enames.hasMoreElements()) {
		        attributeName = (String) enames.nextElement();
		        errorString += "[" + attributeName + "=" + session.getAttribute(attributeName) + "]";
		    }
		    
		    attributeName = "loginEmail";
		    String strTemp = request.getParameter(attributeName);
		    if (strTemp == null || strTemp.length() == 0)
		    	strTemp = "N/A";
		    if(strTemp.equals("N/A"))
		        errorString += "[" + attributeName + "=" + session.getAttribute(strTemp) + "]";

		    attributeName = "registerEmail";
		    strTemp = request.getParameter(attributeName);
		    if (strTemp == null || strTemp.length() == 0)
		    	strTemp = "N/A";
		    if(strTemp.equals("N/A"))
		        errorString += "[" + attributeName + "=" + session.getAttribute(strTemp) + "]";

		    attributeName = "surveySector";
		    strTemp = request.getParameter(attributeName);
		    if (strTemp == null || strTemp.length() == 0)
		    	strTemp = "N/A";
		    if(strTemp.equals("N/A"))
		        errorString += "[" + attributeName + "=" + session.getAttribute(strTemp) + "]";

		    attributeName = "surveyBrand";
		    strTemp = request.getParameter(attributeName);
		    if (strTemp == null || strTemp.length() == 0)
		    	strTemp = "N/A";
		    if(strTemp.equals("N/A"))
		        errorString += "[" + attributeName + "=" + session.getAttribute(strTemp) + "]";

		    attributeName = "surveyDealer";
		    strTemp = request.getParameter(attributeName);
		    if (strTemp == null || strTemp.length() == 0)
		    	strTemp = "N/A";
		    if(strTemp.equals("N/A"))
		        errorString += "[" + attributeName + "=" + session.getAttribute(strTemp) + "]";

		    attributeName = "surveyLastVisit";
		    strTemp = request.getParameter(attributeName);
		    if (strTemp == null || strTemp.length() == 0)
		    	strTemp = "N/A";
		    if(strTemp.equals("N/A"))
		        errorString += "[" + attributeName + "=" + session.getAttribute(strTemp) + "]";
		
		} catch (Exception e){
			LOG.error("Exception occured : ");
			LOG.error(e.getMessage(), e);
		}
		
		return errorString;
		
    }
	
}
