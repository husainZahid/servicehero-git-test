package com.khayal.shero;

import com.tridion.marketingsolution.profile.Contact;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ContactUsSubmission {
	/*
	SurveyVoting_kw: id, sHeroUseId, userLanguage, countryCode, parentCategoryCode, categoryCode, brandCode, dealerCode, lastVisit  
	Create table contactUsSubmission (
	    	id number constraint pk_CUSId_contactUsSubmission primary key,
			shUserId number,
			userLanguage varchar2(50),
			userCountry varchar2(50),
			personName varchar2(512),
			mobileNumber varchar2(512),
			contactEmail varchar2(512),
			queryType varchar2(100),
			queryRelatedTo varchar2(100),
			yourMessage varchar2(2000)
	);
	
	insert into contactUsSubmission values(10000, 10000, 'en', 'kw', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'Hi All this is a comment How do I look');
	CREATE SEQUENCE contactUsSubmission_Seq MINVALUE 10001 MAXVALUE 9999999999999999 INCREMENT BY 1 START WITH 10001 CACHE 2 NOORDER NOCYCLE;

	*/
	private static Logger LOG = LoggerFactory.getLogger(ContactUsSubmission.class);

    private String userLanguage;
    private HttpServletRequest httpServletRequest;
    
    //private ServiceHeroUser serviceHeroUser;
	//private ServiceHeroUserExtra serviceHeroUserExtra;
   
    public ContactUsSubmission(HttpServletRequest httpServletRequest, String userLanguage) {
        this.httpServletRequest = httpServletRequest;
        this.userLanguage = userLanguage;
        //serviceHeroUser = (ServiceHeroUser) httpServletRequest.getSession().getAttribute("shUser");
        //serviceHeroUserExtra = (ServiceHeroUserExtra) httpServletRequest.getSession().getAttribute("shUserExtra");
    }
	
    public String submitContactUsQuery() {
		int result = 0;
		String shStatus = "";
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			String countryCode = (String) httpServletRequest.getSession().getAttribute("userCountry");	
            if(countryCode == null || countryCode.length() == 0)
            	countryCode = "N/A";
            SHSurveyUser shSurveyUser = (SHSurveyUser)httpServletRequest.getSession().getAttribute("shSurveyUser");	
            
            if(!countryCode.equals("N/A")) {
				connection = dataSource.getConnection();
	    		
	    		long submitId = 0;
	    		String strQuery, personName, mobileNumber, emailAddress, queryType, queryRelatedTo, yourMessage;
	    		PreparedStatement ps = connection.prepareStatement("Select contactUsSubmission_Seq.nextVal from dual");
                ResultSet objRs = ps.executeQuery();
                if(objRs.next())
                	submitId = objRs.getLong(1);
                objRs.close();
                ps.close();
                if(submitId < 10001)
                	submitId = 10001;
                
                personName = (String) httpServletRequest.getParameter("personName");
                if(personName == null || personName.length() == 0)
                	personName = "N/A";
                
                mobileNumber = (String) httpServletRequest.getParameter("mobileNumber1");
                if(mobileNumber == null || mobileNumber.length() == 0)
                	mobileNumber = "N/A";

                emailAddress = (String) httpServletRequest.getParameter("contactEmail");
                if(emailAddress == null || emailAddress.length() == 0)
                	emailAddress = "N/A";

                queryType = (String) httpServletRequest.getParameter("queryType");
                if(queryType == null || queryType.length() == 0)
                	queryType = "N/A";

                queryRelatedTo = (String) httpServletRequest.getParameter("queryRelatedTo");
                if(queryRelatedTo == null || queryRelatedTo.length() == 0)
                	queryRelatedTo = "N/A";

                yourMessage = (String) httpServletRequest.getParameter("yourMessage");
                if(yourMessage == null || yourMessage.length() == 0)
                	yourMessage = "N/A";


                strQuery = "insert into contactUsSubmission values(?,?, ?,?, ?,?, ?,?, ?,?)";
				ps = connection.prepareStatement(strQuery);
				ps.setLong(1, submitId);
				if(shSurveyUser != null)
					ps.setLong(2, shSurveyUser.getId());
				else
					ps.setLong(2, 0);
				ps.setString(3, userLanguage);
				ps.setString(4, countryCode);
				ps.setString(5, personName);
				ps.setString(6, mobileNumber);
				ps.setString(7, emailAddress);
				ps.setString(8, queryType);
				ps.setString(9, queryRelatedTo);
				ps.setString(10, yourMessage);
				result = ps.executeUpdate();
				ps.close();
				
				if(result > 0) {
					if(shSurveyUser != null) {
						UserActivityLog userLog = new UserActivityLog();
						userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE ,UserActivityLog.ACTIVITY_CONTACT_US, shSurveyUser.getId(), submitId, "");
					}
					
				}
				if(result == 1)
					shStatus = "contact-us-submit";
				else
					shStatus = "contact-us-error";
				connection.commit();
				
				int plId = (userLanguage.equals("en") ? 9 : 10);

				Contact contact = new Contact();
				contact.setEmailAddress(emailAddress);
	            contact.setExtendedDetail("IDENTIFICATION_SOURCE", "contactus_" + System.currentTimeMillis());
	            contact.setAddressBookId(1040);
	            contact.setSubscribeStatusID(com.tridion.marketingsolution.profile.Contact.SUBSCRIBE_STATUS_SUBSCRIBED);
	            contact.setExtendedDetail("mail", emailAddress);
	            if(personName.indexOf(" ") > -1) {
					contact.setExtendedDetail("first_name", personName.substring(0, personName.indexOf(" ")));
				    contact.setExtendedDetail("last_name", personName.substring(personName.indexOf(" ") + 1));
			    } else {
					contact.setExtendedDetail("first_name", personName);
					contact.setExtendedDetail("last_name", "");
			    }
	            contact.setExtendedDetail("mobile_number", mobileNumber);
	            contact.setExtendedDetail("password_salt", "N/A");
	            contact.setExtendedDetail("password", "N/A");
	            
	            String pattern, strTempString = "";
	            pattern = "yyyy.MM.dd HH:mm:ss";
	            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(pattern);
	            strTempString = format.format(new java.util.Date(System.currentTimeMillis())).toString();
	            contact.setExtendedDetail("extra_field_1", strTempString);
	            
	            String strName = ((plId == 9)?"Dear":"عزيزتي");
			    strName += " "  + personName;
			    strName += ((plId == 9)?",":"،");
			    contact.setExtendedDetail("extra_field_4", strName);
	    		
	            int[] keywords;
	            keywords = new int[] {635, 636};
	            if(userLanguage.equals("en")) {
	            	contact.removeKeyword("tcm:10-" + keywords[0] + "-1024");  // boolean No
					contact.addKeyword("tcm:9-" + keywords[1] + "-1024");  // boolean No
				} else {
					contact.removeKeyword("tcm:9-" + keywords[1] + "-1024");  // boolean No
					contact.addKeyword("tcm:10-" + keywords[0] + "-1024");  // boolean No
				} 
	            
	            keywords = new int[] {391, 390};
	            if(countryCode.equals("kw")) {
	            	contact.removeKeyword("tcm:10-" + keywords[0] + "-1024");  
					contact.addKeyword("tcm:9-" + keywords[1] + "-1024");  
				} else {
					contact.removeKeyword("tcm:9-" + keywords[1] + "-1024"); 
					contact.addKeyword("tcm:10-" + keywords[0] + "-1024"); 
				}
	            contact.save("tcm:" + (userLanguage.equals("en")?"9":"10") + "-" + SHSurveyUser.TRIDION_COUNTACT_US_PAGE + "-64");    // Add URL for automated email
			}
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
			shStatus = "vote-error";
		}finally {
			Utils.closeQuietly(connection);
		}
		return shStatus;
	}
}
