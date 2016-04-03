package com.khayal.shero;

import com.sdl.dxa.modules.generic.model.UserHistory;
import com.sdl.dxa.modules.generic.utilclasses.DatabaseQueryBuilder;
import com.sdl.dxa.modules.generic.utilclasses.DatabaseQueryField;
import com.sdl.dxa.modules.generic.utilclasses.DateFunctions;
import com.sdl.dxa.modules.generic.utilclasses.PasswordEncryption;
import com.sdl.webapp.common.api.model.Entity;
import com.tridion.marketingsolution.profile.*;

import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class SHSurveyUser extends ServiceHeroUser {
	/*
	SHeroUserTableExtra: shUserId, activated, numberOfVotes, votingAverage, contactMe, offersMe, isProfileUpdateAjaxm, isProfileUpdateComplete, oauthprvdrtyp, strOauthvalidrid
	drop table serviceHeroUsersExtra purge;
	Create table serviceHeroUsersExtra (
			shUserId constraint fkshuId_serviceHeroUsers references serviceHeroUsers(id),
	    	activated varchar2(1),
	    	numberOfVotes number,
	    	votingAverage number,
	    	contactMe varchar2(1),
	    	offersMe varchar2(1),
	    	profileUpdateAjaxm varchar2(1),
	    	profileUpdateComplete varchar2(1),
	    	socialMediaWallPost varchar2(1),
			adminUser varchar2(1),
			interestedStaff varchar2(1),
		    interestedLocation varchar2(1),
		    interestedValue varchar2(1),
		    interestedQuality varchar2(1),
		    interestedSpeed varchar2(1),
		    interestedReliability varchar2(1),
		    interestedCallCenter varchar2(1),
		    interestedWebsite varchar2(1),
		    interestedOverAllSatisfaction varchar2(1),
		    interestedRecommendation varchar2(1),
		    interestedIdealoffering varchar2(1)
	);
	insert into serviceHeroUsersExtra values(10000, 'N', 0, 0, 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');
	
	Create table BrandAverageScores_kw (
			brandCode number,
			sectorCode number,
			parentSectorCode number,
			comments number,
			nominations number,
			wins number,
			trends number,
			starRating number(5,3),
			averageBrand number(5,3),
			averageStaff number(5,3),
			averageLocation number(5,3),
			averageValue number(5,3),
			averageQuality number(5,3),
			averageSpeed number(5,3),
			averageReliability number(5,3),
			averageCallCenter number(5,3),
			averageWebsite number(5,3)
			
	);
	
	insert into BrandAverageScores values(2284, 793, 791, 10, 2, 3, 1, 6.0, 6.8, 6.9, 7.0, 7.1, 7.2, 7.3, 7.4, 7.5);
	insert into BrandAverageScores values(2294, 793, 791, 5, 1, 1, -1, 5.0, 7.8, 7.9, 8.0, 8.1, 8.2, 8.3, 8.4, 8.5);
	*/ 	
	
	public static final int TRIDION_RESET_PASSWORD_PAGE = 20646;
	public static final int TRIDION_UNSUBSCRIBE_MAILING_PAGE = 20647;
	public static final int TRIDION_VOTE_MAILING_PAGE = 20648;
	public static final int TRIDION_REGISTRATION_PAGE = 20649;
	public static final int TRIDION_COUNTACT_US_PAGE = 26277;
	public static final int TRIDION_SUBSCRIBE_MAILING_PAGE = 10001;
	
	private static Logger LOG = LoggerFactory.getLogger(SHSurveyUser.class);
	
	private String activated;
	private long numberOfVotes;
	private float votingAverage;
    private String contactMe;
    private String offersMe;
    private String socialMediaWallPost;
    private String adminUser;
    private String interestedStaff;
    private String interestedLocation;
    private String interestedValue;
    private String interestedQuality;
    private String interestedSpeed;
    private String interestedReliability;
    private String interestedCallCenter;
    private String interestedWebsite;
    private String interestedOverallSatisfaction;
    private String interestedRecommendation;
    private String interestedIdealoffering;
    
    private String votedBrands;
    private String agreeComments;
    private String disagreeComments;
    
  	private int socialMedia;
    private String socialMediaId;
    
	public SHSurveyUser(HttpServletRequest httpServletRequest, String userLanguage) {
		super(httpServletRequest, userLanguage);
		// TODO Auto-generated constructor stub
    	activated = "N";
        numberOfVotes = 0;
        votingAverage = 0;
        contactMe = "N";
        offersMe = "N";
        socialMediaWallPost = "N";
        adminUser = "N";
        interestedStaff = "N";
        interestedLocation = "N";
        interestedValue = "N";
        interestedQuality = "N";
        interestedSpeed = "N";
        interestedReliability = "N";
        interestedCallCenter = "N";
        interestedWebsite = "N";
        interestedOverallSatisfaction = "N";
        interestedRecommendation = "N";
        interestedIdealoffering = "N";
        
        votedBrands = "";
        agreeComments = "";
        disagreeComments = "";
        socialMedia = 0;
        socialMediaId = "";
    }
	
	public SHSurveyUser(HttpServletRequest httpServletRequest, String userLanguage, Long shUserId) {
		super(httpServletRequest, userLanguage, shUserId);
		// TODO Auto-generated constructor stub
    	ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
    	try {
    		connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("select * from serviceHeroUsersExtra where shUserId=?");
			ps.setLong(1, shUserId);
			ResultSet objRs = ps.executeQuery();
			if (objRs.next()) {
				setSHSurveyUserValues(objRs);
	        } 
			objRs.close();
			ps.close();
			
	    	try {
				if(httpServletRequest.getSession().getAttribute("socialMedia") != null) {
					int smType = Integer.parseInt((String)httpServletRequest.getSession().getAttribute("socialMedia"));
					ps = connection.prepareStatement("select * from serviceHeroUsersSocial where shUserId=? and socialMedia=?");
					ps.setLong(1, shUserId);
					ps.setInt(2, smType);
					objRs = ps.executeQuery();
					if (objRs.next()) {
						socialMedia = objRs.getInt("socialMedia");
						socialMediaId = objRs.getString("socialMediaId");
					} 
					objRs.close();
					ps.close();
				}
			} catch(Exception ex) {
				LOG.error(ex.getMessage(),ex);
			}			
			fetchVotedBrands(getCountryCode());
			fetchCommentCounts();
			
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
	}
	
	public void setSHSurveyUserValues(ResultSet objRs) {
		try {
			activated = objRs.getString(2);
			numberOfVotes = objRs.getLong(3);
			votingAverage = objRs.getFloat(4);
			contactMe = objRs.getString(5);
			offersMe = objRs.getString(6);
			socialMediaWallPost = objRs.getString(7);
			adminUser = objRs.getString(8);
			interestedStaff = objRs.getString(9);
			interestedLocation = objRs.getString(10);
			interestedValue = objRs.getString(11);
			interestedQuality = objRs.getString(12);
			interestedSpeed = objRs.getString(13);
			interestedReliability = objRs.getString(14);
			interestedCallCenter = objRs.getString(15);
			interestedWebsite = objRs.getString(16);
			interestedOverallSatisfaction = objRs.getString(17);
			interestedRecommendation = objRs.getString(18);
			interestedIdealoffering = objRs.getString(19);
		} catch(Exception e) {
			
		}
	    
	}

	public String getActivated() {
		return activated;
	}

	public void setActivated(String activated) {
		this.activated = activated;
	}

	public long getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(long numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

	public float getVotingAverage() {
		return votingAverage;
	}

	public void setVotingAverage(float votingAverage) {
		this.votingAverage = votingAverage;
	}

	public String getContactMe() {
		return contactMe;
	}

	public void setContactMe(String contactMe) {
		this.contactMe = contactMe;
	}

	public String getOffersMe() {
		return offersMe;
	}

	public void setOffersMe(String offersMe) {
		this.offersMe = offersMe;
	}

	public String getSocialMediaWallPost() {
		return socialMediaWallPost;
	}

	public void setSocialMediaWallPost(String socialMediaWallPost) {
		this.socialMediaWallPost = socialMediaWallPost;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getInterestedStaff() {
		return interestedStaff;
	}

	public void setInterestedStaff(String interestedStaff) {
		this.interestedStaff = interestedStaff;
	}

	public String getInterestedLocation() {
		return interestedLocation;
	}

	public void setInterestedLocation(String interestedLocation) {
		this.interestedLocation = interestedLocation;
	}

	public String getInterestedValue() {
		return interestedValue;
	}

	public void setInterestedValue(String interestedValue) {
		this.interestedValue = interestedValue;
	}

	public String getInterestedQuality() {
		return interestedQuality;
	}

	public void setInterestedQuality(String interestedQuality) {
		this.interestedQuality = interestedQuality;
	}

	public String getInterestedSpeed() {
		return interestedSpeed;
	}

	public void setInterestedSpeed(String interestedSpeed) {
		this.interestedSpeed = interestedSpeed;
	}

	public String getInterestedReliability() {
		return interestedReliability;
	}

	public void setInterestedReliability(String interestedReliability) {
		this.interestedReliability = interestedReliability;
	}

	public String getInterestedCallCenter() {
		return interestedCallCenter;
	}

	public void setInterestedCallCenter(String interestedCallCenter) {
		this.interestedCallCenter = interestedCallCenter;
	}

	public String getInterestedWebsite() {
		return interestedWebsite;
	}

	public void setInterestedWebsite(String interestedWebsite) {
		this.interestedWebsite = interestedWebsite;
	}

	public String getInterestedOverallSatisfaction() {
		return interestedOverallSatisfaction;
	}

	public void setInterestedOverallSatisfaction(
			String interestedOverallSatisfaction) {
		this.interestedOverallSatisfaction = interestedOverallSatisfaction;
	}

	public String getInterestedRecommendation() {
		return interestedRecommendation;
	}

	public void setInterestedRecommendation(String interestedRecommendation) {
		this.interestedRecommendation = interestedRecommendation;
	}

	public String getInterestedIdealoffering() {
		return interestedIdealoffering;
	}

	public void setInterestedIdealoffering(String interestedIdealoffering) {
		this.interestedIdealoffering = interestedIdealoffering;
	}
	
	public String getVotedBrands() {
		return votedBrands;
	}

	public void setVotedBrands(String votedBrands) {
		this.votedBrands = votedBrands;
	}

	public String getAgreeComments() {
		return agreeComments;
	}

	public void setAgreeComments(String agreeComments) {
		this.agreeComments = agreeComments;
	}

	public String getDisagreeComments() {
		return disagreeComments;
	}

	public void setDisagreeComments(String disagreeComments) {
		this.disagreeComments = disagreeComments;
	}

	public int getSocialMedia() {
		return socialMedia;
	}

	public void setSocialMedia(int socialMedia) {
		this.socialMedia = socialMedia;
	}

	public String getSocialMediaId() {
		return socialMediaId;
	}

	public void setSocialMediaId(String socialMediaId) {
		this.socialMediaId = socialMediaId;
	}

	public static String validateUserForSurvey(HttpServletRequest httpServletRequest, HttpServletResponse response, String userLanguage, String emailAddress, String password, String formType) {
		// TODO Auto-generated method stub
		String status = "", loginStatus = "";
    	ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
    	try {
    		connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("select * from serviceHeroUsers where lower(emailAddress)=?");
			ps.setString(1, emailAddress);
			ResultSet objRs = ps.executeQuery();
			if (objRs.next()) {
				String hashedPassword = PasswordEncryption.hashpw(password, objRs.getString("encryptionSalt"));
				loginStatus = objRs.getString("emailAddress") + ".equals(" + emailAddress.toLowerCase() + ")=" + (objRs.getString("password").toLowerCase().equals(emailAddress.toLowerCase())) + "; " + objRs.getString("password") + ".equals(" + hashedPassword + ")=" + (objRs.getString("password").equals(hashedPassword));
				if(formType.equals("login")) {
					if (objRs.getString("password").equals(hashedPassword) || objRs.getString("id").equals((String) httpServletRequest.getSession().getAttribute("pcSHUserId"))) {
						status = "user-valid-password-match";
						SHSurveyUser shSurveyUser;
						shSurveyUser = new SHSurveyUser(httpServletRequest, userLanguage, objRs.getLong("id"));
						httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
						int newNotificationCount = 0;
						Contact contact;
						contact = new Contact(new String[]{emailAddress, "servicehero"});
						try {
							newNotificationCount = Integer.parseInt((String)contact.getExtendedDetail("extra_field_10"));
						} catch(Exception px) {
							newNotificationCount = 0;
						}	
						httpServletRequest.getSession().setAttribute("newNotificationCount", String.valueOf(newNotificationCount));
						String rememberMe = httpServletRequest.getParameter("rememberMe");
						if(rememberMe != null && rememberMe.equals("remember-me")) {
							Cookie objCookie = new Cookie("sheroUserId", String.valueOf(objRs.getLong("id")));
					    	objCookie.setDomain("www.servicehero.com");
				            objCookie.setPath("/");
				            objCookie.setComment("servicehero: Login");
				            objCookie.setMaxAge(31 * 24 * 60 * 60);
				            response.addCookie(objCookie);
						}
						
						UserActivityLog.setActivityVariables(httpServletRequest); 
						UserActivityLog userLog = new UserActivityLog();
						userLog.insertRecord(httpServletRequest, connection,  UserActivityLog.ACTIVITY_SURVEY_TABLE ,UserActivityLog.ACTIVITY_WEB_LOGIN, objRs.getLong("id"), 0, "");

						String campaignId = (String) httpServletRequest.getSession().getAttribute("campaignId");
						if(campaignId != null && campaignId.length() > 0 && campaignId.toLowerCase().indexOf("email") > -1) {
							SHSurveyUser.activateUser(httpServletRequest, objRs.getLong("id"));
							//userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_ACTIVATE, objRs.getLong("id"), 0, "");
						}
					} else {
						httpServletRequest.getSession().setAttribute("forgotPasswordEMail", emailAddress.toLowerCase());
						status = "user-valid-password-wrong";
			        }
				} else if(formType.equals("resetpass")) {
					status = "user-valid-resetpass-match";
					SHSurveyUser shSurveyUser = new SHSurveyUser(httpServletRequest, userLanguage, objRs.getLong("id")); 
					httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
				} else if(formType.equals("useractivate")) {
					status = "user-valid-useractivate-match";
					SHSurveyUser shSurveyUser = new SHSurveyUser(httpServletRequest, userLanguage, objRs.getLong("id")); 
					httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
				} else if(formType.equals("social")) {
					status = "user-valid-social-match";
					SHSurveyUser shSurveyUser = new SHSurveyUser(httpServletRequest, userLanguage, objRs.getLong("id")); 
					httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
				} else if (formType.equals("register")) {
					httpServletRequest.getSession().setAttribute("forgotPasswordEMail", emailAddress.toLowerCase());
					status = "user-valid-already-present";
				}
	        } else {
				status = "user-invalid-email-wrong";
				if(formType.equals("register")) {
					userLanguage = (String) httpServletRequest.getSession().getAttribute("userLanguage");
					if(userLanguage == null || userLanguage.length() == 0)
						userLanguage = "en";
					String userCountry = (String) httpServletRequest.getSession().getAttribute("userCountry");
					if(userCountry == null || userCountry.length() == 0)
						userCountry = "kw";
					
					
					String strPass1 = "", strPass2 = "", strEmail = "";
					String strTemp = httpServletRequest.getParameter("registerPassword");
					if(strTemp == null || strTemp.length() == 0)
						strTemp = "N/A";
					else
						strPass1 = strTemp;
					
					strTemp = httpServletRequest.getParameter("retypePassword");
					if(strTemp == null || strTemp.length() == 0)
						strTemp = "N/A";
					else
						strPass2 = strTemp;

					strTemp = httpServletRequest.getParameter("registerEmail");
					if(strTemp == null || strTemp.length() == 0)
						strTemp = "N/A";
					else
						strEmail = strTemp.toLowerCase();

					if (validateEmailAddress(strEmail)) {
						if (!strPass1.equals("N/A") && strPass1.equals(strPass2)) {
							SHSurveyUser shSurveyUser = new SHSurveyUser(httpServletRequest, userLanguage);
							shSurveyUser.getFormfields();
							shSurveyUser.setPreferedLanguage(userLanguage);
							shSurveyUser.setCountryCode(userCountry);
							shSurveyUser.insertNewUser(0);
							httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
							status = "user-new-email-insert";
						} else {
							status = "user-email-pass-not-match";
						}
					}else{
						status = "user-email-invalid";
					}
				} else if(formType.equals("social")) {
					status = "user-new-email-social";
				}
	        } 
			objRs.close();
			ps.close();
			connection.commit();
		} catch(Exception e) {
			status = "user-database-error";
			LOG.error("User Validation error: " + formType + ": " + UserActivityLog.generateErrorLogs(httpServletRequest));
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
		//LOG.error("1: status: " + status);
    	return status;
	}
	
	public void getFormfields() {
		String strTemp = "", strSalt = "", strPass1 = "", strPass2 = "";
		
		strTemp = getHttpServletRequest().getParameter("registerEmail");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setEmailAddress(strTemp.toLowerCase());
		
		strTemp = getHttpServletRequest().getParameter("registerPassword");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		else
			strPass1 = strTemp;
		
		strTemp = getHttpServletRequest().getParameter("retypePassword");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		else
			strPass2 = strTemp;
		if(!strPass1.equals("N/A") && strPass1.equals(strPass2)) {
			strSalt = PasswordEncryption.gensalt();
			setEncryptionSalt(strSalt);
			strTemp = PasswordEncryption.hashpw(strPass1, strSalt);
			setPassword(strTemp);
		}
			
		strTemp = getHttpServletRequest().getParameter("chkPreferedLang");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setPreferedLanguage(strTemp);
		
		strTemp = getHttpServletRequest().getParameter("txtName");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setPersonName(strTemp);
		
		strTemp = getHttpServletRequest().getParameter("txtMobile");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setMobileNumber(strTemp);
		
		strTemp = getHttpServletRequest().getParameter("txtCivilId");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "0";
		setCivilId(Long.parseLong(strTemp));
		
		strTemp = getHttpServletRequest().getParameter("chkGender");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setGender(strTemp);
		
		strTemp = getHttpServletRequest().getParameter("txtAgeGroup");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setAgeGroup(strTemp);
		
		strTemp = getHttpServletRequest().getParameter("txtBirthDate");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "0";
		if(strTemp.equals("0"))
			setDateOfBirth(0);
		else
			setDateOfBirth(DateFunctions.getDateFromStringFixTime(DateFunctions.getSimpleDate(strTemp)).getTimeInMillis());

		strTemp = getHttpServletRequest().getParameter("selNationality");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setNationality(strTemp);

		strTemp = getHttpServletRequest().getParameter("selResidence");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setResidence(strTemp);

		strTemp = getHttpServletRequest().getParameter("selGovernorate");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setGovernorate(strTemp);
		
		strTemp = getHttpServletRequest().getParameter("selEduacation");
		if(strTemp == null || strTemp.length() == 0)
			strTemp = "N/A";
		setEducation(strTemp);
	}
	
	public static int activateUser(HttpServletRequest httpServletRequest, Long shUserId) {
		// TODO Auto-generated method stub
		int result = 0;
    	ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseQueryBuilder dbQuery = new DatabaseQueryBuilder(DatabaseQueryBuilder.QUERY_UPDATE, "serviceHeroUsersExtra");
			List<DatabaseQueryField> columnNameValues = new ArrayList<>();
			columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, "activated", DatabaseQueryField.OPERATOR_EQUAL, "Y", DatabaseQueryField.CONNECTOR_BLANK));
			dbQuery.setColumnNameValues(columnNameValues);

			List<DatabaseQueryField> identifierNameValues = new ArrayList<>();
			identifierNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, "activated", DatabaseQueryField.OPERATOR_EQUAL, "N", DatabaseQueryField.CONNECTOR_AND));
			dbQuery.setIdentifierNameValues(identifierNameValues);

			identifierNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "shUserId", DatabaseQueryField.OPERATOR_EQUAL, String.valueOf(shUserId), DatabaseQueryField.CONNECTOR_BLANK));
			dbQuery.setIdentifierNameValues(identifierNameValues);
			//LOG.error("activateEmail " + dbQuery.getQuery());
			//LOG.debug(dbQuery.getQuery());
			PreparedStatement ps = connection.prepareStatement(dbQuery.getQuery());
			result = ps.executeUpdate();
			ps.close();
			//LOG.error("activateEmail " + result);

			if (result > 0) {
				UserActivityLog userLog = new UserActivityLog();
				userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , UserActivityLog.ACTIVITY_ACTIVATE, shUserId, 0, "");
			}			
			connection.commit();
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
		return result;
	} 
	
	public int updateSHUserExtraValues() {
		// TODO Auto-generated method stub
		int result = 0, count = 0;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(getHttpServletRequest());
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			String strParam = "", strTemp = "";
			DatabaseQueryBuilder dbQuery = new DatabaseQueryBuilder(DatabaseQueryBuilder.QUERY_UPDATE, "serviceHeroUsersExtra");
			List<DatabaseQueryField> columnNameValues = new ArrayList<>();
			
			String strNewOptionValue;
			Contact contact;
			contact = new Contact(new String[]{getEmailAddress(), "servicehero"});
			int[] keywords;
			int plId = getPreferedLanguage().equals("en") ? 9 : 10;
			
			strParam = "numberOfVotes";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "votingAverage";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}
			
			strParam = "contactMe";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				strTemp = (String)getHttpServletRequest().getSession().getAttribute(strParam);
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
				
				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "X";
				}
				if(!strTemp.equals("")) {
		            keywords = new int[] {1033, 1034, 1035};
					for (int i = 0 ; i < keywords.length; i++) { 
						contact.removeKeyword("tcm:9-" + keywords[i] + "-1024"); 
						contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");  
					}
					if(strTemp.equals("Y"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[0] + "-1024");  
					if(strTemp.equals("N"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[1] + "-1024");  
					if(strTemp.equals("X"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[2] + "-1024");  
				}
			}
			
			strParam = "offersMe";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				strTemp = (String)getHttpServletRequest().getSession().getAttribute(strParam);
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
				
				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "X";
				}
				if(!strTemp.equals("")) {
					keywords = new int[] {1036, 1037, 1038};
					for (int i = 0 ; i < keywords.length; i++) { 
						contact.removeKeyword("tcm:9-" + keywords[i] + "-1024"); 
						contact.removeKeyword("tcm:10-" + keywords[i] + "-1024"); 
					}
					if(strTemp.equals("Y"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[0] + "-1024");  
					if(strTemp.equals("N"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[1] + "-1024"); 
					if(strTemp.equals("X"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[2] + "-1024"); 
				}
			}
			
			strParam = "socialMediaWallPost";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				strTemp = (String)getHttpServletRequest().getSession().getAttribute(strParam);
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
				
				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "X";
				}
				if(!strTemp.equals("")) {
					keywords = new int[] {1039, 1040, 1041};
					for (int i = 0 ; i < keywords.length; i++) { 
						contact.removeKeyword("tcm:9-" + keywords[i] + "-1024");  
						contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");  
					}
					if(strTemp.equals("Y"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[0] + "-1024");  
					if(strTemp.equals("N"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[1] + "-1024");  
					if(strTemp.equals("X"))
						contact.addKeyword("tcm:" + plId +  "-" + keywords[2] + "-1024");  
				}
			}
			
			strParam = "interestedStaff";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedLocation";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedValue";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedQuality";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedSpeed";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}
			
			strParam = "interestedReliability";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedCallcenter";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedWebsite";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedOverallSatisfaction";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedRecommendation";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			strParam = "interestedIdealoffering";
			if(getHttpServletRequest().getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)getHttpServletRequest().getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				getHttpServletRequest().getSession().removeAttribute(strParam);
				count++;
			}

			dbQuery.setColumnNameValues(columnNameValues);
			
			List<DatabaseQueryField> identifierNameValues = new ArrayList<>();
			identifierNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "shUserId", DatabaseQueryField.OPERATOR_EQUAL, String.valueOf(getId()), DatabaseQueryField.CONNECTOR_BLANK));
			dbQuery.setIdentifierNameValues(identifierNameValues);
			
			if(count > 0) {
				LOG.debug("ServiceHeroUserExtra: updateSHUserExtraValues - " + dbQuery.getQuery());
				connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(dbQuery.getQuery());
				result = ps.executeUpdate();
				ps.close();
				connection.commit();
				//contact.save(emailPageTcmId);    // Add URL for automated email
				contact.save();    // Add URL for automated email
			}
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
		return result;
	}

	public void fetchVotedBrands(String country) {
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(getHttpServletRequest());
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
        Connection connection = null;
		try {
			SurveySubmission.setVotingDates();
	        connection = dataSource.getConnection();
            String strQuery = "select parentSectorCode, sectorCode, brandCode, dealerCode from surveySubmission_kw a, userActivityLog b where a.shUserId=b.shUserId and a.id=b.actionTypeId and b.actionType='" + UserActivityLog.ACTIVITY_VOTE + "' and a.shUserId=? and actionTime >=? and actionTime <=?";
            PreparedStatement ps = connection.prepareStatement(strQuery);
            ps.setLong(1, getId());
            ps.setTimestamp(2, new Timestamp(SurveySubmission.votingStartDate));
            ps.setTimestamp(3, new Timestamp(SurveySubmission.votingEndDate));
            votedBrands = "";
            ResultSet objRs = ps.executeQuery();
            while (objRs.next()) {
           		votedBrands += "kw~" + objRs.getString("sectorCode") + "~" + objRs.getString("brandCode") + "~" + objRs.getString("dealerCode") + ",";
            }
            objRs.close();
            ps.close();
            
            strQuery = "select parentSectorCode, sectorCode, brandCode, dealerCode from surveySubmission_ae a, userActivityLog b where a.shUserId=b.shUserId and a.id=b.actionTypeId and b.actionType='" + UserActivityLog.ACTIVITY_VOTE + "' and a.shUserId=? and actionTime >=? and actionTime <=?";
            ps = connection.prepareStatement(strQuery);
            ps.setLong(1, getId());
            ps.setTimestamp(2, new Timestamp(SurveySubmission.votingStartDate));
            ps.setTimestamp(3, new Timestamp(SurveySubmission.votingEndDate));
            objRs = ps.executeQuery();
            while (objRs.next()) {
           		votedBrands += "ae~" + objRs.getString("sectorCode") + "~" + objRs.getString("brandCode") + "~" + objRs.getString("dealerCode") + ",";
            }
            objRs.close();
            ps.close();
            
			connection.commit();
        } catch (Exception e) {
			LOG.error(e.getMessage(),e);
        } finally {
			Utils.closeQuietly(connection);
        }
	}
	
	
	public void fetchCommentCounts() {
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(getHttpServletRequest());
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
           
            String strTemp = "", processedIds = ",", strQuery = "";
            connection = dataSource.getConnection();
			strQuery = "select actionTypeId, actionType, max(actionTime) from userActivityLog where actionType like 'agreeComment%' and shUserId=? group by actionTypeId, actionType, actionTime order by actionTime desc";	
			agreeComments = ",";
	        PreparedStatement ps = connection.prepareStatement(strQuery);
            ps.setLong(1, getId());
            ResultSet objRs = ps.executeQuery();
            while (objRs.next()) {
            	strTemp = objRs.getString("actionTypeId");
            	if(processedIds.indexOf("," + strTemp + ",") == -1) {
            		if(objRs.getString("actionType").indexOf("Remove") > -1) {
                    	processedIds += strTemp + ",";
            		} else {
	            		agreeComments += strTemp + ",";
	                	processedIds += strTemp + ",";
            		}
            	}
            }
            objRs.close();
            ps.close();
            
            processedIds = ",";
            disagreeComments = ",";
            strQuery = "select actionTypeId, actionType, max(actionTime) from userActivityLog where actionType like 'disagreeComment%' and shUserId=? group by actionTypeId, actionType, actionTime order by actionTime desc";	
			ps = connection.prepareStatement(strQuery);
            ps.setLong(1, getId());
            objRs = ps.executeQuery();
            while (objRs.next()) {
            	strTemp = objRs.getString("actionTypeId");
            	if(processedIds.indexOf("," + strTemp + ",") == -1) {
            		if(objRs.getString("actionType").indexOf("Remove") > -1) {
                    	processedIds += strTemp + ",";
            		} else {
	            		disagreeComments += strTemp + ",";
	                	processedIds += strTemp + ",";
            		}
            	}
            }
            objRs.close();
            ps.close();
            connection.commit();
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
        } finally {
			Utils.closeQuietly(connection);
        }
	}
	
	public static String updateTridionMailingPreferences(HttpServletRequest request, String emailAddress, int emailPageTcmId) {
		String shStatus = "";
		try {
			int plId = (request.getRequestURI().indexOf("ar") > -1) ? 10 : 9;
			String pattern, strTempString = "";
            pattern = "yyyy.MM.dd HH:mm:ss";
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(pattern);
            strTempString = format.format(new java.util.Date(System.currentTimeMillis())).toString();
            
			Contact contact = new Contact(new String[]{emailAddress, "servicehero"});
			if(emailPageTcmId == TRIDION_UNSUBSCRIBE_MAILING_PAGE) {
				contact.setSubscribeStatusID(Contact.SUBSCRIBE_STATUS_UNSUBSCRIBED);
				contact.save("tcm:" + plId + "-" + TRIDION_UNSUBSCRIBE_MAILING_PAGE + "-64");  
			} else if(emailPageTcmId == TRIDION_SUBSCRIBE_MAILING_PAGE) {
				contact.setSubscribeStatusID(Contact.SUBSCRIBE_STATUS_OPTEDIN);
				contact.save("tcm:" + plId + "-" + TRIDION_SUBSCRIBE_MAILING_PAGE + "-64");  
			} else if(emailPageTcmId == TRIDION_VOTE_MAILING_PAGE) {   
				contact.setExtendedDetail("extra_field_2", strTempString);
				contact.setExtendedDetail("extra_field_5", "Vote-" + strTempString);
				contact.save("tcm:" + plId + "-" + TRIDION_VOTE_MAILING_PAGE + "-64");  
			} else if(emailPageTcmId == TRIDION_RESET_PASSWORD_PAGE) {
				//LOG.error("reset password: " + emailAddress + "  " + TRIDION_RESET_PASSWORD_PAGE);
                contact.setExtendedDetail("extra_field_5", "ForgotPassword-" + strTempString);
				contact.save("tcm:" + plId + "-" + TRIDION_RESET_PASSWORD_PAGE + "-64");   
			} else {
				contact.save("tcm:" + plId + "-" + emailPageTcmId + "-64");  
			}
			shStatus = "tridion-mail-sent";
		} catch(Exception e) {
			shStatus = "tridion-error";
		}
		return shStatus;
	}

	public Entity getUserHistory(HttpServletRequest request, UserHistory entity) {
		
        Calendar objCal1 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kuwait"));
        objCal1.set(Calendar.YEAR, objCal1.get(Calendar.YEAR) - 2);
        objCal1.set(Calendar.DAY_OF_MONTH, 1);
        objCal1.set(Calendar.HOUR_OF_DAY, 0);
        objCal1.set(Calendar.MINUTE, 0);
        objCal1.set(Calendar.SECOND, 0);
        objCal1.set(Calendar.MILLISECOND, 0);
        objCal1.getTimeInMillis();
		
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			SHSurveyUser shSurveyUser = (SHSurveyUser)request.getSession().getAttribute("shSurveyUser");	
            
			String strQuery = "select * userActivityLog where b.actionType='" + UserActivityLog.ACTIVITY_VOTE + "' and a.shUserId=? and actionTime>=?";
			PreparedStatement ps = connection.prepareStatement(strQuery);
	        ps.setLong(1, shSurveyUser.getId());
	        ps.setTimestamp(2, new Timestamp(objCal1.getTimeInMillis()));
	        ResultSet objRs = ps.executeQuery();
	        if(objRs.next()) {
	        	entity.setLastVoteDays(5);
	        }
	        objRs.close();
	        ps.close();
	        
			//strQuery = "select count(*) from userActivityLog where b.actionType='" + UserActivityLog.ACTIVITY_VOTE + "' and a.shUserId=? and actionTime>=?";
			strQuery = "select numberOfVotes, votingAverage from serviceHeroUsersExtra where shUserId=?";
	        ps = connection.prepareStatement(strQuery);
	        ps.setLong(1, shSurveyUser.getId());
	        objRs = ps.executeQuery();
	        if(objRs.next()) {
	        	entity.setNumberOfVotes(objRs.getInt(1));
	        	entity.setVoteAverage(objRs.getDouble(2));
	        }
	        objRs.close();
	        ps.close();
		} catch(Exception e) {
			//shStatus = "tridion-error";
		} finally {
			Utils.closeQuietly(connection);
		}
    	return entity;
	}
	
	/*public static void addNotificationCount(String emailAddress) {
		int newNotificationCount = 0;
		try { 
			Contact contact;
			contact = new Contact(new String[]{emailAddress, "servicehero"});
			try {
				newNotificationCount = Integer.parseInt((String)contact.getExtendedDetail("extra_field_10"));
			} catch(Exception px) {
				newNotificationCount = 0;
			}
			newNotificationCount = newNotificationCount + 1;
			contact.setExtendedDetail("extra_field_10", String.valueOf(newNotificationCount));
			contact.save();
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		} finally {
		}
	}*/
}
