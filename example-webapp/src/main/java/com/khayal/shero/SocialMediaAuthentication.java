package com.khayal.shero;

import com.sdl.dxa.modules.generic.utilclasses.PasswordEncryption;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.TreeMap;
import java.util.UUID;

public class SocialMediaAuthentication {
	
	
	
	/* 
	Create table serviceHeroUsersSocial (
		shUserId number,
		socialMedia number,
		socialMediaId varchar2(100),
		hashVal varchar2(100)
	)
	*/
	private int id;
	private String emailId;
	private String password;
	
	public static final int SOCIAL_MEDIA_FACEBOOK = 1;
	public static final int SOCIAL_MEDIA_GOOGLEPLUS = 2;
	public static final int SOCIAL_MEDIA_FACEBOOK_APP = 3;



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		if (emailId == null || emailId.equals(""))
            this.emailId = "N/A";
        else
            this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password.equals(""))
            this.password = "N/A";
        else
            this.password = password;
	}

	private static Logger LOG = LoggerFactory.getLogger(SocialMediaAuthentication.class);
	
	
	
	public boolean validateSocialUser(HttpServletRequest request, HttpServletResponse response, int socialMedia, String socialMediaId, String emailAddress, String profileName, String gender ) {
		SHSurveyUser shSurveyUser = null;
		long shUserId = 0;
		boolean status = false;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try{
	        HttpSession session = request.getSession();
			String userLanguage = (String) session.getAttribute("userLanguage");
			if(userLanguage == null || userLanguage.length() == 0)
				userLanguage = "en";
			String userCountry = (String) session.getAttribute("userCountry");
			if(userCountry == null || userCountry.length() == 0)
				userCountry = "kw";
			
		    String shStatus = SHSurveyUser.validateUserForSurvey(request, response, userLanguage, emailAddress, password, "social");
		    if(shStatus.equals("user-valid-social-match")) {
			    shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			    shUserId = shSurveyUser.getId();
		    } else if(shStatus.equals("user-new-email-social")) {
		    	shSurveyUser = new SHSurveyUser(request, userLanguage); 
				shSurveyUser.setEmailAddress(emailAddress);
				String strSalt = PasswordEncryption.gensalt();
				shSurveyUser.setEncryptionSalt(strSalt);
				shSurveyUser.setCountryCode(userCountry);
				String time = String.valueOf(System.currentTimeMillis());
				password = "s" + time.charAt(1) + "h" + time.charAt(9) + "e" + time.charAt(7) + "r" + time.charAt(3) + "0";
				password = PasswordEncryption.hashpw(password, strSalt);
				shSurveyUser.setPassword(password);
				
				int result = shSurveyUser.insertNewUser(socialMedia);
				shUserId = shSurveyUser.getId();
				request.getSession().setAttribute("shSurveyUser", shSurveyUser);
				
				/*
				String pattern = "", strTempString = "";
		        pattern = "yyyy.MM.dd HH:mm:ss";
		        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(pattern);
				Contact contact = new Contact();
				contact.setEmailAddress(emailAddress);
	            contact.setExtendedDetail("IDENTIFICATION_SOURCE", "servicehero");
	            contact.setAddressBookId(1010);
	            contact.setSubscribeStatusID(com.tridion.marketingsolution.profile.Contact.SUBSCRIBE_STATUS_SUBSCRIBED);
	            contact.setExtendedDetail("mail", emailAddress);
	            contact.setExtendedDetail("password_salt", encryptionSalt);
	            contact.setExtendedDetail("password", password);
	            strTempString = format.format(new java.util.Date(System.currentTimeMillis())).toString();
		        contact.setExtendedDetail("extra_field_1", strTempString);
		        contact.setExtendedDetail("extra_field_4", CSHeroFunctions.getNameAddressing((String) objTMDetails.get("first_name"), (String) objTMDetails.get("last_name"), sheroUser.getGender(), MyHttpServlet.getLanguageId(userLanguage)));
		        
	            int[] keywords;
	            keywords = new int[] {635, 636};
	            if(preferedLanguage.equals("en")) {
	            	contact.removeKeyword("tcm:10-" + keywords[1] + "-1024");  // boolean No
					contact.addKeyword("tcm:9-" + keywords[0] + "-1024");  // boolean No
				} else {
					contact.removeKeyword("tcm:9-" + keywords[0] + "-1024");  // boolean No
					contact.addKeyword("tcm:10-" + keywords[1] + "-1024");  // boolean No
				}   		
	            contact.save("tcm:" + (preferedLanguage.equals("en")?"en":"ar")+ "-0000-64");   
	            */
		    }
				
			connection = dataSource.getConnection();
			PreparedStatement ps;
		    ResultSet objRs;
			ps = connection.prepareStatement("select hashVal from serviceHeroUsersSocial where shUserId=? and socialMedia=? and socialMediaId=?");
			ps.setLong(1, shUserId);
			ps.setInt(2, socialMedia);
			ps.setString(3, socialMediaId);
			objRs = ps.executeQuery();
			UUID uniqueid = null;
			String shashVal = "";
			PreparedStatement ps1 = null;
			if(objRs.next()) {
				shashVal = objRs.getString(1);
			}
			objRs.close();
			ps.close();
			if(shashVal == null || shashVal.length() == 0) {
				uniqueid = UUID.randomUUID();
                shashVal =  uniqueid.toString().replace("-", "");
                ps1 = connection.prepareStatement("insert into serviceHeroUsersSocial values(?,?,?,?)");
                ps1.setLong(1, shUserId);
                ps1.setInt(2, socialMedia);
                ps1.setString(3, socialMediaId);
                ps1.setString(4, shashVal);
                ps1.executeUpdate();
                ps1.close();
                connection.commit();
			}
			session.setAttribute("shashVal", shashVal);
			boolean setProfile = false;
			if(shSurveyUser != null) {
				if(profileName != null)
					if(shSurveyUser.getPersonName() == null || shSurveyUser.getPersonName().length() == 0) {
						session.setAttribute("personName", profileName);
						setProfile = true;
					}
				/* only update gender if it is 637 or 638 and if it is 659 then only update if db gender is null
				   check that gender is valid 659				637 				638 */
				if(gender.equals("637") || gender.equals("638") || gender.equals("659")) {
					if(shSurveyUser.getGender() == null || shSurveyUser.getGender().length() == 0) {
						if(shSurveyUser.getGender()!= gender  && !gender.equals("659")) {
							session.setAttribute("gender", gender);
							setProfile = true;
						}
					}
				}
				
				if(setProfile) {
					shSurveyUser.updateSHUserValues();
					//shSurveyUser.updateSHUserExtraValues();
				}
				
				if(!shSurveyUser.getActivated().equals("Y"))
					SHSurveyUser.activateUser(request, shUserId);
				UserActivityLog.setActivityVariables(request); 
				UserActivityLog userLog = new UserActivityLog();
				if(socialMedia == SOCIAL_MEDIA_FACEBOOK)
					userLog.insertRecord(request, connection,  UserActivityLog.ACTIVITY_SURVEY_TABLE ,UserActivityLog.ACTIVITY_FB_LOGIN, shUserId, 0, "");

				else if(socialMedia == SOCIAL_MEDIA_GOOGLEPLUS) 
					userLog.insertRecord(request, connection,  UserActivityLog.ACTIVITY_SURVEY_TABLE ,UserActivityLog.ACTIVITY_GP_LOGIN, shUserId, 0, "");

				
				if(shSurveyUser.getSocialMediaWallPost() != null && !shSurveyUser.getSocialMediaWallPost().equals("Y")) {
					if(socialMedia == SOCIAL_MEDIA_FACEBOOK)  {
						session.setAttribute("publishWallMessage", "true");
						TreeMap fbFriends = (TreeMap) request.getSession().getAttribute("fbFriends");
						if(profileName != null) {
							//sheroUser.setPersonName(profileName);
							//sheroUser.updateFBFriends(socialMediaId, fbFriends, true);
						} else {
							//sheroUser.updateFBFriends(socialMediaId, fbFriends);
						}
					}
				}
			}
	    	status = true;
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
	    	status = false;
		}finally {
			Utils.closeQuietly(connection);
		}
		return status;
	}	
	
	/*public boolean loginUser(HttpServletRequest request, int socialMedia, String socialMediaId, String emailAddress, String profileName, String gender ) {
		SHSurveyUser shSurveyUser = null;
		long shUserId = 0;
		boolean statusLogin = false;
		try{
	        HttpSession session = request.getSession();
			String userLanguage = (String) session.getAttribute("userLanguage");
	    	ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
	    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
			Connection connection = dataSource.getConnection();
		    String shStatus = SHSurveyUser.validateUserForSurvey(request, userLanguage, emailAddress, password, "social");
		    if(shStatus.equals("user-valid-social-match")) {
		    	statusLogin = true;
			    shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");
			    shUserId = shSurveyUser.getId();
			    PreparedStatement ps;
			    ResultSet objRs;
				if(shUserId > 10000) {
					if(profileName != null) 
						shSurveyUser.setPersonName(profileName);
					UserActivityLog.setActivityVariables(request); 
					UserActivityLog userLog = new UserActivityLog();
					if(socialMedia == SOCIAL_MEDIA_FACEBOOK)
						userLog.insertRecord(request, connection, UserActivityLog.ACTIVITY_FB_LOGIN, shUserId, 0, "");
					else if(socialMedia == SOCIAL_MEDIA_GOOGLEPLUS) 
						userLog.insertRecord(request, connection, UserActivityLog.ACTIVITY_GP_LOGIN, shUserId, 0, "");

					//PreparedStatement ps = objCon.prepareStatement("SELECT OAUTHPRVDRID, HASHVAL FROM SHEROOAUTHPRVDR WHERE SHUSERID = ? AND PRVDRTYP = ? AND OAUTHPRVDRID = ? ");
					ps = connection.prepareStatement("select socialMediaId, hashval from serviceHeroUsersSocial where shUserId=? and socialMedia? and socialMediaId=?");
					ps.setLong(1, shUserId);
					ps.setInt(2, socialMedia);
					ps.setString(3, socialMediaId);
					objRs = ps.executeQuery();
					UUID uniqueid = null;
					String shashVal = null;
					PreparedStatement ps1 = null;
					if(objRs == null) {
		                uniqueid = UUID.randomUUID();
		                shashVal =  uniqueid.toString().replace("-","");
	                    ps1 = connection.prepareStatement("Insert into serviceHeroUsersSocial values(?,?,?,?)");
	                    ps1.setLong(1, shUserId);
	                    ps1.setInt(2, socialMedia);
	                    ps1.setString(3, socialMediaId);
	                    ps1.setString(4, shashVal);
	                    ps1.executeUpdate();
                        ps1.close();
                        connection.commit();
					} else {
						while (objRs.next()) {
							if(objRs.getString(2) == null ||  objRs.getString(2).length() < 1){
								ps1 = connection.prepareStatement("update serviceHeroUsersSocial set hashval=? where shUserId=? and socialMediaType=? and socialMediaId=?");
								uniqueid = UUID.randomUUID();
								shashVal =  uniqueid.toString().replace("-","");
								ps1.setString(1, shashVal);
								ps1.setLong(2, shUserId);
								ps1.setInt(3, socialMedia);
								ps1.setString(4, socialMediaId);
								ps1.executeUpdate();
							}else{
								shashVal = objRs.getString(2).toString();
							}
						}
					}
					ps1.close();
					objRs.close();
					ps.close();
					session.setAttribute("shashVal", shashVal);
				}
					
		    } else {
		    	
		    }
			connection.close();
			if(shSurveyUser != null) {
				if(!shSurveyUser.getActivated().equals("Y"))
					SHSurveyUser.activateUser(request, shUserId);
				if(!shSurveyUser.getSocialMediaWallPost().equals("Y")) {
					if(socialMedia == SOCIAL_MEDIA_FACEBOOK)  {
						TreeMap fbFriends = (TreeMap) request.getSession().getAttribute("fbFriends");
						if(profileName != null) {
							//sheroUser.setPersonName(profileName);
							//sheroUser.updateFBFriends(socialMediaId, fbFriends, true);
						} else {
							//sheroUser.updateFBFriends(socialMediaId, fbFriends);
						}
					}
				}
			}
			
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
		}
		
		return statusLogin;
	}	
	
	public boolean createUser(HttpServletRequest request, int socialMedia, String socialMediaId, String emailAddress, String profileName, String gender) {
		boolean statusLogin = false;
		try{
			HttpSession session = request.getSession();
	        ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
	    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
			Connection connection = dataSource.getConnection();

			String userLanguage = (String) session.getAttribute("userLanguage");
	    	SHSurveyUser shSurveyUser = new SHSurveyUser(request, userLanguage); 
			shSurveyUser.setEmailAddress(emailAddress);
			String strSalt = PasswordEncryption.gensalt();
			shSurveyUser.setEncryptionSalt(strSalt);
			shSurveyUser.setCountryCode((String)session.getAttribute("userCountry"));
			
			password = "currency";
			password = PasswordEncryption.hashpw(password, strSalt);
			shSurveyUser.setPassword(password);
			
			int result = shSurveyUser.insertNewUser();
			if(result == 2) {
				long shUserId = shSurveyUser.getId();
				if(!shSurveyUser.getActivated().equals("Y"))
					SHSurveyUser.activateUser(request, shUserId);
				if(!shSurveyUser.getSocialMediaWallPost().equals("Y")) {
					if(socialMedia == SOCIAL_MEDIA_FACEBOOK)  {
						TreeMap fbFriends = (TreeMap) request.getSession().getAttribute("fbFriends");
						if(profileName != null) {
							//sheroUser.setPersonName(profileName);
							//sheroUser.updateFBFriends(socialMediaId, fbFriends, true);
						} else {
							//sheroUser.updateFBFriends(socialMediaId, fbFriends);
						}
					}
				}

				if(profileName != null)
					session.setAttribute("personName", profileName);
				session.setAttribute("gender", gender);
				shSurveyUser.updateSHUserValues();
				
				//shSurveyUser.updateSHUserExtraValues();
				PreparedStatement ps;
			    ps = connection.prepareStatement("insert into serviceHeroUsersSocial values(?,?,?,?)");
		        UUID uniqueid = UUID.randomUUID();
		        String shashVal =  uniqueid.toString().replace("-","");
	            ps.setInt(1, id);
	            ps.setInt(2, socialMedia);
	            ps.setString(3, socialMediaId);
		        ps.setString(4, shashVal);
	            ps.executeUpdate();
	            ps.close();
				session.setAttribute("shashVal", shashVal);
			}
			statusLogin = true;
			connection.commit();
			connection.close();	
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
		}
		return statusLogin;
	}*/
}
