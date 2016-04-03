package com.khayal.shero;

import com.sdl.dxa.modules.generic.utilclasses.DatabaseQueryBuilder;
import com.sdl.dxa.modules.generic.utilclasses.DatabaseQueryField;
import com.sdl.dxa.modules.generic.utilclasses.PasswordEncryption;
import com.tridion.marketingsolution.profile.Contact;
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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SHBrandUser {
	
	private static Logger LOG = LoggerFactory.getLogger(SHBrandUser.class);
	public static final int TRIDION_RESET_PASSWORD_PAGE = 26405;
	
	private String userLanguage;
	private HttpServletRequest httpServletRequest;
	
	private long shUserId;
	private String shUserName;
	private String emailAddress;
	private long startDate;
	private long endDate;
	private String active;
	
    private String assignedBrands;

		
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

	public long getShUserId() {
		return shUserId;
	}

	public void setShUserId(long shUserId) {
		this.shUserId = shUserId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getAssignedBrands() {
		return assignedBrands;
	}

	public void setAssignedBrands(String assignedBrands) {
		this.assignedBrands = assignedBrands;
	}

	public String getShUserName() {
		return shUserName;
	}

	public void setShUserName(String shUserName) {
		this.shUserName = shUserName;
	}

	public SHBrandUser(HttpServletRequest httpServletRequest, String userLanguage) {
		this.userLanguage = userLanguage;
		this.httpServletRequest = httpServletRequest;
		shUserId = 0;
		shUserName = "";
		emailAddress = "";
		startDate = 0;
		endDate = 0;
		active = "N";
		
    }
	
	public SHBrandUser(HttpServletRequest httpServletRequest, String userLanguage, Long shBrandUserId) {
		this.userLanguage = userLanguage;
		this.httpServletRequest = httpServletRequest;
		this.shUserId = shBrandUserId;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
    	try {
    		connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("select * from brandusers where Id=?");
			ps.setLong(1, shUserId);
			ResultSet objRs = ps.executeQuery();
			if (objRs.next()) {
				emailAddress = objRs.getString("email");
				shUserName =  objRs.getString("fname");
				startDate = objRs.getLong("startDate");
				endDate = objRs.getLong("endDate");
				active = objRs.getString("active");
	        } 
			objRs.close();
			ps.close();
	    				
			fetchAssignedBrands();
			
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
	}
	
	public static String validateUserForDashboard(HttpServletRequest httpServletRequest, HttpServletResponse response, String userLanguage, String emailAddress, String password, String formType) {
		// TODO Auto-generated method stub
		String status = "", loginStatus = "", hashedPassword = "";
    	ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
    	try {
    		connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("select * from brandusers where lower(email)=? and (startDate is null or startDate = 0 or startDate < ?) and (endDate is null or endDate = 0 or endDate > ?) and active='Y'");
			ps.setString(1, emailAddress);
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, System.currentTimeMillis());
			ResultSet objRs = ps.executeQuery();
			if (objRs.next()) {
				if(objRs.getString("encryptionSalt") != null ) {
					hashedPassword = PasswordEncryption.hashpw(password, objRs.getString("encryptionSalt"));
				}
				if(formType.equals("login")) {
					if (objRs.getString("password").equals(hashedPassword)) {
						status = "user-valid-password-match";
						SHBrandUser shBrandUser = new SHBrandUser(httpServletRequest, userLanguage, objRs.getLong("id")); 
						httpServletRequest.getSession().setAttribute("shBrandUser", shBrandUser);
						
						String rememberMe = httpServletRequest.getParameter("brandRememberMe");
						if(rememberMe != null && rememberMe.equals("remember-me")) {
							Cookie objCookie = new Cookie("sheroBrandUserId", String.valueOf(objRs.getLong("id")));
					    	objCookie.setDomain("www.servicehero.com");
				            objCookie.setPath("/");
				            objCookie.setComment("servicehero: Brand Login");
				            objCookie.setMaxAge(31 * 24 * 60 * 60);
				            response.addCookie(objCookie);
						}
					    /* check if user has more than one brand */
						if(shBrandUser.getAssignedBrands().indexOf(",") > -1){
							String[] tokens = shBrandUser.getAssignedBrands().split(",");
							if(tokens.length == 1){
								// only one brand , redirect to brand page

								String brand = tokens[0];
								String[] brandDtls = brand.split("~");
								if(brandDtls[0].equals("kw") || brandDtls[0].equals("ae"))  {
									httpServletRequest.getSession().setAttribute("userCountry", brandDtls[0]) ;
								}
								status+="~dashboard/brand-dashboard?brand="+brandDtls[0]+"~"+brandDtls[1]+"~"+brandDtls[2]+"~"+brandDtls[3];

							}

						}else{

							// no brands assigned
							status+="~no-brands-assigned";
						}



					} else {
						httpServletRequest.getSession().setAttribute("resetBrandPasswordEMail", emailAddress.toLowerCase());
						status = "user-valid-password-wrong";
			        }
				} else if(formType.equals("resetpass")) {
					status = "user-valid-resetpass-match";
					SHBrandUser shBrandUser = new SHBrandUser(httpServletRequest, userLanguage, objRs.getLong("id"));
					httpServletRequest.getSession().setAttribute("shBrandUser", shBrandUser);
					/* check if user has more than one brand */
					if(shBrandUser.getAssignedBrands().indexOf(",") > -1){
						String[] tokens = shBrandUser.getAssignedBrands().split(",");
						if(tokens.length == 1){
							// only one brand , redirect to brand page

							String brand = tokens[0];
							String[] brandDtls = brand.split("~");
							if(brandDtls[0].equals("kw") || brandDtls[0].equals("ae"))  {
								httpServletRequest.getSession().setAttribute("userCountry", brandDtls[0]) ;
							}
							status+="~dashboard/brand-dashboard?brand="+brandDtls[0]+"~"+brandDtls[1]+"~"+brandDtls[2]+"~"+brandDtls[3];

						}

					}else{

						// no brands assigned
						status+="~no-brands-assigned";
					}

				} else if(formType.equals("useractivate")) {
					status = "user-valid-useractivate-match";
					SHBrandUser shBrandUser = new SHBrandUser(httpServletRequest, userLanguage, objRs.getLong("id"));
					httpServletRequest.getSession().setAttribute("shBrandUser", shBrandUser);
				} else if(formType.equals("social")) {
					status = "user-valid-social-match";
					SHBrandUser shBrandUser = new SHBrandUser(httpServletRequest, userLanguage, objRs.getLong("id"));
					httpServletRequest.getSession().setAttribute("shBrandUser", shBrandUser);
				} else if (formType.equals("register")) {
					httpServletRequest.getSession().setAttribute("resetBrandPasswordEMail", emailAddress.toLowerCase());
					status = "user-valid-already-present";
				}
	        } else {
				status = "user-invalid-email-wrong";
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
	
	public static int activateUser(HttpServletRequest httpServletRequest, Long shBrandUserId) {
		// TODO Auto-generated method stub
		int result = 0;
    	ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseQueryBuilder dbQuery = new DatabaseQueryBuilder(DatabaseQueryBuilder.QUERY_UPDATE, "brandUsers");
			List<DatabaseQueryField> columnNameValues = new ArrayList<>();
			columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, "active", DatabaseQueryField.OPERATOR_EQUAL, "Y", DatabaseQueryField.CONNECTOR_BLANK));
			dbQuery.setColumnNameValues(columnNameValues);
			
			List<DatabaseQueryField> identifierNameValues = new ArrayList<>();
			identifierNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "active", DatabaseQueryField.OPERATOR_EQUAL, "N", DatabaseQueryField.CONNECTOR_COMMA));
			dbQuery.setIdentifierNameValues(identifierNameValues);

			identifierNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "id", DatabaseQueryField.OPERATOR_EQUAL, String.valueOf(shBrandUserId), DatabaseQueryField.CONNECTOR_BLANK));
			dbQuery.setIdentifierNameValues(identifierNameValues);

			LOG.debug(dbQuery.getQuery());
			PreparedStatement ps = connection.prepareStatement(dbQuery.getQuery());
			result = ps.executeUpdate();
			ps.close();
			
			/*if(result > 0) {
				UserActivityLog userLog = new UserActivityLog();
				userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , UserActivityLog.ACTIVITY_ACTIVATE, shUserId, 0, "");
			}*/			
			connection.commit();
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
		return result;
	} 
	
	
	public void fetchAssignedBrands() {
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
        Connection connection = null;
		try {
	        connection = dataSource.getConnection();
            String strQuery = "select countryCode, sectorCode, brandCode, dealerCode from brandUserAllocatedBrands where brandUserId=? and active='Y'";
            PreparedStatement ps = connection.prepareStatement(strQuery);
			ps.setLong(1, shUserId);
            assignedBrands = "";
            ResultSet objRs = ps.executeQuery();
            while (objRs.next()) {
            	assignedBrands += objRs.getString("countryCode") + "~" + objRs.getString("sectorCode") + "~" + objRs.getString("brandCode") + "~" + objRs.getString("dealerCode") + ",";
            }
			objRs.close();
            ps.close();
			connection.commit();
			//LOG.error("Admin User Brands: " + assignedBrands);
        } catch (Exception e) {
			LOG.error(e.getMessage(),e);
        } finally {
			Utils.closeQuietly(connection);
        }
	}
	
	public int updateBrandUserValues() {
		// TODO Auto-generated method stub
		int result = 0, count = 0;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		//LOG.error("Entered updateSHUserValues");
		try {
			String strParam = "", strTemp = "", strName = "";
			DatabaseQueryBuilder dbQuery = new DatabaseQueryBuilder(DatabaseQueryBuilder.QUERY_UPDATE, "brandusers");
			List<DatabaseQueryField> columnNameValues = new ArrayList<>();
			boolean isValidParameterValue = true;
			Contact contact =  null;
			try {
				contact = new Contact(new String[]{getEmailAddress(), "brands"});
			}catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
			int[] keywords;
			int plId = 9;

			strParam = "encryptionSalt";
			if(httpServletRequest.getSession().getAttribute("dashboardencryptionSalt") != null) {
				strTemp = (String)httpServletRequest.getSession().getAttribute("dashboardencryptionSalt");
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_STRING, strParam, DatabaseQueryField.OPERATOR_EQUAL, strTemp, DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute("dashboardencryptionSalt");
				count++;
				try{
					contact.setExtendedDetail("password_salt", strTemp);
				}catch (Exception e) {
					LOG.error(e.getMessage(),e);
				}
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


			dbQuery.setColumnNameValues(columnNameValues);

			List<DatabaseQueryField> identifierNameValues = new ArrayList<>();
			identifierNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "id", DatabaseQueryField.OPERATOR_EQUAL, String.valueOf(shUserId), DatabaseQueryField.CONNECTOR_BLANK));
			dbQuery.setIdentifierNameValues(identifierNameValues);

			if(count > 0 && isValidParameterValue && result > -1) {
				//LOG.error("brandusers: updateBrandUserValues - " + dbQuery.getQuery());
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
	                  // pass countrycode~sector~brand~dealer
	public int checkUserHasBrandAccess(String BrandDetail) {
		int result = 1;    // no access
		StringTokenizer st;
		//fetchAssignedBrands();

		if(assignedBrands.length() > 1){
			st = new StringTokenizer(assignedBrands, ",");
			while (st.hasMoreTokens()) {
				if (st.nextToken().indexOf(BrandDetail) > -1) {
					result = 0;
				}
			}
		}
		return result;
	}

	public static String updateTridionMailingPreferences(HttpServletRequest request, String emailAddress, int emailPageTcmId) {
		String shStatus = "";
		try {
			int plId = (request.getRequestURI().indexOf("ar") > -1) ? 10 : 9;
			String pattern, strTempString = "";
            pattern = "yyyy.MM.dd HH:mm:ss";
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(pattern);
            strTempString = format.format(new java.util.Date(System.currentTimeMillis())).toString();

			Contact contact = new Contact(new String[]{emailAddress, "brands"});
			if(emailPageTcmId == TRIDION_RESET_PASSWORD_PAGE) {
				//LOG.error("reset password: " + emailAddress + "  " + TRIDION_RESET_PASSWORD_PAGE);
                contact.setExtendedDetail("extra_field_5", "ForgotPassword-" + strTempString);
				contact.save("tcm:" + plId + "-" + TRIDION_RESET_PASSWORD_PAGE + "-64");
			} else {
				contact.save("tcm:" + plId + "-" + emailPageTcmId + "-64");
			}
			shStatus = "tridion-mail-sent";
		} catch(Exception e) {
			shStatus = "tridion-error";
			//LOG.error("@@@@@@@@@@@@@@@@@@@@@@@@@");
			LOG.error(e.getMessage(),e);
			//LOG.error("@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
		return shStatus;
	}
	
}
