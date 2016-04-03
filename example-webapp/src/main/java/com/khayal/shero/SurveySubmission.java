package com.khayal.shero;

import com.sdl.dxa.modules.generic.utilclasses.DatabaseQueryBuilder;
import com.sdl.dxa.modules.generic.utilclasses.DatabaseQueryField;

import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

import java.sql.*;
import java.util.*;

public class SurveySubmission {
	/*
	SurveyVoting_kw: id, sHeroUseId, userLanguage, countryCode, parentCategoryCode, categoryCode, brandCode, dealerCode, lastVisit
	Create table surveySubmission_ae (
	    	id number constraint pk_sskwId_surveySubmission_kw primary key,
			shUserId constraint fkshuId_surveySubmission_kw references serviceHeroUsers(id),
			userLanguage varchar2(50),
			parentSectorCode varchar2(50),
			sectorCode varchar2(50),
			brandCode varchar2(50),
			dealerCode varchar2(50),
			lastVisit varchar2(50),
			surveyTime number,
			commentApproved varchar2(1),
			validVote varchar2(1), 
			invalidVoteReason varchar2(1000)
	);
	insert into surveySubmission_kw values(10000, 10000, 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 'N/A', 0, 'N', 'N', 'N/A');
	insert into surveySubmission_kw values(10001, 10006, 'en', '10001', '10002', '10003', '10004', '10005', 0, 'Y', 'Y', 'N/A');
	
	SurveyVotingData_kw: surveyVotingId, staffBefore, staffAfter, locationBefore, locationAfter, valueBefore, valueAfter, qualityBefore, qualityAfter, speedBefore, 
						speedAfter, reliabilityBefore, reliabilityAfter, callCenterBefore, callCenterAfter, websiteBefore, websiteAfter, overAllSatisfactionBefore, 
						overAllSatisfactionAfter, recommendationBefore, recommendationAfter, idealofferingBefore, idealofferingAfter, beforeAverage, afterAverage, voteAverage
	Create table surveySubmissionData_ae (
	    	surveyVotingId constraint fk_sskwId_surveySubmission_kw references surveySubmission_kw(id),
			staffBefore number,
			staffAfter number,
			locationBefore number,
			locationAfter number,
			valueBefore number,
			valueAfter number,
			qualityBefore number,
			qualityAfter number,
			speedBefore number,
			speedAfter number,
			reliabilityBefore number,
			reliabilityAfter number,
			callCenterBefore number,
			callCenterAfter number,
			websiteBefore number,
			websiteAfter number,
			overAllSatisfactionBefore number,
			overAllSatisfactionAfter number,
			recommendationBefore number,
			recommendationAfter number,
			idealofferingBefore number,
			idealofferingAfter number,
			beforeAverage number,
			afterAverage number,
			gap number,
			improveComment varchar2(4000)
	);
	insert into surveySubmissionData_kw values(10000, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0,0, 'N/A');
	CREATE SEQUENCE surveySubmission_kw_Seq MINVALUE 10001 MAXVALUE 9999999999999999 INCREMENT BY 1 START WITH 10001 CACHE 2 NOORDER NOCYCLE;

	Create table surveyComments (
	  		id number constraint pk_sskwId_surveyComments primary key,
	  		shUserId number,
	    	surveyVotingId number,
	     	parentCommentId number,
	      	languageCode varchar2(3),
	    	countryCode varchar2(3),
	      	parentSectorCode varchar2(50),
	      	sectorCode varchar2(50),
	      	brandCode varchar2(50),
	      	originalComment varchar2(4000),
	      	approveComment varchar2(4000),
	      	commentApproved varchar2(1),
	      	adminRating varchar2(1),
	      	approvalReason varchar2(2),
	      	approvedBy number,
	      	approvedTime TIMESTAMP(6) with time zone,
	      	agree number,
	      	disagree number,
	      	fbShare number,
	      	twShare number
	);
	insert into surveyComments values(10000, 10000, 10000, 10000, 'en', 'kw', '644', '927', '930', 'Hi All this is a comment How do I look',
	      'N/A', 'N', null, null, null, null, 0, 0, 0, 0);
	CREATE SEQUENCE surveyComments_Seq MINVALUE 10001 MAXVALUE 9999999999999999 INCREMENT BY 1 START WITH 10001 CACHE 2 NOORDER NOCYCLE;

	*/
	private static Logger LOG = LoggerFactory.getLogger(SurveySubmission.class);

    public static long votingStartDate;
    public static long votingEndDate;

    public static String COMMENT_COUNT_READ = "commentRead";
    
    private String userLanguage;
    private HttpServletRequest httpServletRequest;
    
    //private ServiceHeroUser serviceHeroUser;
	//private ServiceHeroUserExtra serviceHeroUserExtra;
   
    public SurveySubmission(HttpServletRequest httpServletRequest, String userLanguage) {
        this.httpServletRequest = httpServletRequest;
        this.userLanguage = userLanguage;
        //serviceHeroUser = (ServiceHeroUser) httpServletRequest.getSession().getAttribute("shUser");
       // serviceHeroUserExtra = (ServiceHeroUserExtra) httpServletRequest.getSession().getAttribute("shUserExtra");
        setVotingDates();
    }
	
    public static void setVotingDates() {
        Calendar objCal1 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kuwait"));
        Calendar objCal2 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kuwait"));
        int month = objCal1.get(Calendar.MONTH);
        objCal1.set(Calendar.DAY_OF_MONTH, 1);
        objCal1.set(Calendar.HOUR_OF_DAY, 0);
        objCal1.set(Calendar.MINUTE, 0);
        objCal1.set(Calendar.SECOND, 0);
        objCal1.set(Calendar.MILLISECOND, 0);

        objCal2.setTimeInMillis(objCal1.getTimeInMillis());
        if(month >= 9) {
            objCal1.set(Calendar.MONTH, 9);
            objCal2.set(Calendar.YEAR, objCal2.get(Calendar.YEAR) + 1);
            objCal2.set(Calendar.MONTH, 0);
        } else if(month >= 6) {
            objCal1.set(Calendar.MONTH, 6);
            objCal2.set(Calendar.MONTH, 9);
        } else if(month >= 3) {
            objCal1.set(Calendar.MONTH, 3);
            objCal2.set(Calendar.MONTH, 6);
        } else if(month >= 0) {
            objCal1.set(Calendar.MONTH, 0);
            objCal2.set(Calendar.MONTH, 3);
        }
        votingEndDate = objCal2.getTimeInMillis();
        votingStartDate = objCal1.getTimeInMillis();
    }
    
    
    public String submitSurvey() {
		int result = 0;
		String shStatus = "";
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		long surveyId = 0, surveyStartTime = 0, surveyTime, parentCommentId = 0;
		float beforeQNum = 0, afterQNum = 0, beforeQNumSum = 0, afterQNumSum = 0, beforeQNumCount = 0, afterQNumCount = 0;
		String strTemp, surveyQuery, parentCategoryCode = "", categoryCode = "", brandCode = "", dealerCode = "", dealerCityCode = "", lastVisit = "", beforeValue, afterValue;
		try {
			String countryCode = (String) httpServletRequest.getSession().getAttribute("userCountry");	
            if(countryCode == null || countryCode.length() == 0)
            	countryCode = "N/A";
            SHSurveyUser shSurveyUser = (SHSurveyUser)httpServletRequest.getSession().getAttribute("shSurveyUser");	
            
            parentCategoryCode = (String) httpServletRequest.getParameter("surveyParentSector");
            if(parentCategoryCode == null || parentCategoryCode.length() == 0)
            	parentCategoryCode = "N/A";

            categoryCode = (String) httpServletRequest.getParameter("surveySector");
            if(categoryCode == null || categoryCode.length() == 0)
            	categoryCode = "N/A";
            /*if(categoryCode.equals("N/A")) {
            	categoryCode = "" +  httpServletRequest.getSession().getAttribute("surveySector");
                if(categoryCode == null || categoryCode.length() == 0)
                	categoryCode = "N/A";
            }*/
            httpServletRequest.getSession().setAttribute("surveySector", categoryCode);

            brandCode = (String) httpServletRequest.getParameter("surveyBrand");
            if(brandCode == null || brandCode.length() == 0)
            	brandCode = "N/A";
            /*if(brandCode.equals("N/A")) {
            	brandCode = "" + httpServletRequest.getSession().getAttribute("surveyBrand");
                if(brandCode == null || brandCode.length() == 0)
                	brandCode = "N/A";
            }*/
            httpServletRequest.getSession().setAttribute("surveyBrand", brandCode);

            dealerCode = (String) httpServletRequest.getParameter("surveyDealer");
            if(dealerCode == null || dealerCode.length() == 0)
            	dealerCode = "N/A";
            
            dealerCityCode = (String) httpServletRequest.getParameter("surveyDealerCity");
            if(dealerCityCode == null || dealerCityCode.length() == 0)
            	dealerCityCode = "N/A";

            if(!countryCode.equals("N/A") && !categoryCode.equals("N/A") && !brandCode.equals("N/A")) {
            	if(shSurveyUser.getVotedBrands().indexOf(countryCode + "~" + categoryCode + "~" + brandCode + "~" + dealerCode) == -1) {
	            	connection = dataSource.getConnection();
		    		
		    		PreparedStatement ps = connection.prepareStatement("Select surveySubmission_Seq.nextVal from dual");
	                ResultSet objRs = ps.executeQuery();
	                if(objRs.next())
	                	surveyId = objRs.getLong(1);
	                objRs.close();
	                ps.close();
	                if(surveyId < 10001)
	                	surveyId = 10001;
	                
	                
	                lastVisit = (String) httpServletRequest.getParameter("surveyLastVisit");
	                if(lastVisit == null || lastVisit.length() == 0)
	                	lastVisit = "N/A";
	                
	                strTemp = "" +  httpServletRequest.getSession().getAttribute("surveyStartTime");	
	                if(strTemp == null || strTemp.length() == 0)
	                	surveyStartTime = 0;
	                else 
	                	surveyStartTime = Long.parseLong(strTemp);
	                surveyTime = System.currentTimeMillis() - surveyStartTime;
	                httpServletRequest.getSession().removeAttribute("surveyStartTime");
	                
	                strTemp = (String) httpServletRequest.getSession().getAttribute("parentCommentId");	
	                if(strTemp == null || strTemp.length() == 0)
	                	parentCommentId = 0;
	                else 
	                	parentCommentId = Long.parseLong(strTemp);
	                
	    			LOG.error("Before Vote: " + System.currentTimeMillis() + ": " + UserActivityLog.generateErrorLogs(httpServletRequest));
	                surveyQuery = "insert into surveySubmission_" + countryCode + " values(?,?, ?,?, ?,?, ?,?, ?,?, ?, ?,?,?,?,?,?, ?)";
					ps = connection.prepareStatement(surveyQuery);
					ps.setLong(1, surveyId);
					ps.setLong(2, shSurveyUser.getId());
					ps.setString(3, userLanguage);
					ps.setString(4, parentCategoryCode);
					ps.setString(5, categoryCode);
					ps.setString(6, brandCode);
					ps.setString(7, dealerCode);
					ps.setString(8, lastVisit);
					ps.setLong(9, surveyTime);
					ps.setString(10, "N");
					ps.setString(11, "N/A");
					ps.setInt(12, 0);
					ps.setInt(13, 0);
					ps.setString(14, "0");
					ps.setString(15, "0");
					ps.setString(16, "0");
					ps.setString(17, "0");
					ps.setString(18, dealerCityCode);
					result = ps.executeUpdate();
					ps.close();
					
					int k = 1, i = 1;
					surveyQuery = "insert into surveySubmissiondata_" + countryCode + " values(?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?,?,?,?)"; 	
					ps = connection.prepareStatement(surveyQuery);
					ps.setLong(k++, surveyId);
					for (i = 1; i < 15; i++) {
						if(httpServletRequest.getParameter("qNum" + (1000 + i)) != null) {
							beforeValue = (String) httpServletRequest.getParameter("qNum" + i);
							if(beforeValue == null || beforeValue.length() == 0)
								beforeQNum = -1;
			                else { 
			                	beforeQNum = Integer.parseInt(beforeValue);
			                	if(beforeQNum > 0) {
			                		beforeQNumSum += beforeQNum;
			                		beforeQNumCount++;
			                	}
			                }
							ps.setFloat(k++, beforeQNum);
							        
							afterValue = (String) httpServletRequest.getParameter("qNum" + (1000 + i));
							if(afterValue == null || afterValue.length() == 0)
								afterQNum = -1;
			                else { 
			                	afterQNum = Integer.parseInt(afterValue);
			                	if(beforeQNum > -1 && afterQNum > 0) {
			                		afterQNumSum += afterQNum;
			                		afterQNumCount++;
			                	}
			                }
							ps.setFloat(k++, afterQNum);
						} else
							break;
					}
					if(beforeQNumSum > 0)
						ps.setFloat(k++, (beforeQNumSum / beforeQNumCount));
					else 
						ps.setFloat(k++, (-1));
	
					if(afterQNumSum > 0)
						ps.setFloat(k++, (afterQNumSum / afterQNumCount));
					else
						ps.setFloat(k++, (-1));
	
					if(afterQNumSum > 0 && beforeQNumSum > 0)
						ps.setFloat(k++, ((afterQNumSum / afterQNumCount) - (beforeQNumSum / beforeQNumCount)));
					else if(afterQNumSum > 0 && beforeQNumSum == 0)
						ps.setFloat(k++, (afterQNumSum / afterQNumCount));
					else 
						ps.setFloat(k++, (-99));
					ps.setInt(k++, 0);
					ps.setInt(k++, 0);
	
					result += ps.executeUpdate();
					ps.close();
					
					
					if(result > 0) {
						UserActivityLog userLog = new UserActivityLog();
						userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , UserActivityLog.ACTIVITY_VOTE, shSurveyUser.getId(), surveyId, "");

						
						strTemp = (String) httpServletRequest.getParameter("surveyComment");
		                if(strTemp == null || strTemp.trim().length() == 0)
		                	strTemp = "N/A";
						if(strTemp.length() > 5) {
							long commentId = 0;
							ps = connection.prepareStatement("Select surveyComments_Seq.nextVal from dual");
			                objRs = ps.executeQuery();
			                if(objRs.next())
			                	commentId = objRs.getLong(1);
			                objRs.close();
			                ps.close();
			                if(commentId < 10001)
			                	commentId = 10001;
			                
			                k = 1;
			                surveyQuery = "insert into surveyComments (ID, SHUSERID, SURVEYVOTINGID, PARENTCOMMENTID, LANGUAGECODE, COUNTRYCODE, SECTORCODE, BRANDCODE, DEALERCODE," +
			                              " ORIGINALCOMMENT, APPROVECOMMENT, COMMENTAPPROVED, APPROVEDTIME, APPROVEDLANGUAGECODE, STARRATING, ROOTPARENTID  ) " +
			                              "values(?,?, ?,?, ?,?, ?,?, ?,?, ?,?, ?,?,?,?)";
							ps = connection.prepareStatement(surveyQuery);
							ps.setLong(k++, commentId);
							ps.setLong(k++, shSurveyUser.getId());
							ps.setLong(k++, surveyId);
							ps.setLong(k++, parentCommentId);
							ps.setString(k++, userLanguage);
							ps.setString(k++, countryCode);
							ps.setString(k++, categoryCode);
							ps.setString(k++, brandCode);
							ps.setString(k++, dealerCode);  //dealer code
							ps.setString(k++, strTemp);
							ps.setString(k++, "N/A");
							ps.setString(k++, "X");
							ps.setTimestamp(k++, new Timestamp(System.currentTimeMillis()));
							ps.setString(k++, userLanguage);
							if(afterQNumSum > 0)
								ps.setFloat(k++, 0);
							else
								ps.setFloat(k++, -1);
							ps.setLong(k++, 0);
							ps.executeUpdate();
							k = 1;
							surveyQuery = "insert into surveyCommentsExtraInfo values(?,?, ?,?, ?,?, ?,?)"; 
							ps = connection.prepareStatement(surveyQuery);
							ps.setLong(k++, commentId);
							ps.setLong(k++, 0);
							ps.setLong(k++, 0);
							ps.setLong(k++, 0);
							ps.setLong(k++, 0);
							ps.setLong(k++, 0);
							ps.setLong(k++, 0);
							ps.setLong(k++, 0);
							ps.executeUpdate();


							userLog = new UserActivityLog();
							userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , UserActivityLog.ACTIVITY_COMMENT, shSurveyUser.getId(), commentId, "");

						}
						
						float vAvg = 0;
						try { 
							if(afterQNumSum > 0)
								vAvg = (((shSurveyUser.getVotingAverage() * shSurveyUser.getNumberOfVotes()) + (afterQNumSum / afterQNumCount)) / (shSurveyUser.getNumberOfVotes() + 1));
							else 
								vAvg = shSurveyUser.getVotingAverage();
								
						} catch(Exception ex) {
							vAvg = 0;
						}
						
						httpServletRequest.getSession().setAttribute("numberOfVotes", String.valueOf(shSurveyUser.getNumberOfVotes() + 1));
						httpServletRequest.getSession().setAttribute("votingAverage", String.valueOf(vAvg));
						shSurveyUser.setNumberOfVotes(shSurveyUser.getNumberOfVotes() + 1);
						shSurveyUser.setVotingAverage(vAvg);
											
					    shSurveyUser.setHttpServletRequest(httpServletRequest);
					    result += shSurveyUser.updateSHUserExtraValues();
					    
					    shSurveyUser.setVotedBrands(shSurveyUser.getVotedBrands() + countryCode + "~" + categoryCode + "~" + brandCode + "~" + dealerCode + "," );
	
					    try { 
					    	if(afterQNumSum > 0) {
					    		httpServletRequest.getSession().setAttribute("surveyVoteAverage", String.valueOf(afterQNumSum / afterQNumCount));
					    	} else {
					    		httpServletRequest.getSession().setAttribute("surveyVoteAverage", String.valueOf(0));
					    	}
						} catch(Exception ex) {
							httpServletRequest.getSession().setAttribute("surveyVoteAverage", String.valueOf(0));
						}
					    
					    Calendar objCal = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kuwait"));
				        objCal.set(Calendar.HOUR_OF_DAY, 0);
				        objCal.set(Calendar.MINUTE, 0);
				        objCal.set(Calendar.SECOND, 0);
				        objCal.set(Calendar.MILLISECOND, 0);
				        String strQuery = "select count(*) From userActivityLog where actionType='" + UserActivityLog.ACTIVITY_VOTE + "' and shUserId=? and actionTime>=?";
						ps = connection.prepareStatement(strQuery);
						ps.setLong(1, shSurveyUser.getId());
						ps.setTimestamp(2, new Timestamp(objCal.getTimeInMillis()));
						objRs = ps.executeQuery();
						if(objRs.next()) {
							LOG.debug("Todays votes: " + shSurveyUser.getId() + "  " + objRs.getInt(1));
							if(objRs.getInt(1) == 1)
								SHSurveyUser.updateTridionMailingPreferences(httpServletRequest, shSurveyUser.getEmailAddress(), SHSurveyUser.TRIDION_VOTE_MAILING_PAGE);   //Vote confirmation Email
						}
						objRs.close();
						ps.close();
						
						httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
					}
					if(result == 3) {
						boolean isProfileComplete = shSurveyUser.isProfileComplete();
						if(!isProfileComplete){
							shStatus = "vote-submit-profile-update";
						}   else{
							shStatus = "vote-submit";
						}

						/* if valid vote submission and if login via fb then setup  wallpost msg*/
						/* right now set only the company*/
						/*String strWallMsg = //MyHttpServlet.getSiteHeader("survey.facebook.wall.message", userLanguage);
						strWallMsg = strWallMsg.replace("(store)",strCompName);    */
						httpServletRequest.getSession().setAttribute("facebookWallMsg", "true");
						httpServletRequest.getSession().setAttribute("surveyvoteid", surveyId);
					}else
						shStatus = "vote-error";
	
					connection.commit();
					
					try {
						if(afterQNumSum > 0) {
							//CallableStatement cstmt = connection.prepareCall("{? = CALL GET_VOTE_STAR_RATING(p_sectorcode number, p_brandcode number, p_dealercode number, p_votingid number, p_afteraverage number)}");
							String returnString = "";
							String call = "{? = call GET_VOTE_STAR_RATING(?,?,?,?,?)}";
							CallableStatement cstmt = connection.prepareCall(call);
	
							cstmt.registerOutParameter(1, Types.VARCHAR);
	
							cstmt.setLong(2, Long.parseLong(categoryCode));
							cstmt.setLong(3, Long.parseLong(brandCode));
							cstmt.setLong(4, Long.parseLong(dealerCode));
							cstmt.setLong(5, surveyId);
							cstmt.setFloat(6, (afterQNumSum / afterQNumCount));
							cstmt.executeQuery();
							returnString = cstmt.getString(1);
							if(returnString.length() >0 && returnString.startsWith("ERROR") ) {
								LOG.error("Vote comment star rating error: categoryCode=" + categoryCode + ", brandCode=" + brandCode + ", dealerCode=" + dealerCode + ", surveyId=" + surveyId + ", avg=" + (afterQNumSum / afterQNumCount));
								LOG.error(returnString);
							}
						}
					} catch(SQLException se) {
						LOG.error("Vote comment star rating error: categoryCode=" + categoryCode + ", brandCode=" + brandCode + ", dealerCode=" + dealerCode + ", surveyId=" + surveyId + ", avg=" + (afterQNumSum / afterQNumCount));
						LOG.error(se.getMessage(),se);
	
					} catch(Exception ex) {
						LOG.error("Vote comment star rating error: categoryCode=" + categoryCode + ", brandCode=" + brandCode + ", dealerCode=" + dealerCode + ", surveyId=" + surveyId + ", avg=" + (afterQNumSum / afterQNumCount));
						LOG.error(ex.getMessage(), ex);
					}
            	} else {
					shStatus = "already-vote-error";
            	}
			}
		} catch(Exception e) {
			LOG.error("After Vote error: " + System.currentTimeMillis() + ": " + UserActivityLog.generateErrorLogs(httpServletRequest));
			LOG.error(e.getMessage(),e);
			shStatus = "vote-error";
		}finally {
			Utils.closeQuietly(connection);
		}
		return shStatus;
	}
    

    public int updateSurveyCommentsValues() {
		// TODO Auto-generated method stub
		int result = 0, count = 0;
		long commentId = 0;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
		PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
			String strTemp = "", strParam = "", userActivity = "";
			SHSurveyUser shSurveyUser = (SHSurveyUser)httpServletRequest.getSession().getAttribute("shSurveyUser");	
			
			connection = dataSource.getConnection();
			
			strTemp = (String) httpServletRequest.getParameter("extraId");	
            if(strTemp == null || strTemp.length() == 0)
            	commentId = 0;
            else 
            	commentId = Long.parseLong(strTemp);
			
			DatabaseQueryBuilder dbQuery = new DatabaseQueryBuilder(DatabaseQueryBuilder.QUERY_UPDATE, "surveyComments");
			List<DatabaseQueryField> columnNameValues = new ArrayList<>();
			
			strParam = "agree";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				if(shSurveyUser.getAgreeComments().indexOf("," + commentId + ",") == -1) {
					columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)httpServletRequest.getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
					userActivity = UserActivityLog.ACTIVITY_COMMENT_AGREE;
				} else {
					columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "agree", DatabaseQueryField.OPERATOR_EQUAL, "agree-1", DatabaseQueryField.CONNECTOR_COMMA));
					userActivity = UserActivityLog.ACTIVITY_COMMENT_AGREE_REMOVE;
				}
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
				if(shSurveyUser.getDisagreeComments().indexOf("," + commentId + ",") > -1) {
					columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "disagree", DatabaseQueryField.OPERATOR_EQUAL, "disagree-1", DatabaseQueryField.CONNECTOR_COMMA));
					shSurveyUser.setDisagreeComments(shSurveyUser.getDisagreeComments().replace(commentId + ",", ""));
					UserActivityLog userLog = new UserActivityLog();
					userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , UserActivityLog.ACTIVITY_COMMENT_DISAGREE_REMOVE, shSurveyUser.getId(), commentId, "");

				}
			}
				
			strParam = "disagree";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				if(shSurveyUser.getDisagreeComments().indexOf("," + commentId + ",") == -1) {
					columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)httpServletRequest.getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
					userActivity = UserActivityLog.ACTIVITY_COMMENT_DISAGREE;
				} else {
					columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "disagree", DatabaseQueryField.OPERATOR_EQUAL, "disagree-1", DatabaseQueryField.CONNECTOR_COMMA));
					userActivity = UserActivityLog.ACTIVITY_COMMENT_DISAGREE_REMOVE;
				}
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
				if(shSurveyUser.getAgreeComments().indexOf("," + commentId + ",") > -1) {
					columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "agree", DatabaseQueryField.OPERATOR_EQUAL, "agree-1", DatabaseQueryField.CONNECTOR_COMMA));
					shSurveyUser.setAgreeComments(shSurveyUser.getAgreeComments().replace(commentId + ",", ""));
					UserActivityLog userLog = new UserActivityLog();
					userLog.insertRecord(httpServletRequest, connection,  UserActivityLog.ACTIVITY_SURVEY_TABLE ,UserActivityLog.ACTIVITY_COMMENT_AGREE_REMOVE, shSurveyUser.getId(), commentId, "");

				}
			}

			strParam = "fbShare";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)httpServletRequest.getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
			}
			
			strParam = "twShare";
			if(httpServletRequest.getSession().getAttribute(strParam) != null) {
				columnNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, strParam, DatabaseQueryField.OPERATOR_EQUAL, (String)httpServletRequest.getSession().getAttribute(strParam), DatabaseQueryField.CONNECTOR_COMMA));
				httpServletRequest.getSession().removeAttribute(strParam);
				count++;
			}
			
			dbQuery.setColumnNameValues(columnNameValues);
			
			List<DatabaseQueryField> identifierNameValues = new ArrayList<>();
			identifierNameValues.add(new DatabaseQueryField(DatabaseQueryField.FIELD_TYPE_NUMBER, "id", DatabaseQueryField.OPERATOR_EQUAL, String.valueOf(commentId), DatabaseQueryField.CONNECTOR_BLANK));
			dbQuery.setIdentifierNameValues(identifierNameValues);
			
			if(count > 0) {
				LOG.debug("surveySubmisssion: updateSurveyCommentsValues - " + dbQuery.getQuery());
				
				PreparedStatement ps = connection.prepareStatement(dbQuery.getQuery());
				result = ps.executeUpdate();
				ps.close();
				
				if(userActivity.startsWith("agreeComment")) {
					strTemp = shSurveyUser.getAgreeComments();
					if(strTemp.indexOf(commentId + ",") == -1) {
						shSurveyUser.setAgreeComments(strTemp + commentId + ",");
					} else {
						shSurveyUser.setAgreeComments(strTemp.replace(commentId + ",", ""));
					}
				} else if(userActivity.startsWith("disagreeComment")) {
					strTemp = shSurveyUser.getDisagreeComments();
					if(strTemp.indexOf(commentId + ",") == -1) {
						shSurveyUser.setDisagreeComments(strTemp + commentId + ",");
					} else {
						shSurveyUser.setDisagreeComments(strTemp.replace(commentId + ",", ""));
					}
				}
				
				UserActivityLog userLog = new UserActivityLog();
				userLog.insertRecord(httpServletRequest, connection, UserActivityLog.ACTIVITY_SURVEY_TABLE , userActivity, shSurveyUser.getId(), commentId, "");


				httpServletRequest.getSession().setAttribute("shSurveyUser", shSurveyUser);
				connection.commit();
			}
		} catch(Exception e) {
			LOG.error(e.getMessage(),e);
		}finally {
			Utils.closeQuietly(connection);
		}
		return result;
	}
    
    
    /*
	Create table SurveyCommentsExtraInfo(
    	surveyCommentId constraint fk_scId_surveyComments references surveyComments(id),
    	commentRead number,
      	commentAgree number,
      	commentDisagree number,
      	commentFBShare number,
      	commentGPShare number,
      	commentTWShare number,
      	commentLIShare number
	);
    	    	
 	Create table UserActivityLogComments(
    	surveyCommentId constraint fk_scId1_surveyComments references surveyComments(id),
    	shUserId number,
		sessionId varchar2(100),
    	mediaChannel varchar2(100),
    	platform varchar2(100),
    	userAgent varchar2(512), 
    	CampaignId varchar2(100), 
    	IPAddress varchar2(100), 
    	countryName varchar2(100), 
    	GPSInfo varchar2(100), 
    );
    
	 */
	
	public static int setUserCommentCount(HttpServletRequest request, String commentIds, String countField) {
		int result = 0;
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		try {
        	connection = dataSource.getConnection();
			/*if(countField.equals(SurveySubmission.COMMENT_COUNT_READ)) {
				commentIds = (String) request.getSession().getAttribute("commentReadsIds");
				if(commentIds == null || commentIds.length() == 0)
					commentIds = "";
			}*/
			if(!commentIds.equals("")) {
				PreparedStatement ps = connection.prepareStatement("update SurveyCommentsExtraInfo set " + countField + "=(" + countField + "+1) where SurveyCommentId in ("+ commentIds + ")");
				result = ps.executeUpdate();
				ps.close();

				String strTemp = "", mediaChannel = "", platform = "", campaignId = "", userAgent = "", ipAddress = "", countryName = "", gpsInfo = "";
				
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
	            
	            SHSurveyUser shSurveyUser = null;
	            if(request.getSession().getAttribute("shSurveyUser") != null)
	            	shSurveyUser = (SHSurveyUser)request.getSession().getAttribute("shSurveyUser");
				
				ps = connection.prepareStatement("insert into UserActivityLogComments values(?,?, ?,?, ?,?, ?,?, ?,?)");
				//ps.setLong(1, Long.parseLong(strTemp)); this was strTemp by Saurabh. I am assigning this to commentid
				ps.setLong(1, Long.parseLong(commentIds));
	            if(shSurveyUser != null)
	            	ps.setLong(2, shSurveyUser.getId());
	            else 
	            	ps.setLong(2, 0);
				ps.setString(3, request.getSession().getId());
				ps.setString(4, mediaChannel);
	    		ps.setString(5, platform);
	    		ps.setString(6, userAgent);
	    		ps.setString(7, campaignId);
	    		ps.setString(8, ipAddress);
	    		ps.setString(9, countryName);
	    		ps.setString(10, gpsInfo);
				result = ps.executeUpdate();
				ps.close();
				connection.commit();
			}
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}finally {
			Utils.closeQuietly(connection);
		}
    	return result;	
	}
    
    
    
}
