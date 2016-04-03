package com.khayal.shero;

import com.sdl.dxa.modules.generic.utilclasses.*;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServiceHeroUser {
    /*
    serviceHeroUsers: uniqueId, emailAddress, password, encryptionSalt, preferedLanguage, countryCode, 
    				personName, mobileNumber, civilId, gender, ageGroup, dateOfBirth, nationality, governorate, education      
    Create table serviceHeroUsers (
    	id number constraint pk_shuId_serviceHeroUsers primary key,
		emailAddress varchar2(100),
		password varchar2(1024),
		encryptionSalt varchar2(1024),
		preferedLanguage varchar2(50),
		countryCode varchar2(50), 
		personName varchar2(100), 
		mobileNumber varchar2(50), 
		civilId number, 
		gender varchar2(50), 
		ageGroup varchar2(50),
		dateOfBirth number,
		nationality varchar2(50),
		governorate varchar2(50),
		education varchar2(50)
	);
	insert into serviceHeroUsers values(10000, 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 0, 'N/A', 'N/A', 0, 'N/A', 'N/A', 'N/A');
	CREATE SEQUENCE serviceHeroUser_Seq MINVALUE 10001 MAXVALUE 9999999999999999 INCREMENT BY 1 START WITH 10001 CACHE 20 NOORDER NOCYCLE;
    */

	private static Logger LOG = LoggerFactory.getLogger(ServiceHeroUser.class);

	private String userLanguage;
	private HttpServletRequest httpServletRequest;
	private long id;
	private String emailAddress;
	private String password;
	private String encryptionSalt;
	private String preferedLanguage;
	private String countryCode;

	private String personName;
	private String mobileNumber;
	private long civilId;
	private String gender;
	private String ageGroup;
	private long dateOfBirth;
	private String nationality;
	private String residence;
	private String governorate;
	private String education;
	private FormFieldValidator formFieldValidator;

	public ServiceHeroUser(HttpServletRequest httpServletRequest, String userLanguage) {
		this.httpServletRequest = httpServletRequest;
		this.userLanguage = userLanguage;
		emailAddress = "N/A";
		password = "N/A";
		encryptionSalt = "N/A";
		preferedLanguage = userLanguage;
		personName = "N/A";
		mobileNumber = "N/A";
		civilId = 0;
		gender = "N/A";
		ageGroup = "N/A";
		dateOfBirth = 0;
		nationality = "N/A";
		residence ="N/A" ;
		governorate = "N/A";
		education = "N/A";

	}

	public ServiceHeroUser(HttpServletRequest httpServletRequest, String userLanguage, long shUserId) {
		this.httpServletRequest = httpServletRequest;
		this.userLanguage = userLanguage;
		this.id = shUserId;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("select * from serviceHeroUsers where id=?");
			ps.setLong(1, shUserId);
			ResultSet objRs = ps.executeQuery();
			if (objRs.next()) {
				setSHUserValues(objRs);
			}
			objRs.close();
			ps.close();
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
	}

	public void setSHUserValues(ResultSet objRs) {
		try {
			id = objRs.getLong(1);
			emailAddress = objRs.getString(2);
			password = objRs.getString(3);
			encryptionSalt = objRs.getString(4);
			preferedLanguage = objRs.getString(5);
			countryCode = objRs.getString(6);

			personName = objRs.getString(7);
			mobileNumber = objRs.getString(8);
			civilId = objRs.getLong(9);
			gender = objRs.getString(10);
			ageGroup = objRs.getString(11);
			dateOfBirth = objRs.getLong(12);
			nationality = objRs.getString(13);
			governorate = objRs.getString(14);
			education = objRs.getString(15);
			residence = objRs.getString(16);
		} catch(Exception e) {

		}
	}

	public String getUserLanguage() {
		return userLanguage;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncryptionSalt() {
		return encryptionSalt;
	}

	public void setEncryptionSalt(String encryptionSalt) {
		this.encryptionSalt = encryptionSalt;
	}

	public String getPreferedLanguage() {
		return preferedLanguage;
	}

	public void setPreferedLanguage(String preferedLanguage) {
		this.preferedLanguage = preferedLanguage;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public long getCivilId() {
		return civilId;
	}

	public void setCivilId(long civilId) {
		this.civilId = civilId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public long getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getResidence() {
		return residence;
	}

	public void setResidence(String residence) {
		this.residence = residence;
	}

	public String getGovernorate() {
		return governorate;
	}

	public void setGovernorate(String governorate) {
		this.governorate = governorate;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public int insertNewUser(int activityType) {
		int result = 0;


		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("Select serviceHeroUser_Seq.nextVal from dual");
			ResultSet objRs = ps.executeQuery();
			if(objRs.next())
				id = objRs.getLong(1);
			objRs.close();
			ps.close();
			ps = connection.prepareStatement("insert into serviceHeroUsers (id, emailAddress, password, encryptionSalt, preferedLanguage, countryCode) values(?,?, ?,?, ?,?)");
			ps.setLong(1, id);
			ps.setString(2, emailAddress);
			ps.setString(3, password);
			ps.setString(4, encryptionSalt);
			ps.setString(5, preferedLanguage);
			ps.setString(6, countryCode);

			result = ps.executeUpdate();
			ps.close();

			ps = connection.prepareStatement("insert into serviceHeroUsersExtra (shUserId, activated, numberOfVotes, votingAverage) values(?,?, ?,?)");
			ps.setLong(1, id);
			ps.setString(2, "N");
			ps.setLong(3, 0);
			ps.setLong(4, 0);

			result += ps.executeUpdate();
			ps.close();

			UserActivityLog userLog = new UserActivityLog();
			if(activityType == SocialMediaAuthentication.SOCIAL_MEDIA_FACEBOOK)
				userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , UserActivityLog.ACTIVITY_FB_REGISTER, id, 0, "");

			else if(activityType == SocialMediaAuthentication.SOCIAL_MEDIA_GOOGLEPLUS)
				userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , UserActivityLog.ACTIVITY_GP_REGISTER, id, 0, "");

			else
				userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , UserActivityLog.ACTIVITY_WEB_REGISTER, id, 0, "");

			connection.commit();
			connection.close();

			Contact contact = new Contact();
			contact.setEmailAddress(emailAddress);
			contact.setExtendedDetail("IDENTIFICATION_SOURCE", "servicehero");
			contact.setAddressBookId(1020);
			contact.setSubscribeStatusID(com.tridion.marketingsolution.profile.Contact.SUBSCRIBE_STATUS_SUBSCRIBED);
			contact.setExtendedDetail("mail", emailAddress);
			contact.setExtendedDetail("password_salt", encryptionSalt);
			contact.setExtendedDetail("password", password);

			String pattern, strTempString = "";
			pattern = "yyyy.MM.dd HH:mm:ss";
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(pattern);
			strTempString = format.format(new java.util.Date(System.currentTimeMillis())).toString();
			contact.setExtendedDetail("extra_field_1", strTempString);

			int[] keywords;
			keywords = new int[] {635, 636};
			if(preferedLanguage.equals("en")) {
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
			contact.save("tcm:" + (preferedLanguage.equals("en")?"9":"10") + "-" + SHSurveyUser.TRIDION_REGISTRATION_PAGE + "-64");    // Add URL for automated email
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
		return result;
	}

	public int updateSHUserValues() {
		// TODO Auto-generated method stub
		int result = 0, count = 0;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		//LOG.error("Entered updateSHUserValues");
		try {
			String strParam = "", strTemp = "", strName = "";
			DatabaseQueryBuilder dbQuery = new DatabaseQueryBuilder(DatabaseQueryBuilder.QUERY_UPDATE, "serviceHeroUsers");
			List<DatabaseQueryField> columnNameValues = new ArrayList<>();
			setupFieldValidations();
			String strNewOptionValue;
			boolean isValidParameterValue = true;
			Contact contact =  null;
			try {
				contact = new Contact(new String[]{getEmailAddress(), "servicehero"});
			}catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
			int[] keywords;
			int plId = getPreferedLanguage().equals("en") ? 9 : 10;

			strParam = "preferedLanguage";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					//LOG.error("user profile validate preferedLanguage");
					formFieldValidator.validate(strParam, strTemp);

				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					LOG.error("user profile validate preferedLanguage caught exception "+fe.getMessage());
					LOG.error(strParam+ " - "+strTemp+" is not valid");
					result = -1;
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;

				keywords = new int[] {635, 636};
				try {
					if (strTemp.equals("en")) {
						contact.removeKeyword("tcm:10-" + keywords[0] + "-1024");
						contact.addKeyword("tcm:9-" + keywords[1] + "-1024");
						plId = 9;
					} else {
						contact.removeKeyword("tcm:9-" + keywords[1] + "-1024");
						contact.addKeyword("tcm:10-" + keywords[0] + "-1024");
						plId = 10;

					}
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}

				strName = "";
				if(gender != null && gender.equals("637")) {
					strName += ((plId == 9)?"Dear":"عزيزي");  // male
				} else if(gender != null && gender.equals("638")) {
					strName += ((plId == 9)?"Dear":"عزيزتي");
				} else
					strName += ((plId == 9)?"Dear":"عزيزتي");
				try {
					strName += " "  + contact.getExtendedDetail("first_name");
					if(contact.getExtendedDetail("last_name") != null && !contact.getExtendedDetail("last_name").equals(""))
						strName += " " + contact.getExtendedDetail("last_name");
					strName += ((plId == 9)?",":"،");
					contact.setExtendedDetail("extra_field_4", strName);
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
			}

			strParam = "countryCode";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					formFieldValidator.validate(strParam, strTemp);

				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -2;
					LOG.error(strParam+ " - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;

				keywords = new int[] {391, 390};
				try {
					for (int i = 0 ; i < keywords.length; i++) {
						contact.removeKeyword("tcm:9-" + keywords[i] + "-1024");
						contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");
					}
					if(strTemp.equals("kw")) {
						contact.addKeyword("tcm:" + plId +  "-" + keywords[0] + "-1024");
					} else {
						contact.addKeyword("tcm:" + plId +  "-" + keywords[1] + "-1024");
					}
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
			}

			strParam = "personName";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					//LOG.error("user profile validate personName  ");
					formFieldValidator.validate(strParam, strTemp);
				} catch (FormFieldValidatorException fe) {
					result = -3;
					isValidParameterValue = false;
					LOG.error("user profile validate personName caught exception "+fe.getMessage());
					LOG.error(strParam+ " - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
				try{
					if(strTemp.indexOf(" ") > -1) {
						contact.setExtendedDetail("first_name", strTemp.substring(0, strTemp.indexOf(" ")));
						contact.setExtendedDetail("last_name", strTemp.substring(strTemp.indexOf(" ") + 1));
					} else {
						contact.setExtendedDetail("first_name", strTemp);
						contact.setExtendedDetail("last_name", "");
					}


					strName = "";
					if(gender != null && gender.equals("637")) {
						strName += ((plId == 9)?"Dear":"عزيزي");  // male
					} else if(gender != null && gender.equals("638")) {
						strName += ((plId == 9)?"Dear":"عزيزتي");
					} else
						strName += ((plId == 9)?"Dear":"عزيزتي");
					strName += " "  + strTemp;
					strName += ((plId == 9)?",":"،");
					contact.setExtendedDetail("extra_field_4", strName);
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
			}

			strParam = "mobileNumber";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				//LOG.error("user profile validate mobileNumber ");
				try {
					formFieldValidator.validate(strParam, strTemp);
				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -4;
					LOG.error("user profile validate mobileNumber caught exception "+fe.getMessage());
					LOG.error("MobileNumber "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
				try{
					contact.setExtendedDetail("mobile_number", strTemp);
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
			}

			strParam = "encryptionSalt";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
				contact.setExtendedDetail("password_salt", strTemp);
			}

			strParam = "password";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
				try{
					contact.setExtendedDetail("password", strTemp);
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
			}

			strParam = "civilId";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
				try {
					contact.setExtendedDetail("first_name", strTemp);
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
			}

			strParam = "gender";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					formFieldValidator.validate(strParam, strTemp);
				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -5;
					LOG.error(strParam+" - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;

				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "659";
				}
				if(!strTemp.equals("")) {
					keywords = new int[] {659, 637, 638};
					try{
						for (int i = 0 ; i < keywords.length; i++) {
							contact.removeKeyword("tcm:9-" + keywords[i] + "-1024");
							contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");
						}
						contact.addKeyword("tcm:" + plId +  "-" + strTemp + "-1024");
					}catch (Exception e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}

			strParam = "ageGroup";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					formFieldValidator.validate(strParam, strTemp);
				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -6;
					LOG.error(strParam+" - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;

				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "676";
				}
				if(!strTemp.equals("")) {
					keywords = new int[] {676, 651, 652, 653, 654, 655, 656, 657};
					try {
						for (int i = 0; i < keywords.length; i++) {
							contact.removeKeyword("tcm:9-" + keywords[i] + "-1024");
							contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");
						}
						contact.addKeyword("tcm:" + plId + "-" + strTemp + "-1024");
					}catch (Exception e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}

			strParam = "dateOfBirth";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				LOG.error("user profile validate dateOfBirth "+strTemp);

				try {

					formFieldValidator.validate(strParam, strTemp);
					strTemp = strTemp.substring(6) + "" + strTemp.substring(3, 5) + "" + strTemp.substring(0, 2);
					java.util.Calendar objDOBCal = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("Asia/Kuwait"));
					java.util.Calendar objCurrCal = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("Asia/Kuwait"));
		            objDOBCal = DateFunctions.getDateFromStringFixTime(strTemp);

		            int iAge = objCurrCal.get(Calendar.YEAR) - objDOBCal.get(Calendar.YEAR);

					if (objCurrCal.get(Calendar.DAY_OF_YEAR) < objDOBCal.get(Calendar.DAY_OF_YEAR))
			            iAge--;
					int minValidAgeToVote = 14, maxValidAgeToVote = 100;

		            if( iAge < minValidAgeToVote  ||  iAge > maxValidAgeToVote)  {
			            LOG.error(strParam+" - "+strTemp+" is not within age range ");
			            //throw new FormFieldValidatorException( "DOB is not within age range ");
			            result = -7;
		            }

				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -8;
					LOG.error("user profile validate dateOfBirth caught exception "+fe.getMessage());
					LOG.error(strParam+" - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}

				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
				try{
					contact.setExtendedDetail("birth_date", strTemp);
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
			}

			strParam = "nationality";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					formFieldValidator.validate(strParam, strTemp);
				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -9;
					LOG.error(strParam+" - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;

				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "675";
				}
				if(!strTemp.equals("")) {
					keywords = new int[] {675, 639, 640, 658, 33297};
					try{
						for (int i = 0 ; i < keywords.length; i++) {
							contact.removeKeyword("tcm:9-" + keywords[i] + "-1024");
							contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");
						}
						contact.addKeyword("tcm:" + plId +  "-" + strTemp + "-1024");
					}catch (Exception e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}

			strParam = "residence";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					formFieldValidator.validate(strParam, strTemp);
				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -10;
					LOG.error(strParam+" - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;

				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "675";
				}
				if(!strTemp.equals("")) {
					keywords = new int[] {675, 639, 640, 658, 33297};
					try{
						for (int i = 0 ; i < keywords.length; i++) {
							contact.removeKeyword("tcm:9-" + keywords[i] + "-1024");
							contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");
						}
						contact.addKeyword("tcm:" + plId +  "-" + strTemp + "-1024");
					}catch (Exception e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}

			strParam = "governorate";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					formFieldValidator.validate(strParam, strTemp);
				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -11;
					LOG.error(strParam+" - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;

				strNewOptionValue = governorate;
				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "677";
				}
				if(!strTemp.equals("")) {
					keywords = new int[] {677, 645, 646, 647, 648, 649, 650, 26286, 26287, 26288, 26289, 26290, 26291, 26292};
					try{
						for (int i = 0 ; i < keywords.length; i++) {
							contact.removeKeyword("tcm:9-" + keywords[i] + "-1024");
							contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");
						}
						contact.addKeyword("tcm:" + plId +  "-" + strTemp + "-1024");
					}catch (Exception e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}

			strParam = "education";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute(strParam);
				try {
					formFieldValidator.validate(strParam, strTemp);
				} catch (FormFieldValidatorException fe) {
					isValidParameterValue = false;
					result = -12;
					LOG.error(strParam+" - "+strTemp+" is not valid");
					//System.out.println("Name "+strFieldValue+" is not valid");
				}
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;

				strNewOptionValue = education;
				if(strTemp == null || strTemp.length() == 0) {
					strTemp = "681";
				}
				if(!strTemp.equals("")) {
					keywords = new int[] {681, 660, 661, 678, 679, 680};
					try{
						for (int i = 0 ; i < keywords.length; i++) {
							contact.removeKeyword("tcm:9-" + keywords[i] + "-1024");
							contact.removeKeyword("tcm:10-" + keywords[i] + "-1024");
						}
						contact.addKeyword("tcm:" + plId +  "-" + strTemp + "-1024");
					}catch (Exception e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}

			dbQuery.setColumnNameValues(columnNameValues);

			List<DatabaseQueryField> identifierNameValues = new ArrayList<>();
			identifierNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "id", DatabaseQueryField.OPERATOR_EQUAL, String.valueOf(id), DatabaseQueryField.CONNECTOR_BLANK));
			dbQuery.setIdentifierNameValues(identifierNameValues);

			if(count > 0 && isValidParameterValue && result > -1) {
				LOG.error("ServiceHeroUser: updateSHUserValues - " + dbQuery.getQuery());
				connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(dbQuery.getQuery());
				result = ps.executeUpdate();
				ps.close();
				connection.commit();
				//contact.save(emailPageTcmId);    // Add URL for automated email
				try{
					contact.save();    // Add URL for automated email
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
				//updateTridionContact("tcm:10-0000-64");
			} else if(result == 0 ){
				result = 0;
			}
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
		return result;
	}

	public static boolean validateEmailAddress(String emailaddress){
		boolean isValid = false;
		if (emailaddress != null && emailaddress.length() > 0 && !emailaddress.trim().equalsIgnoreCase("0")){
			String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)" ;
			isValid = emailaddress.matches(regex) ;
		}
		return isValid;
	}

	public void setupFieldValidations()
	{
		formFieldValidator = new FormFieldValidator();
		try{

			formFieldValidator.addValidation("personName", FormFieldValidator.REQUIRED);
			formFieldValidator.addValidation("personName", FormFieldValidator.ALPHABETS_ONLY);
			formFieldValidator.setMaxLength("personName", 100);
			formFieldValidator.addSpecialCharacter("personName", FormFieldValidator.SPECIAL_CHARACTER_SPACE);
			formFieldValidator.addSpecialCharacter("personName", FormFieldValidator.SPECIAL_CHARACTER_HYPHEN);
			formFieldValidator.addSpecialCharacter("personName", FormFieldValidator.SPECIAL_CHARACTER_DOT);

			formFieldValidator.addValidation("mobileNumber", FormFieldValidator.REQUIRED);
			formFieldValidator.addValidation("mobileNumber", FormFieldValidator.INTL_PHONE_NO);

			formFieldValidator.addValidation("gender", FormFieldValidator.REQUIRED);
			formFieldValidator.setMaxLength("gender", 8);
			formFieldValidator.addValidation("gender", FormFieldValidator.NUMBERS_ONLY);

			formFieldValidator.addValidation("ageGroup", FormFieldValidator.REQUIRED);
			formFieldValidator.setMaxLength("ageGroup", 8);
			formFieldValidator.addValidation("ageGroup", FormFieldValidator.NUMBERS_ONLY);

			formFieldValidator.addValidation("dateOfBirth", FormFieldValidator.REQUIRED);
			formFieldValidator.addValidation("dateOfBirth", FormFieldValidator.DATE_DD_MM_YYYY);

			formFieldValidator.addValidation("nationality", FormFieldValidator.REQUIRED);
			formFieldValidator.setMaxLength("nationality", 8);
			formFieldValidator.addValidation("nationality", FormFieldValidator.NUMBERS_ONLY);

			formFieldValidator.addValidation("residence", FormFieldValidator.REQUIRED);
			formFieldValidator.setMaxLength("residence", 8);
			formFieldValidator.addValidation("residence", FormFieldValidator.NUMBERS_ONLY);

			formFieldValidator.addValidation("governorate", FormFieldValidator.REQUIRED);
			formFieldValidator.setMaxLength("governorate", 8);
			formFieldValidator.addValidation("governorate", FormFieldValidator.NUMBERS_ONLY);

			formFieldValidator.addValidation("education", FormFieldValidator.REQUIRED);
			formFieldValidator.addValidation("education", FormFieldValidator.NUMBERS_ONLY);
			formFieldValidator.setMaxLength("education", 8);

			formFieldValidator.addValidation("preferedLanguage", FormFieldValidator.REQUIRED);
			formFieldValidator.addValidation("preferedLanguage", FormFieldValidator.ALPHABETS_ONLY);
			formFieldValidator.setMaxLength("preferedLanguage", 2);

			formFieldValidator.addValidation("countryCode", FormFieldValidator.REQUIRED);
			formFieldValidator.addValidation("countryCode", FormFieldValidator.ALPHABETS_ONLY);
			formFieldValidator.setMaxLength("countryCode", 2);


		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(),e);
			//  System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public boolean isProfileComplete() {
        boolean completeProfile;
	    //completeProfile = true;
	    try {
		    completeProfile = !getPersonName().equals("") && !getPersonName().equals("0") && !getPersonName().equals("-") && getPersonName().length() > 0 && getNationality() != null && !getNationality().equals("N/A") && !getNationality().equals("675")  && !getNationality().equals("196")
		                      && getResidence() != null && !getResidence().equals("N/A") && !getResidence().equals("675")  && !getResidence().equals("196")
		                      && !getMobileNumber().equals("N/A") && !getMobileNumber().equals("-") && !getMobileNumber().equals("[-") && getMobileNumber().equals("N/A")
		                      && getDateOfBirth() != 0   && !getGender().equals("") && !getGender().equals("N/A") && getGender() != null  && !getGender().equals("659")
		                      && !getGovernorate().equals("") && !getGovernorate().equals("N/A") && getGovernorate() !=null && !getGovernorate().equals("677")  && !getEducation().equals("")
		                      && getEducation() !=null && !getEducation().equals("681") && !getEducation().equals("N/A") ;
	    } catch(Exception e) {
		    completeProfile = false;
	    }
        return completeProfile;
    }
}
