package com.khayal.shero.webservices;

import com.khayal.shero.BrandLuceneIndexer;
import com.khayal.shero.SHBrandUser;
import com.khayal.shero.SHSurveyUser;
import com.khayal.shero.UserActivityLog;
import com.khayal.shero.services.SHSearchService;
import com.sdl.dxa.modules.generic.model.BrandSearch;
import com.sdl.dxa.modules.generic.model.KeyValuePair;
import com.sdl.dxa.modules.generic.model.UserComment;
import com.sdl.dxa.modules.generic.model.VoteHistory;
import com.sdl.dxa.modules.generic.utilclasses.TaxonomyComparator;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import org.json.JSONObject;
//import org.json.JSONArray;


//import com.servicehero.JWT;

public class SHAppRequests {	
	private static final Logger LOG = LoggerFactory.getLogger(SHAppRequests.class);
	private static TreeMap<String, String> objTMSysConstants = new TreeMap<String, String>();
	String shStatus="", strJWT="false";	
	/*public String callLogin(HttpServletRequest request, HttpServletResponse response, String userLanguage, String email, String password)
	{
		if (email == null || email.length() == 0)
	 		email = "None";
	 	if (password == null || password.length() == 0)
		    password = "None";
	 	//ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
	 	try{	 		
		 	if (!email.equals("None") && !password.equals("None")) {
		 		System.out.println("password2: "+password);
		 		try{
		 			shStatus = SHSurveyUser.validateUserForSurvey(request, response, userLanguage, email, password, "login");
		 		}catch(Exception e3){
		 			e3.printStackTrace();
		        	System.out.println(e3.getMessage());	    
		 		}
			    //System.out.println("shStatus: "+shStatus);
			    if(shStatus != null && !shStatus.equals("") && shStatus.length()>0 && shStatus.equals("user-valid-password-match") ){
			 		SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");			 		
			 		try{	
			 			System.out.println("before calling generateJWT: "+shStatus);
			 			strJWT = JWT.generateJWT(request, shSurveyUser.getId());
			 		}catch (Exception ex) {			 	
			 			System.err.println("An InvocationTargetException was caught!");			 	
			 	        Throwable cause = ex.getCause();			 	       
			 	        System.out.format("Invocation of %s failed because of: %s%n", "methodName", cause.getMessage());			 	
			 	   }
			 	}else{
			 		strJWT = "false";
			 	}
		    }
	 	}
	 	catch (Exception e)
        {
        	e.printStackTrace();
        	System.out.println(e.getMessage());	           
        }
		return strJWT;
	}*/
	
	public String[] callLogin(HttpServletRequest request, HttpServletResponse response, String userLanguage, String email, String password, String formType)
	{
		String[] returnArray = new String[5];
		if (email == null || email.length() == 0)
	 		email = "None";
	 	if (password == null || password.length() == 0)
		    password = "None";
	 	//ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
	 	try{	 		
		 	if (!email.equals("None") && !password.equals("None")) {		 		
		 		try{
		 			shStatus = SHSurveyUser.validateUserForSurvey(request, response, userLanguage, email, password, formType);
		 		}catch(Exception e3){
		 			e3.printStackTrace();
		        	System.out.println(e3.getMessage());	    
		 		}			    
		 		if(formType.toLowerCase().equals("login")) {
		 			if(shStatus != null && !shStatus.equals("") && shStatus.length()>0 && shStatus.equals("user-valid-password-match") ){
		 				SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");			 		
		 				try{		 					
		 					strJWT = JWT.generateJWT(request, shSurveyUser.getId());
		 				}catch (Exception ex) {			 	
		 					System.err.println("An InvocationTargetException was caught!");			 	
		 					Throwable cause = ex.getCause();			 	       
		 					System.out.format("Invocation of %s failed because of: %s%n", "methodName", cause.getMessage());			 	
		 				}
		 			}else{
		 				strJWT = "false";
		 			}
		 		}else if(formType.toLowerCase().equals("register")) {
		 			if(shStatus != null && !shStatus.equals("") && shStatus.length()>0 && shStatus.equals("user-new-email-insert") ){
		 				SHSurveyUser shSurveyUser = (SHSurveyUser) request.getSession().getAttribute("shSurveyUser");			 		
		 				try{		 					
		 					strJWT = JWT.generateJWT(request, shSurveyUser.getId());
		 				}catch (Exception ex) {			 	
		 					System.err.println("An InvocationTargetException was caught!");			 	
		 					Throwable cause = ex.getCause();			 	       
		 					System.out.format("Invocation of %s failed because of: %s%n", "methodName", cause.getMessage());			 	
		 				}
		 			}else{
		 				strJWT = "false";
		 			}
		 		}
		    }
	 	}
	 	catch (Exception e)
        {
        	e.printStackTrace();
        	System.out.println(e.getMessage());	           
        }
		
		returnArray[0] = shStatus;
		returnArray[1] = strJWT;		
		return returnArray;
	}
	
	//Returns isValid, isRenewed, strRenewedJWT
	public String[] validateJWT(HttpServletRequest request, String userLanguage, String strJWT) {				 	
	 	
		return JWT.validateJWT(request, userLanguage, strJWT);
	}
	
	public HashMap getLastVisit(String userLanguage, String strSurveyMoreThan, String strSurveyPast){			
		HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
		HashMap<String,String> objHMLastVisitId = new HashMap<String,String>();
		HashMap<String,String> objHMPastHeader = new HashMap<String,String>();
		HashMap<String,String> objHMWhen = new HashMap<String,String>();
		String siteLastVisitTaxonomyId="91", publicationId="9", strKeywordURI="";
		if(userLanguage != null && userLanguage.equals("ar")){
			publicationId = "10";
			strSurveyMoreThan = "أكثر من";
			strSurveyPast = "الماضي";
		}else{
			publicationId="9";
		}
						
        TaxonomyFactory objTaxonomy = new TaxonomyFactory();        
		try {
            List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + publicationId + "-" + siteLastVisitTaxonomyId + "-512").getKeywordChildren();
            Collections.sort(objKeyList);
            Iterator iterator = objKeyList.listIterator();
            Keyword objKey;
            int iCount =0;
            while(iterator.hasNext())
            {
            	objKey = (Keyword) iterator.next();
            	strKeywordURI = objKey.getKeywordURI();
            	if(!publicationId.equals("0")) {
     	            if(strKeywordURI.toLowerCase().contains("tcm:"+ publicationId + "-")){
     	            	strKeywordURI = com.sdl.dxa.modules.generic.utilclasses.CStringParser.replace(strKeywordURI, ("tcm:"+publicationId+"-"), "");
     	            	if(strKeywordURI.indexOf("-")>-1){
     	            		strKeywordURI = strKeywordURI.substring(0, strKeywordURI.indexOf("-"));
     	            	}
     	            }
     	        }
            	if(iCount == 4){
            		objHMPastHeader.put(strKeywordURI, strSurveyMoreThan);
            	}else{
            		objHMPastHeader.put(strKeywordURI, strSurveyPast);
            	}                
            	objHMLastVisitId.put(strKeywordURI, strKeywordURI);
                objHMWhen.put(strKeywordURI, objKey.getKeywordDescription());
                iCount++;                
            }
            objHM.put("lastVisitId", objHMLastVisitId);
            objHM.put("when", objHMWhen);
            objHM.put("pastHeader", objHMPastHeader);
        } catch(Exception e) {
        	LOG.error(e.getMessage(), e);           
        }
		return objHM;
	}
	
	public HashMap getRecentComments(HttpServletRequest httpServletRequest, String userCountry, String userLanguage, String actionType, String commentMaxSize, String sortParam){
		HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
		HashMap<String,String> objHMIdBrandId = new HashMap<String,String>();
		HashMap<String,String> objHMIdSectorId = new HashMap<String,String>();
		HashMap<String,String> objHMIdParentSectorId = new HashMap<String,String>();
		HashMap<String,String> objHMIdApproveComment = new HashMap<String,String>();
		HashMap<String,String> objHMIdDealerId = new HashMap<String,String>();
		HashMap<String,String> objHMIdCommentDate = new HashMap<String,String>();
		
		HashMap<String,HashMap> objHMBrands = new HashMap<String,HashMap>();
        HashMap<String,String> objHMIdBrandTitle = new HashMap<String,String>();
        HashMap<String,String> objHMIdBrandLogo1X = new HashMap<String,String>();
        HashMap<String,String> objHMIdBrandLogo2X = new HashMap<String,String>();  
        HashMap<String,String> objHMIdBrandTitleTemp = new HashMap<String,String>();
        HashMap<String,String> objHMIdBrandLogo1XTemp = new HashMap<String,String>();
        HashMap<String,String> objHMIdBrandLogo2XTemp = new HashMap<String,String>();       
        
		ApplicationContext context = RequestContextUtils.getWebApplicationContext(httpServletRequest);
    	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
		Connection connection = null;
		String brandIds="";
		try{
			if(userCountry == null || userCountry.length() < 1 || userCountry.equals("")){
				userCountry = "kw";
			}
			if(userLanguage == null || userLanguage.length() < 1 || userLanguage.equals("")){
				userLanguage = "en";
			}
			if(commentMaxSize == null || commentMaxSize.length() < 1 || commentMaxSize.equals("")){
				commentMaxSize = "70";
			}
			if(sortParam == null || sortParam.length() < 1 || sortParam.equals("")){
				sortParam = " order by actionTime desc ";
			}
			connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("select * from (select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode=? and lower(a.approvedLanguageCode)=? and a.shuserId=c.id and b.actionType=? and commentApproved='Y' and approveComment <> '-' and length(approveComment) > 30 and length(approveComment) < ? and a.shUserId=b.shUserId "+sortParam+") where rownum<=20 order by rownum");
			ps.setString(1, userCountry.toLowerCase());
			ps.setString(2, userLanguage.toLowerCase());
			ps.setString(3, "comment");
			ps.setString(4, commentMaxSize);
			ResultSet objRs = ps.executeQuery();
			while (objRs.next()){
				if(brandIds.indexOf(objRs.getString("brandCode") + ",") == -1) {
					brandIds += objRs.getString("brandCode") + ",";
                }
				objHMIdBrandId.put(objRs.getString("brandCode"), objRs.getString("brandCode"));
				objHMIdSectorId.put(objRs.getString("brandCode"), objRs.getString("sectorCode"));
				objHMIdParentSectorId.put(objRs.getString("brandCode"), objRs.getString("parentSectorCode"));
				objHMIdApproveComment.put(objRs.getString("brandCode"), objRs.getString("approveComment"));	
				objHMIdDealerId.put(objRs.getString("brandCode"), objRs.getString("dealerCode"));
				objHMIdCommentDate.put(objRs.getString("brandCode"), ""+new DateTime(objRs.getTimestamp("actionTime").getTime()));					
			}
			objRs.close();
			ps.close();
			
			if(brandIds != null && brandIds.length() > 0)
				brandIds = brandIds.substring(0, brandIds.length() - 1);
			if(brandIds != null && brandIds.length() > 0) {
				objHMBrands = SHSearchService.searchBrands(httpServletRequest, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandIds, "titlevalue", userCountry, userLanguage, false);
                
                objHMIdBrandTitleTemp = (HashMap) objHMBrands.get("title");
                objHMIdBrandLogo1XTemp = (HashMap) objHMBrands.get("logo1X");
                objHMIdBrandLogo2XTemp = (HashMap) objHMBrands.get("logo2X");          
                
                objHMIdBrandTitle.putAll(objHMIdBrandTitleTemp);
                objHMIdBrandLogo1X.putAll(objHMIdBrandLogo1XTemp);
                objHMIdBrandLogo2X.putAll(objHMIdBrandLogo2XTemp);
                
			}
			objHM.put("brandTitle", objHMIdBrandTitle);
			objHM.put("brandLogo1X", objHMIdBrandLogo1X);
			objHM.put("brandLogo2X", objHMIdBrandLogo2X);
			objHM.put("brandId", objHMIdBrandId);
			objHM.put("sectorId", objHMIdSectorId);
			objHM.put("parentSectorId", objHMIdParentSectorId);
			objHM.put("approveComment", objHMIdApproveComment);
			objHM.put("dealerId", objHMIdDealerId);
			objHM.put("commentDate", objHMIdCommentDate);
			
		}catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}finally {
			Utils.closeQuietly(connection);
		}
		return objHM;
	}	
	
	public static String getCountrySectors(String strCountry) {
		String strCountrySectors="";
		String siteSectorsTaxonomyId="218", publicationId="9";
        TaxonomyFactory objTaxonomy = new TaxonomyFactory();        
		try {
            List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + publicationId + "-" + siteSectorsTaxonomyId + "-512").getKeywordChildren();
            Collections.sort(objKeyList);
            Iterator iterator = objKeyList.listIterator();
            Keyword objKey;
            while(iterator.hasNext())
            {
                objKey = (Keyword) iterator.next(); 
                if(objKey.getKeywordKey() != null && (""+objKey.getKeywordKey()).length()>0 && ((""+objKey.getKeywordKey()).indexOf(strCountry) > 0)){
                	strCountrySectors = strCountrySectors+objKey.getKeywordDescription()+",";
                }                
            }
        } catch(Exception e) {
        	LOG.error(e.getMessage(), e);           
        }
		if(strCountrySectors.length() >0 && strCountrySectors.endsWith(",")){
			strCountrySectors = strCountrySectors.substring(0, strCountrySectors.length() - 1);
		}
		return strCountrySectors;
	}
	
	public static void getSystemConstants() {		
        String publicationsIds = "", siteSysConstantsTaxonomyId="92", publicationId="9";
        TaxonomyFactory objTaxonomy = new TaxonomyFactory();        
		try {
            List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + publicationId + "-" + siteSysConstantsTaxonomyId + "-512").getKeywordChildren();
            Collections.sort(objKeyList);
            Iterator iterator = objKeyList.listIterator();
            Keyword objKey;
            while(iterator.hasNext())
            {
                objKey = (Keyword) iterator.next();                
                objTMSysConstants.put(objKey.getKeywordName(), objKey.getKeywordDescription());
            }
        } catch(Exception e) {
        	LOG.error(e.getMessage(), e);           
        }
    }

    @SuppressWarnings({"unchecked"})
    public static String getSystemConstant(String systemConstant, int publicationId) {
        getSystemConstants();
        String strTemp;
		objTMSysConstants.get(systemConstant);
        if(objTMSysConstants.get(systemConstant) == null)
            return systemConstant;
        else {
            strTemp = objTMSysConstants.get(systemConstant);
	        if(publicationId != 0) {
	            if(strTemp.toLowerCase().contains("tcm"))
		            strTemp = com.sdl.dxa.modules.generic.utilclasses.CStringParser.replace(strTemp, ":9-", ":" + publicationId + "-");
	        }
            return strTemp;
        }
    }
	
	public List<KeyValuePair> getHTMLFormSelectValues(HttpServletRequest request, String stDropBoxCategoryId, String strLanguage, String countryCode){
		List<KeyValuePair> KeyValuePairList = new ArrayList<KeyValuePair>();
		int pubId = 8;
        if(strLanguage != null && strLanguage.length() > 0 && strLanguage.equals("ar"))
            pubId = 10;
        else
            pubId = 9;
        
        TaxonomyComparator objComp = new TaxonomyComparator();        
        String key;
        Keyword objKey;
        KeyValuePair keyValuePair;       
        TaxonomyFactory objTaxonomy = new TaxonomyFactory();
		if(!stDropBoxCategoryId.equals("0")) {
            List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-" + stDropBoxCategoryId + "-512").getKeywordChildren();
            //KeyValuePairList = new ArrayList<KeyValuePair>();
            Collections.sort(objKeyList, objComp);
            Iterator iterator = objKeyList.listIterator();
            while(iterator.hasNext()) {
                objKey = (Keyword) iterator.next();
                if(stDropBoxCategoryId.equals("160")) {                                   
                    if (countryCode == null || countryCode.length() == 0)
                        countryCode = "kw";
                    if (objKey.getKeywordName().indexOf(countryCode) > -1) {
                        keyValuePair = new KeyValuePair();
                        key = objKey.getKeywordKey();
                        List<Keyword> objChildKeyList = objKey.getKeywordChildren();
                        //KeyValuePairList = new ArrayList<KeyValuePair>();
                        Collections.sort(objChildKeyList, objComp);
                        Iterator childIterator = objChildKeyList.listIterator();
                        while (childIterator.hasNext()) {
                            objKey = (Keyword) childIterator.next();
                            if (objKey.getKeywordName().indexOf("00 - Not Specified") == -1) {
                                keyValuePair = new KeyValuePair();
                                key = objKey.getKeywordURI();
                                keyValuePair.setKey(key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")) + "_" + objKey.getKeywordKey());
                                keyValuePair.setValue(objKey.getKeywordDescription());
                                KeyValuePairList.add(keyValuePair);
                            }
                        }
                    }
                }else{
                    if(objKey.getKeywordName().indexOf("00 - Not Specified") == -1) {
                        keyValuePair = new KeyValuePair();
                        key = objKey.getKeywordURI();

                        if (stDropBoxCategoryId.equals("153"))
                            keyValuePair.setKey(objKey.getKeywordKey() + "_" + key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")));
                        else
                            keyValuePair.setKey(key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")) + "_" + objKey.getKeywordKey());
                        keyValuePair.setValue(objKey.getKeywordDescription());
                        KeyValuePairList.add(keyValuePair);
                    }
                }
            }                 
        }
		return KeyValuePairList;
	}
	
	public List<VoteHistory> getUserHistory(HttpServletRequest request){
		SHSurveyUser shSurveyUser = (SHSurveyUser)request.getSession().getAttribute("shSurveyUser");
		List<VoteHistory> voteHistory = new ArrayList<VoteHistory>();
		VoteHistory objVote;
        String brandIds_kw = "";
        String brandIds_ae = "";
        long lastVoteDate = 0;
        ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            String strQuery = "Select * from (";
            strQuery += "select parentSectorCode, sectorCode, brandCode, dealerCode, afterAverage, actionTime, 'kw' as countryCode from surveySubmission_kw a, userActivityLog b, surveySubmissionData_kw c where a.shUserId=b.shUserId and b.actionTypeId=c.surveyVotingId and a.id=c.surveyVotingId and b.actionType='" + UserActivityLog.ACTIVITY_VOTE + "' and a.shUserId=?";
            strQuery += " union ";
            strQuery += "select parentSectorCode, sectorCode, brandCode, dealerCode, afterAverage, actionTime, 'ae' as countryCode from surveySubmission_ae a, userActivityLog b, surveySubmissionData_ae c where a.shUserId=b.shUserId and b.actionTypeId=c.surveyVotingId and a.id=c.surveyVotingId and b.actionType='" + UserActivityLog.ACTIVITY_VOTE + "' and a.shUserId=?";
            strQuery += ") order by actionTime desc";
            PreparedStatement ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setLong(1, shSurveyUser.getId());
            ps.setLong(2, shSurveyUser.getId());
            ResultSet objRs = ps.executeQuery();
            while(objRs.next()) {
                if(objRs.getString("countryCode").equals("kw")) {
                    if(brandIds_kw.indexOf(objRs.getString("brandCode") + ",") == -1) {
                        brandIds_kw += objRs.getString("brandCode") + ",";
                    }
                } else if(objRs.getString("countryCode").equals("ae")) {
                    if(brandIds_ae.indexOf(objRs.getString("brandCode") + ",") == -1) {
                        brandIds_ae += objRs.getString("brandCode") + ",";
                    }
                }
            }
            if(brandIds_kw != null && brandIds_kw.length() > 0)
                brandIds_kw = brandIds_kw.substring(0, brandIds_kw.length() - 1);
            if(brandIds_ae != null && brandIds_ae.length() > 0)
                brandIds_ae = brandIds_ae.substring(0, brandIds_ae.length() - 1);

            BrandSearch objBrandList;
            String sectorCode, strTempValue = "", strSrchText;
            HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
            HashMap<String,String> objHMSort = new HashMap<String,String>();
            HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo1X = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo2X = new HashMap<String,String>();
            HashMap<String,String> objHMIdSector = new HashMap<String,String>();

            HashMap<String,String> objHMSortTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdTitleTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo1XTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo2XTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdSectorTemp = new HashMap<String,String>();

            if(brandIds_kw != null && brandIds_kw.length() > 0) {
                objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandIds_kw, "titlevalue", "kw");
                objHMSortTemp = (HashMap) objHM.get("sorter");
                objHMIdTitleTemp = (HashMap) objHM.get("title");
                objHMIdLogo1XTemp = (HashMap) objHM.get("logo1X");
                objHMIdLogo2XTemp = (HashMap) objHM.get("logo2X");
                objHMIdSectorTemp = (HashMap) objHM.get("sectorTitle");

                objHMSort.putAll(objHMSortTemp);
                objHMIdTitle.putAll(objHMIdTitleTemp);
                objHMIdLogo1X.putAll(objHMIdLogo1XTemp);
                objHMIdLogo2X.putAll(objHMIdLogo2XTemp);
                objHMIdSector.putAll(objHMIdSectorTemp);
            }

            if(brandIds_ae != null && brandIds_ae.length() > 0) {
                objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandIds_ae, "titlevalue", "ae");
                objHMSortTemp = (HashMap) objHM.get("sorter");
                objHMIdTitleTemp = (HashMap) objHM.get("title");
                objHMIdLogo1XTemp = (HashMap) objHM.get("logo1X");
                objHMIdLogo2XTemp = (HashMap) objHM.get("logo2X");
                objHMIdSectorTemp = (HashMap) objHM.get("sectorTitle");

                objHMSort.putAll(objHMSortTemp);
                objHMIdTitle.putAll(objHMIdTitleTemp);
                objHMIdLogo1X.putAll(objHMIdLogo1XTemp);
                objHMIdLogo2X.putAll(objHMIdLogo2XTemp);
                objHMIdSector.putAll(objHMIdSectorTemp);
            }

            objRs.beforeFirst();
            while(objRs.next()) {
                objVote = new VoteHistory();
                strTempValue = objRs.getString("brandCode");
                LOG.debug("strTempValue: " + strTempValue);
                objVote.setCountryCode(objRs.getString("countryCode"));
                objVote.setBrandCode(objRs.getLong("brandCode"));
                objVote.setBrandName(objHMIdTitle.get(strTempValue));
                objVote.setLogo1x(objHMIdLogo1X.get(strTempValue));
                objVote.setLogo2x(objHMIdLogo2X.get(strTempValue));
                objVote.setSectorName(objHMIdSector.get(strTempValue));
                objVote.setDealerCode(objRs.getString("dealerCode"));
                if(lastVoteDate == 0)
                    lastVoteDate = objRs.getTimestamp("actionTime").getTime();
                objVote.setVoteDate(new DateTime(objRs.getTimestamp("actionTime").getTime()));
                objVote.setVoteAverage(objRs.getDouble("afterAverage"));
                voteHistory.add(objVote);
            }
            objRs.close();
            ps.close();
           int iLastVoteDateMobileAppUser = (int)((System.currentTimeMillis() - lastVoteDate) / 86400000);
           request.getSession().setAttribute("LastVoteDateMobileAppUser", iLastVoteDateMobileAppUser);
           
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
        }finally {
            Utils.closeQuietly(connection);
        }
		return voteHistory;
	}
	
	public List<BrandSearch> getSurveyBrandList(HttpServletRequest request) {
		List<BrandSearch> brandSrchList = new ArrayList<BrandSearch>();
		/* this method is used by serachbrand as well
 	   If this contains srchText then this is coming from brand search
 	   This is also used for scoreboard brand list
 	 */     
     ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
     PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
     List<UserComment> commentList = new ArrayList<UserComment>();
     String strQuery = "", strTemp = "";
     Connection connection = null;
     try {
         BrandSearch objBrandList;
         String sectorCode, strTempValue = "", strSrchText, sort, sortParam = "", votedBrandIds = "";
         HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
         HashMap<String,String> objHMSort = new HashMap<String,String>();
         HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
         HashMap<String,String> objHMIdLogo1X = new HashMap<String,String>();
         HashMap<String,String> objHMIdLogo2X = new HashMap<String,String>();
         HashMap<String,String> objHMIdSector = new HashMap<String,String>();
         HashMap<String,String> objHMIdSectorName = new HashMap<String,String>();
         HashMap<String,String> objHMIdParentSector = new HashMap<String,String>();
         HashMap<String,String> objHMIdParentSectorName = new HashMap<String,String>();
         HashMap<String,String> objHMIdCountry = new HashMap<String,String>();
         HashMap<String,String> objHMIdPriority = new HashMap<String,String>();
         HashMap<String,String> objHMIdDealers = new HashMap<String,String>();

         HashMap<String,String> objHMIdComments = new HashMap<String,String>();
         HashMap<String,String> objHMIdNominations = new HashMap<String,String>();
         HashMap<String,String> objHMIdWins = new HashMap<String,String>();
         HashMap<String,String> objHMIdTrends = new HashMap<String,String>();
         HashMap<String,String> objHMIdStarRating = new HashMap<String,String>();
         
         
         HashMap<String,String> objHMSortTemp = new HashMap<String,String>();
         HashMap<String,String> objHMIdTitleTemp = new HashMap<String,String>();
         HashMap<String,String> objHMIdLogo1XTemp = new HashMap<String,String>();
         HashMap<String,String> objHMIdLogo2XTemp = new HashMap<String,String>();
         HashMap<String,String> objHMIdSectorTemp = new HashMap<String,String>();
         HashMap<String,String> objHMIdSectorNameTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdParentSectorTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdParentSectorNameTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdCountryTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdPriorityTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdDealersTemp  = new HashMap<String,String>();

         HashMap<String,String> objHMIdCommentsTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdNominationsTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdWinsTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdTrendsTemp  = new HashMap<String,String>();
         HashMap<String,String> objHMIdStarRatingTemp  = new HashMap<String,String>();


         String userCountry = (String) request.getSession().getAttribute("userCountry");

         int pageNo = 0, rowCount = 0, pageCount = 0;
         strTemp = request.getParameter("pg");
         if(strTemp == null || strTemp.length() == 0) {
             pageNo = 1;
         } else {
             try	{
                 pageNo = Integer.parseInt(strTemp);
             } catch(Exception ex) {
                 pageNo = 1;
             }
         }

         strSrchText = request.getParameter("srchText")  ;
         if(strSrchText == null || strSrchText.length() == 0)
             strSrchText = "0";
         sectorCode = request.getParameter("sectorCode");
         if(sectorCode == null || sectorCode.length() == 0)
             sectorCode = "0";
         if(sectorCode.equals("0")) {
             sectorCode = (String) request.getSession().getAttribute("surveySector");
             if(sectorCode == null || sectorCode.length() == 0)
                 sectorCode = "0";
         }

         sort = request.getParameter("sort");
         if(sort == null || sort.length() == 0) {
             if(((String) request.getSession().getAttribute("socialshareUrl1")).indexOf("scoreboard-brand-list") > -1)
                 sort = "rank-desc";
             else
                 sort = "alpha-asc";
         }

         PreparedStatement ps = null;
         ResultSet objRs = null;

         connection = dataSource.getConnection();

         if(!sectorCode.equals("0") && ((String) request.getSession().getAttribute("socialshareUrl1")).indexOf("scoreboard-brand-list") > -1) {
             TreeMap objTMBrandOtherInfo = new TreeMap();
             TreeMap objTMGoodComments = new TreeMap();
             TreeMap objTMBadComments = new TreeMap();
             TreeMap objTMGoodCommentsIds = new TreeMap();
             TreeMap objTMBadCommentsIds = new TreeMap();
             TreeMap objTMGoodCommentsStarRating = new TreeMap();
             TreeMap objTMBadCommentsStarRating = new TreeMap();
             TreeMap objTMGoodCommentsLanguage = new TreeMap();
             TreeMap objTMBadCommentsLanguage = new TreeMap();             

             //strQuery = "select a.brandCode, a.approveComment, a.starRating, a.approvedLanguageCode, max(b.actionTime) From surveyComments a, userActivityLog b where a.id=b.actionTypeId and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and a.shUserId=b.shUserId and LENGTH(TRIM((approvecomment))) > 1 and lower(a.countryCode) = '" + request.getSession().getAttribute("userCountry") + "' and a.sectorCode=? and adminRating > ? group by a.brandCode, a.approveComment, a.starRating, a.approvedLanguageCode";
             strQuery = "SELECT t.brandCode, t.approveComment, t.starRating, t.approvedLanguageCode, t.actionTime, t.adminRating, t.dealerCode FROM (SELECT ROW_NUMBER() OVER (PARTITION BY Brandcode ORDER BY adminRating DESC, actiontime DESC, commentid DESC) AS RowNumber, t.brandCode, t.dealerCode, t.adminRating, t.approveComment, t.starRating, t.approvedLanguageCode, t.actionTime FROM v_surveycomments t where commentApproved='Y' and lower(t.countryCode) = '" + request.getSession().getAttribute("userCountry") + "' and t.sectorCode = ? and adminRating > ? ) t WHERE RowNumber = 1";
             ps = connection.prepareStatement(strQuery);
             ps.setString(1,  sectorCode);
             ps.setInt(2, 0);
             objRs = ps.executeQuery();
             while(objRs.next()) {
                 objTMGoodComments.put(objRs.getString(1) + "~" + objRs.getString(7), objRs.getString(2));
                 objTMGoodCommentsStarRating.put(objRs.getString(1) + "~" + objRs.getString(7), objRs.getString(3));
                 objTMGoodCommentsLanguage.put(objRs.getString(1) + "~" + objRs.getString(7), objRs.getString(4));                 
             }
             objRs.close();
             ps.close();
             request.getSession().setAttribute("goodComments", objTMGoodComments);
             request.getSession().setAttribute("goodCommentsRating", objTMGoodCommentsStarRating);
             request.getSession().setAttribute("goodCommentsLanguage", objTMGoodCommentsLanguage);             

             //strQuery = "select a.brandCode, a.approveComment, a.starRating, a.approvedLanguageCode, max(b.actionTime) From surveyComments a, userActivityLog b where a.id=b.actionTypeId and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and a.shUserId=b.shUserId and LENGTH(TRIM((approvecomment))) > 1 and lower(a.countryCode) = '" + request.getSession().getAttribute("userCountry") + "' and a.sectorCode=? and adminRating < ? group by a.brandCode, a.approveComment, a.starRating, a.approvedLanguageCode";
             strQuery = "SELECT t.brandCode, t.approveComment, t.starRating, t.approvedLanguageCode, t.actionTime, t.adminRating, t.dealerCode FROM (SELECT ROW_NUMBER() OVER (PARTITION BY Brandcode ORDER BY adminRating ASC, actiontime DESC, commentid DESC) AS RowNumber, t.brandCode, t.dealerCode, t.adminRating, t.approveComment, t.starRating, t.approvedLanguageCode, t.actionTime FROM v_surveycomments t where commentApproved='Y' and lower(t.countryCode) = '" + request.getSession().getAttribute("userCountry") + "' and t.sectorCode = ? and adminRating < ? ) t WHERE RowNumber = 1";
             ps = connection.prepareStatement(strQuery);
             ps.setString(1,  sectorCode);
             ps.setInt(2, 0);
             objRs = ps.executeQuery();
             while(objRs.next()) {
                 objTMBadComments.put(objRs.getString(1) + "~" + objRs.getString(7), objRs.getString(2));
                 objTMBadCommentsStarRating.put(objRs.getString(1) + "~" + objRs.getString(7), objRs.getString(3));
                 objTMBadCommentsLanguage.put(objRs.getString(1) + "~" + objRs.getString(7), objRs.getString(4));                 
             }
             objRs.close();
             ps.close();
             request.getSession().setAttribute("badComments", objTMBadComments);
             request.getSession().setAttribute("badCommentsRating", objTMBadCommentsStarRating);
             request.getSession().setAttribute("badCommentsLanguage", objTMBadCommentsLanguage);             
         }

         request.getSession().setAttribute("paginationSort", sort);
     	/*request.getSession().setAttribute("paginationItemNumber", String.valueOf(rowCount));
     	request.getSession().setAttribute("paginationNumber", String.valueOf(pageNo));
     	request.getSession().setAttribute("paginationCount", String.valueOf(pageCount));*/         

         if(strSrchText != "0") {
             request.getSession().setAttribute("searchBrand", request.getParameter("srchTextFull"));
             brandSrchList = SHSearchService.searchBrand(request,  request.getParameter("srchText"), "0");
             //objLocalEntity.setBrandDtl(brandSrchList);
             //request.getSession().setAttribute("surveySector", sectorCode);
         } else {
             if (!sectorCode.equals("0")) {
             	StringTokenizer objSt1;
                 String dealerName;
                 //LOG.error(" brand selection "+request.getSession().getAttribute("shBrandUser"));
                 TreeMap<String,String> objTMAlphaSort = new TreeMap<String,String>();
                 TreeMap<String,String> objTMTop18Sort = new TreeMap<String,String>();

                 /*if(objEntity.getId().equals("29690")) {
                     SHBrandUser shBrandUser = (SHBrandUser)request.getSession().getAttribute("shBrandUser");
                     String assignedBrands = shBrandUser.getAssignedBrands();
                     LOG.error(" entered brand selection "+assignedBrands);
                     StringTokenizer objSt = new StringTokenizer(assignedBrands, ",");
                     String brandIds_kw = "", brandIds_ae = "";
                     String[] array;
                     while(objSt.hasMoreTokens()) {
                     	strTemp = objSt.nextToken();
                     	array = strTemp.split("~");
                     	if(array[0].equals("kw"))
                     		brandIds_kw += array[2] + ",";
                     	else 
                     		brandIds_ae += array[2] + ",";
                     }

                     
                     if(brandIds_kw != null && brandIds_kw.length() > 0)
                         brandIds_kw = brandIds_kw.substring(0, brandIds_kw.length() - 1);
                     if(brandIds_ae != null && brandIds_ae.length() > 0)
                         brandIds_ae = brandIds_ae.substring(0, brandIds_ae.length() - 1);

                     if(brandIds_kw != null && brandIds_kw.length() > 0) {
                         objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandIds_kw, "titlevalue", "kw");
                         objHMSortTemp = (HashMap) objHM.get("sorter");
                         objHMIdTitleTemp = (HashMap) objHM.get("title");
                         objHMIdLogo1XTemp = (HashMap) objHM.get("logo1X");
                         objHMIdLogo2XTemp = (HashMap) objHM.get("logo2X");
                         objHMIdSectorTemp = (HashMap) objHM.get("sectorId");
                         objHMIdSectorNameTemp = (HashMap) objHM.get("sectorTitle");
 	                    objHMIdParentSectorTemp = (HashMap) objHM.get("parentSectorId");
 	                    objHMIdParentSectorNameTemp = (HashMap) objHM.get("parentSectorTitle");
 	                    objHMIdCountryTemp = (HashMap) objHM.get("countryCode");
 	                    objHMIdPriorityTemp = (HashMap) objHM.get("priority");
 	                    objHMIdDealersTemp = (HashMap) objHM.get("dealers");

 	                    objHMIdCommentsTemp = (HashMap)objHM.get("commentsCount");
 	                    objHMIdNominationsTemp = (HashMap)objHM.get("nominationsCount");
 	                    objHMIdWinsTemp = (HashMap)objHM.get("winsCount");
 	                    objHMIdTrendsTemp = (HashMap)objHM.get("trends");
 	                    objHMIdStarRatingTemp = (HashMap)objHM.get("starRating");
                         
                         objHMSort.putAll(objHMSortTemp);
                         objHMIdTitle.putAll(objHMIdTitleTemp);
                         objHMIdLogo1X.putAll(objHMIdLogo1XTemp);
                         objHMIdLogo2X.putAll(objHMIdLogo2XTemp);
                         objHMIdSector.putAll(objHMIdSectorTemp);
                         objHMIdSectorName.putAll(objHMIdSectorNameTemp);
                         objHMIdParentSector.putAll(objHMIdParentSectorTemp);
                         objHMIdParentSectorName.putAll(objHMIdParentSectorNameTemp);
                         objHMIdCountry.putAll(objHMIdCountryTemp);
                         objHMIdPriority.putAll(objHMIdPriorityTemp);
                         objHMIdDealers.putAll(objHMIdDealersTemp);
                         
                         objHMIdComments.putAll(objHMIdCommentsTemp);
                         objHMIdNominations.putAll(objHMIdNominationsTemp);
                         objHMIdWins.putAll(objHMIdWinsTemp);
                         objHMIdTrends.putAll(objHMIdTrendsTemp);
                         objHMIdStarRating.putAll(objHMIdStarRatingTemp);

                     }

                     if(brandIds_ae != null && brandIds_ae.length() > 0) {
                         objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandIds_ae, "titlevalue", "ae");
                         objHMSortTemp = (HashMap) objHM.get("sorter");
                         objHMIdTitleTemp = (HashMap) objHM.get("title");
                         objHMIdLogo1XTemp = (HashMap) objHM.get("logo1X");
                         objHMIdLogo2XTemp = (HashMap) objHM.get("logo2X");
                         objHMIdSectorTemp = (HashMap) objHM.get("sectorId");
                         objHMIdSectorNameTemp = (HashMap) objHM.get("sectorTitle");
 	                    objHMIdParentSectorTemp = (HashMap) objHM.get("parentSectorId");
 	                    objHMIdParentSectorNameTemp = (HashMap) objHM.get("parentSectorTitle");
 	                    objHMIdCountryTemp = (HashMap) objHM.get("countryCode");
 	                    objHMIdPriorityTemp = (HashMap) objHM.get("priority");
 	                    objHMIdDealersTemp = (HashMap) objHM.get("dealers");

 	                    objHMIdCommentsTemp = (HashMap)objHM.get("commentsCount");
 	                    objHMIdNominationsTemp = (HashMap)objHM.get("nominationsCount");
 	                    objHMIdWinsTemp = (HashMap)objHM.get("winsCount");
 	                    objHMIdTrendsTemp = (HashMap)objHM.get("trends");
 	                    objHMIdStarRatingTemp = (HashMap)objHM.get("starRating");

                         objHMSort.putAll(objHMSortTemp);
                         objHMIdTitle.putAll(objHMIdTitleTemp);
                         objHMIdLogo1X.putAll(objHMIdLogo1XTemp);
                         objHMIdLogo2X.putAll(objHMIdLogo2XTemp);
                         objHMIdSector.putAll(objHMIdSectorTemp);
                         objHMIdSectorName.putAll(objHMIdSectorNameTemp);
                         objHMIdParentSector.putAll(objHMIdParentSectorTemp);
                         objHMIdParentSectorName.putAll(objHMIdParentSectorNameTemp);
                         objHMIdCountry.putAll(objHMIdCountryTemp);
                         objHMIdPriority.putAll(objHMIdPriorityTemp);
                         objHMIdDealers.putAll(objHMIdDealersTemp);
                         
                         objHMIdComments.putAll(objHMIdCommentsTemp);
                         objHMIdNominations.putAll(objHMIdNominationsTemp);
                         objHMIdWins.putAll(objHMIdWinsTemp);
                         objHMIdTrends.putAll(objHMIdTrendsTemp);
                         objHMIdStarRating.putAll(objHMIdStarRatingTemp);
                     }                        
                 	
                 } else {*/
	                    if(((String) request.getSession().getAttribute("socialshareUrl1")).indexOf("scoreboard-brand-list") > -1) {
	                        if(sort.indexOf("vote") > -1)
	                            objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR, sectorCode, "votes");
	                        else if(sort.indexOf("rank") > -1)
	                            objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR, sectorCode, "avgbrand");
	                        else
	                            objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR, sectorCode);
	                    } else {
	                        //sort = "vote-desc";
	                        //objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR, sectorCode, "votes");
	                        sort = "alpha-asc";
	                        objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR, sectorCode);
	                    }
	                    
	                    objHMSort = (HashMap) objHM.get("sorter");
	                    objHMIdTitle = (HashMap) objHM.get("title");
	                    objHMIdLogo1X = (HashMap) objHM.get("logo1X");
	                    objHMIdLogo2X = (HashMap) objHM.get("logo2X");
	                    objHMIdSector = (HashMap) objHM.get("sectorId");
	                    objHMIdSectorName = (HashMap) objHM.get("sectorTitle");
	                    objHMIdParentSector = (HashMap) objHM.get("parentSectorId");
	                    objHMIdParentSectorName = (HashMap) objHM.get("parentSectorTitle");
	                    objHMIdCountry = (HashMap) objHM.get("countryCode");
	                    objHMIdPriority = (HashMap) objHM.get("priority");
	                    objHMIdDealers = (HashMap) objHM.get("dealers");

	                    objHMIdComments = (HashMap)objHM.get("commentsCount");
	                    objHMIdNominations = (HashMap)objHM.get("nominationsCount");
	                    objHMIdWins = (HashMap)objHM.get("winsCount");
	                    objHMIdTrends = (HashMap)objHM.get("trends");
	                    objHMIdStarRating = (HashMap)objHM.get("starRating");

                 //}

                 List<String> sorterList = new ArrayList<String>(objHMSort.keySet());
                 if(sort.indexOf("desc") > -1) {
                     Comparator cmp = Collections.reverseOrder();
                     Collections.sort(sorterList, cmp);
                 } else {
                     Collections.sort(sorterList);
                 }
                 int allCount = 0, dealerCount = 0;
                 for (String sortValue : sorterList) {
                     strTempValue = objHMSort.get(sortValue);
                     if(objHMIdCountry.get(strTempValue).equals(userCountry) ) {
                         objSt1 = new StringTokenizer(objHMIdDealers.get(strTempValue), "^");
                         dealerCount = objSt1.countTokens();
                         while(objSt1.hasMoreTokens()) {
                             dealerName = objSt1.nextToken();
                             //if(request.getHeader("referer").indexOf("survey-pages") == -1 || (allCount < 18 && request.getHeader("referer").indexOf("survey-pages") > -1)) {
                             objTMTop18Sort.put(objHMIdTitle.get(strTempValue), strTempValue);
                             objBrandList = new BrandSearch();
                             objBrandList.setBrandCode(strTempValue);                             
                             objBrandList.setBrandName(objHMIdTitle.get(strTempValue));
                             objBrandList.setBrandLogo1x(objHMIdLogo1X.get(strTempValue));
                             objBrandList.setBrandLogo2x(objHMIdLogo2X.get(strTempValue));
                             objBrandList.setSectorCode(objHMIdSector.get(strTempValue));
                             objBrandList.setSectorName(objHMIdSectorName.get(strTempValue));
                             objBrandList.setParentSectorCode(objHMIdParentSector.get(strTempValue));
                             objBrandList.setParentSectorName(objHMIdParentSectorName.get(strTempValue));
                             objBrandList.setCountryCode(objHMIdCountry.get(strTempValue));
                             objBrandList.setPreferedBrand(objHMIdPriority.get(strTempValue));
                             if(dealerCount > 1)
                                 objBrandList.setDealerName(dealerName + "~");
                             else
                                 objBrandList.setDealerName(dealerName);

                             objBrandList.setComments(Integer.parseInt((objHMIdComments.get(strTempValue) != null)? objHMIdComments.get(strTempValue) : "0"));
                             objBrandList.setNominations(Integer.parseInt((objHMIdNominations.get(strTempValue) != null)? objHMIdNominations.get(strTempValue) : "0"));
                             objBrandList.setWins(Integer.parseInt((objHMIdWins.get(strTempValue) != null)? objHMIdWins.get(strTempValue) : "0"));
                             objBrandList.setTrends(Integer.parseInt((objHMIdTrends.get(strTempValue) != null)? objHMIdTrends.get(strTempValue) : "0"));
                             objBrandList.setStarRating((objHMIdStarRating.get(strTempValue) != null)? objHMIdStarRating.get(strTempValue) : "0");

                             brandSrchList.add(objBrandList);
                             allCount++;
                             //} else {
                             //	objTMAlphaSort.put(objHMIdTitle.get(strTempValue), userCountry + "~" + objHMIdParentSector.get(strTempValue) + "~" + objHMIdSector.get(strTempValue) + "~" + strTempValue + "~" + dealerName + "~" + objHMIdTitle.get(strTempValue) + "~" + objHMIdLogo1X.get(strTempValue) + "~" + objHMIdLogo2X.get(strTempValue));
                             //}
                         }
                     }
                 }

                 //objLocalEntity.setBrandDtl(brandSrchList);
                 //if(((String) request.getSession().getAttribute("socialshareUrl1")).indexOf("scoreboard-brand-list") == -1)
                 //	request.getSession().setAttribute("Top18Sort", objTMTop18Sort);
                 //request.getSession().setAttribute("alphaSortList", objTMAlphaSort);
                 request.getSession().setAttribute("surveySector", sectorCode);
             } else {
                 //Wrong sector info - No Brands
                 LOG.error(" 0 Wrong sector info - No Brands");
             }
         }
         if(ps != null)
             ps.close();
     } catch(Exception e) {
         LOG.error(e.getMessage(), e);
     } finally {
         Utils.closeQuietly(connection);
     }
		return brandSrchList;
	}
	
	public List<BrandSearch> getSimilarBrandList(HttpServletRequest request, String strSrchText) {
		List<BrandSearch> brandSrchList = new ArrayList<BrandSearch>();
        try {
            BrandSearch objBrandList;
            String sectorCode, strTempValue = "";
            HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
            HashMap<String,String> objHMSort = new HashMap<String,String>();
            HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo1X = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo2X = new HashMap<String,String>();
            HashMap<String,String> objHMIdSector = new HashMap<String,String>();
            HashMap<String,String> objHMIdParentSector = new HashMap<String,String>();
            HashMap<String,String> objHMIdDealers = new HashMap<String,String>();
            HashMap<String,String> objHMIdCountry = new HashMap<String,String>();
            HashMap<String,String> objHMIdPriority = new HashMap<String,String>();
            HashMap<String,String> objHMIdStarRating = new HashMap<String,String>();             

            if(strSrchText == null || strSrchText.length() == 0)
                strSrchText = "0";
            if(strSrchText.indexOf("/") > 01)
                strSrchText = strSrchText.replace("/", "");
            String userLanguage = (String) request.getSession().getAttribute("userCountry");
            String infoPageBrand = (String) request.getSession().getAttribute("surveyBrand");
            objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SHORT_NAME, strSrchText, "votes~15");
            
            objHMSort = (HashMap) objHM.get("sorter");
            objHMIdTitle = (HashMap) objHM.get("title");
            objHMIdLogo1X = (HashMap) objHM.get("logo1X");
            objHMIdLogo2X = (HashMap) objHM.get("logo2X");
            objHMIdSector = (HashMap) objHM.get("sectorId");
            objHMIdParentSector = (HashMap) objHM.get("parentSectorId");
            objHMIdDealers = (HashMap) objHM.get("dealers");
            //System.out.println("objHMIdDealers: "+objHMIdDealers);
            objHMIdCountry = (HashMap) objHM.get("countryCode");
            objHMIdPriority = (HashMap) objHM.get("priority");
            objHMIdStarRating = (HashMap)objHM.get("starRating");

            int count = 0;
            List<String> sorterList = new ArrayList<String>(objHMSort.keySet());           
            
            String votedBrands = "";
            if(request.getSession().getAttribute("shSurveyUser") != null)
                votedBrands = ((SHSurveyUser) request.getSession().getAttribute("shSurveyUser")).getVotedBrands();

            Collections.shuffle(sorterList);

            for (String sorterValue : sorterList) {
                strTempValue = objHMSort.get(sorterValue);                
                if(objHMIdCountry.get(strTempValue).equals(userLanguage)) {                	
                    //if(!infoPageBrand.equals(strTempValue) && votedBrands.indexOf("~" +strTempValue) == -1) {
                    if(!infoPageBrand.equals(strTempValue)) {                    	
                        count++;
                        objBrandList = new BrandSearch();
                        objBrandList.setBrandCode(strTempValue);
                        objBrandList.setBrandName(objHMIdTitle.get(strTempValue));
                        objBrandList.setBrandLogo1x(objHMIdLogo1X.get(strTempValue));
                        objBrandList.setBrandLogo2x(objHMIdLogo2X.get(strTempValue));
                        objBrandList.setSectorCode(objHMIdSector.get(strTempValue));
                        objBrandList.setParentSectorCode(objHMIdParentSector.get(strTempValue));                       
                        objBrandList.setDealerName((objHMIdDealers.get(strTempValue) != null)? objHMIdDealers.get(strTempValue) : "N/A");
                        objBrandList.setCountryCode(objHMIdCountry.get(strTempValue));
                        objBrandList.setPreferedBrand(objHMIdPriority.get(strTempValue));
                        objBrandList.setStarRating(objHMIdStarRating.get(strTempValue));
                        brandSrchList.add(objBrandList);
                        if(count > 5)
                            break;
                    }
                }
            }
            
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
        }        
        return brandSrchList;
	}
	
	public List<UserComment> getCommentList(HttpServletRequest request, String regionName) {       
        long lBrandUserId = 0;
        int childCommentCount = 0;
        ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
        List<UserComment> commentList = new ArrayList<UserComment>();
        String strQuery = "", strTemp = "";
        UserComment userComment = null;
        Connection connection = null;

        try {
            String sort, sortParam = "", filterbyNone, filterbyRating, ratingFilterParam="", filterbyResponse, responseFilterParam = "", filterbyStatus, statusFilterParam = "", filterbySentiment, sentimentFilterParam="";
            int pageNo;
            strTemp = request.getParameter("pg");
            if(strTemp == null || strTemp.length() == 0) {
                pageNo = 1;
                request.getSession().removeAttribute("paginationSort");
            } else {
                try	{
                    pageNo = Integer.parseInt(strTemp);
                } catch(Exception ex) {
                    pageNo = 1;
                }
            }

            sort = request.getParameter("sort");
            if(sort == null || sort.length() == 0) {
                if(request.getSession().getAttribute("paginationSort") != null) {
                    sort = (String) request.getSession().getAttribute("paginationSort");
                } else {
                    sort = "date-desc";
                }
            }

            if(sort.equals("date-asc"))
                sortParam = " order by actionTime asc";
            if(sort.equals("date-desc"))
                sortParam = " order by actionTime desc";
            else if(sort.equals("agree-asc"))
                sortParam = "order by agree asc";
            else if(sort.equals("agree-desc"))
                sortParam = " order by agree desc";
            else if(sort.equals("disagree-asc"))
                sortParam = " order by disagree asc";
            else if(sort.equals("disagree-desc"))
                sortParam = " order by disagree desc";


            int pubId = 8;
            if(((String) request.getSession().getAttribute("socialshareUrl1")).indexOf("/ar/") > -1)
                pubId = 10;
            else
                pubId = 9;


            if(regionName.equals("BrandDashboard") || regionName.equals("UserDetail")) {
                
                /* fetch the categories for
                     6401-512  Comment Status
                     6402-512  Brand Response Status
                 */
                String anyFilterActive = "false";
                int activeFilters = 0;
                filterbyNone = request.getParameter("filterbyNone");
                if (filterbyNone == null || filterbyNone.length() == 0 || filterbyNone != "true") {
                   filterbyNone = "";   // that means keep using session params
                }

                //request.getSession().setAttribute("paginationFilterByNone", filterbyNone);
                LOG.error(" filterbyNone " + filterbyNone);
                LOG.error(" filterbyRating "+request.getParameter("filterbyRating"));
                LOG.error(" filterbyResponse "+request.getParameter("filterbyResponse"));
                LOG.error(" filterbySentiment "+request.getParameter("filterbySentiment"));
                LOG.error(" filterbyStatus "+request.getParameter("filterbyStatus"));

                //if (filterbyNone == null || filterbyNone.length() == 0) {
                if(request.getParameterNames().toString().length() > 0){
                    filterbyRating = request.getParameter("filterbyRating");
                    if (filterbyRating == null || filterbyRating.length() == 0 || filterbyRating.equals("none")) {
                        if(filterbyRating != null && filterbyRating.equals("none")) {
                            LOG.error("filterbyRating then is  "+filterbyRating);
                            filterbyRating = "";
                            //request.getSession().setAttribute("paginationFilterByRating", null );
                        }
                       /* else if (request.getSession().getAttribute("paginationFilterByRating") != null) {
                            filterbyRating = (String) request.getSession().getAttribute("paginationFilterByRating");
                        }    */
                    }
                   // LOG.error("filterbyRating then is  "+request.getSession().getAttribute("paginationFilterByRating"));
                    if (filterbyRating != null && filterbyRating.toString().length() > 0)  {
                        if (filterbyRating.equals(".5")) {
                            //activeFilters++;
                           ratingFilterParam = " and starrating = 0.5";
                            //request.getSession().setAttribute("paginationFilterByRating", filterbyRating);
                        }else if (filterbyRating.equals("1")) {
                            //activeFilters++;
                            ratingFilterParam = " and starrating = 1";
                           // request.getSession().setAttribute("paginationFilterByRating", filterbyRating);
                        }else if (filterbyRating.equals("1.5")) {
                           // activeFilters++;
                           // request.getSession().setAttribute("paginationFilterByRating", filterbyRating);
                            ratingFilterParam = " and starrating = 1.5";
                        } else if (filterbyRating.equals("2")) {
                            //activeFilters++;
                            ratingFilterParam = "  and starrating = 2";
                            //request.getSession().setAttribute("paginationFilterByRating", filterbyRating);
                        }else if (filterbyRating.equals("2.5")) {
                            //activeFilters++;
                            ratingFilterParam = "  and starrating = 2.5";
                            //request.getSession().setAttribute("paginationFilterByRating", filterbyRating);
                        }else if (filterbyRating.equals("3")) {
                            //activeFilters++;
                            ratingFilterParam = "  and starrating = 3";
                            //request.getSession().setAttribute("paginationFilterByRating", filterbyRating);
                        }

                    }

                    //   filterbyResponse
                    filterbyResponse = request.getParameter("filterbyResponse");
                    if (filterbyResponse == null || filterbyResponse.length() == 0 || filterbyResponse.equals("none")) {
                        if(filterbyResponse != null && filterbyResponse.equals("none")) {
                            filterbyResponse = "";
                            //request.getSession().removeAttribute("paginationFilterByResponse" );
                        }
                        /*else if (request.getSession().getAttribute("paginationFilterByResponse") != null) {
                            filterbyResponse = (String) request.getSession().getAttribute("paginationFilterByResponse");
                        }   */
                    }

                    // COMMENT_BRAND_STATUS_HELPFULL   - 26517
                    // COMMENT_BRAND_STATUS_NOTHELPFULL - 26518

                    if (filterbyResponse != null && filterbyResponse.length() > 0) {
                        responseFilterParam = " and brandresponsestatus like '" + filterbyResponse + "'";
                        //activeFilters++;
                       // request.getSession().setAttribute("paginationFilterByResponse", filterbyResponse);
                    }

                    filterbySentiment = request.getParameter("filterbySentiment");
                    if (filterbySentiment == null || filterbySentiment.length() == 0 || filterbySentiment.equals("none")) {
                        if(filterbySentiment != null && filterbySentiment.equals("none")) {
                            filterbySentiment = "";
                           // request.getSession().setAttribute("paginationFilterBySentiment", null );
                        /*}else
                        if (request.getSession().getAttribute("paginationFilterBySentiment") != null) {

                            filterbySentiment = (String) request.getSession().getAttribute("paginationFilterBySentiment");       */
                        }
                    }
                    // later change this to minus-one, minus-two ....
                    if (filterbySentiment != null && filterbySentiment.toString().length() > 0) {
                        if (filterbySentiment.equals("2"))
                            sentimentFilterParam = " and adminrating = 2 ";
                        else if (filterbySentiment.equals("1"))
                            sentimentFilterParam = " and adminrating = 1 ";
                        else if (filterbySentiment.equals("0"))
                            sentimentFilterParam = " and adminrating = 0 ";
                        else if (filterbySentiment.equals("-1"))
                            sentimentFilterParam = " and adminrating = -1 ";
                        else if (filterbySentiment.equals("-2"))
                            sentimentFilterParam = " and adminrating = -2 ";
                        //activeFilters++;
                        //request.getSession().setAttribute("paginationFilterBySentiment", filterbySentiment );
                    }

                    filterbyStatus = request.getParameter("filterbyStatus");
                    if (filterbyStatus == null || filterbyStatus.length() == 0 || filterbyStatus.equals("none")) {
                        if(filterbyStatus != null && filterbyStatus.equals("none")) {
                            filterbyStatus = "";
                          //  request.getSession().removeAttribute("paginationFilterByStatus" );
                        } /*else if (request.getSession().getAttribute("paginationFilterByStatus") != null) {
                            filterbyStatus = (String) request.getSession().getAttribute("paginationFilterByStatus");
                        }  */
                    }
                    /* status-new"   - 26512
                    // status-flag - 26513
                    // status-replied - 26514
                    // status-okay  - 26515
                    */
                    if (filterbyStatus != null && filterbyStatus.length() > 0) {
                        if (filterbyStatus.equals("26512")) {
                           // activeFilters++;
                            //request.getSession().setAttribute("paginationFilterByStatus", filterbyStatus);
                            statusFilterParam = " and nvl(commentStatus, '26512') like '" + filterbyStatus + "' ";
                        }else {
                            statusFilterParam = " and commentStatus like '" + filterbyStatus + "' ";
                            //activeFilters++;
                            //request.getSession().setAttribute("paginationFilterByStatus", filterbyStatus);
                        }
                    }
                    //request.getSession().setAttribute("activeFilters", activeFilters);

                    /* new columns - DISPLAYORDER - value for first comment - 0; have to update all the comments with 0 for this column
                                   - BRANDRESPONSESTATUS - helpfull, nothelpfull
                                   - COMMENTSTATUS  -  new, flag, replied, okay
                                   - BRANDUSERID -
                                   - insert time into branduseractivitylog
                                   - PRIVATECOMMENT - pprivate Y  public N
                                   -
                    */

                }else{

                }
            }

            String strTempValue = "";
            HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
            HashMap<String,String> objHMSort = new HashMap<String,String>();
            HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdShortTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo1X = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo2X = new HashMap<String,String>();
            HashMap<String,String> objHMIdParentSectorTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdSectorId = new HashMap<String,String>();
            HashMap<String,String> objHMIdSectorTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdDealers = new HashMap<String,String>();
            HashMap<String,String> objHMIdCountry = new HashMap<String,String>();

            HashMap<String,String> objHMIdComments = new HashMap<String,String>();
            HashMap<String,String> objHMIdNominations = new HashMap<String,String>();
            HashMap<String,String> objHMIdWins = new HashMap<String,String>();
            HashMap<String,String> objHMIdTrends = new HashMap<String,String>();
            HashMap<String,String> objHMIdStarRating = new HashMap<String,String>();
            HashMap<String,String> objHMIdAwards = new HashMap<String,String>();


            HashMap<String,String> objHMSortTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdTitleTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdShortTitleTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo1XTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo2XTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdParentSectorTitleTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdSectorIdTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdSectorTitleTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdDealersTemp = new HashMap<String,String>();

            HashMap<String,String> objHMIdCommentsTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdNominationsTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdWinsTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdTrendsTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdStarRatingTemp = new HashMap<String,String>();
            HashMap<String,String> objHMIdAwardsTemp = new HashMap<String,String>();
            connection = dataSource.getConnection();



            int brandCode = 0, sectorcode = 0;   long lBrandUserDealer = 0;
            if(regionName.equals("ThankYou") || regionName.equals("BrandInfo") || regionName.equals("BrandDashboard")) {

                strTemp = request.getParameter("brandCode");
                if(strTemp == null || strTemp.length() == 0)
                    brandCode = 0;
                else
                    brandCode = Integer.parseInt(strTemp);
                if(brandCode == 0) {
                    if(regionName.equals("BrandDashboard")) {
                        //strTemp = String.valueOf(request.getSession().getAttribute("dashboardBrand"));
                        strTemp = request.getParameter("brand");
                        if (strTemp == null || strTemp.length() == 0 || strTemp.indexOf("~") > -1){
                            brandCode = 0;
                        }   else  {

                            StringTokenizer strBrand = new java.util.StringTokenizer(strTemp, "~");
                            sectorcode =  Integer.parseInt(strBrand.nextToken());
                            brandCode =   Integer.parseInt(strBrand.nextToken());
                            String strTempDealer = strBrand.nextToken();
                            if(strTempDealer == null || strTempDealer.length() == 0 || strTempDealer.equals("N/A"))
                                lBrandUserDealer = 0;
                           else
                               lBrandUserDealer = Integer.parseInt(strTempDealer);
                        }

                    }else{
                        strTemp = String.valueOf(request.getSession().getAttribute("surveyBrand"));
                    }
                    if(strTemp == null || strTemp.length() == 0)
                        brandCode = 0;
                    else
                        brandCode = Integer.parseInt(strTemp);
                }
                if(regionName.equals("BrandDashboard")) {
                   // request.getSession().setAttribute("dashboardBrand", String.valueOf(brandCode));
                }else{
                    request.getSession().setAttribute("surveyBrand", String.valueOf(brandCode));
                }


                objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, String.valueOf(brandCode));
                objHMSort = (HashMap)objHM.get("sorter");
                objHMIdTitle = (HashMap)objHM.get("title");
                objHMIdShortTitle = (HashMap)objHM.get("shortTitle");
                objHMIdLogo1X = (HashMap)objHM.get("logo1X");
                objHMIdLogo2X = (HashMap)objHM.get("logo2X");
                objHMIdParentSectorTitle = (HashMap)objHM.get("parentSectorTitle");
                objHMIdCountry = (HashMap)objHM.get("countryCode")  ;
                objHMIdSectorId = (HashMap)objHM.get("sectorId");
                objHMIdSectorTitle = (HashMap)objHM.get("sectorTitle");
                objHMIdDealers = (HashMap)objHM.get("dealers");

                objHMIdComments = (HashMap)objHM.get("commentsCount");
                objHMIdNominations = (HashMap)objHM.get("nominationsCount");
                objHMIdWins = (HashMap)objHM.get("winsCount");
                objHMIdTrends = (HashMap)objHM.get("trends");
                objHMIdStarRating = (HashMap)objHM.get("starRating");
                objHMIdAwards = (HashMap)objHM.get("awards");

                List<String> sorterList = new ArrayList<String>(objHMSort.keySet());
                Collections.sort(sorterList);
                for (String sortValue : sorterList) {
                    strTempValue = objHMSort.get(sortValue);
                    

                    /* code here to insert count of comments : call function */
                   if(regionName.equals("BrandDashboard")) {

                       // set the last refreshtim
                       request.getSession().setAttribute("lastRefreshTime", System.currentTimeMillis())    ;

                       SHBrandUser shBrandUser = (SHBrandUser)request.getSession().getAttribute("shBrandUser");

                       lBrandUserId =  shBrandUser.getShUserId();

                       String sBrandUserCountry = objHMIdCountry.get(strTempValue);
                       String strTempDealer = String.valueOf(request.getSession().getAttribute("dashboardDealer"));

                        
                    }
                    if(objHMIdShortTitle.get(strTempValue) != null) {
                        strTempValue = objHMIdShortTitle.get(strTempValue);
                        if(strTempValue.endsWith(","))
                            strTempValue = strTempValue.substring(0, strTempValue.length() - 1);
                        request.getSession().setAttribute("brandShortTitle", strTempValue);
                    }

                }

                String otherUserCountry = "", userCountry = (String) request.getSession().getAttribute("userCountry");
                if(userCountry.equals("kw")) {
                    otherUserCountry = "ae";
                } else if(userCountry.equals("ae")) {
                    otherUserCountry = "kw";
                }
                String switchCountry;
                objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, String.valueOf(brandCode), "titlevalue", otherUserCountry);
                objHMSort = (HashMap)objHM.get("sorter");
                if(objHMSort.size() == 1) {
                    switchCountry = "1";
                } else {
                    switchCountry = "0";
                }
                objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR, String.valueOf("" + request.getSession().getAttribute("surveySector")), "titlevalue", otherUserCountry);
                objHMSort = (HashMap)objHM.get("sorter");
                if(objHMSort.size() > 1) {
                    switchCountry += "1";
                } else {
                    switchCountry += "0";
                }
                request.getSession().setAttribute("switchingCountryPossible", switchCountry);
            }

            SHSurveyUser shSurveyUser = (SHSurveyUser)request.getSession().getAttribute("shSurveyUser");

            PreparedStatement ps = null;
            int rowCount = 0, pageCount = 0;
            if(regionName.equals("OtherArticles")) {
                String platform = (String)request.getSession().getAttribute("platform");
                if(platform == null || platform.length() == 0)
                    platform = "Website";

                int commentMaxSize = 70;
                if(platform.length() > 0 && !platform.equals("Mobile")) {
                    commentMaxSize = 210;
                } else if(platform.length() > 0 && (platform.equals("Mobile") || platform.equals("MobileApp"))) {
                    commentMaxSize = 70;
                }
                strQuery = "select * from (select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode='" + ((String) request.getSession().getAttribute("userCountry")) + "' and lower(a.approvedLanguageCode)='" + ((String)request.getSession().getAttribute("userLanguage")) + "' and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and approveComment <> '-' and length(approveComment) > 30 and length(approveComment) < " + commentMaxSize + " and a.shUserId=b.shUserId" + sortParam + ") where rownum<=20 order by rownum";
                ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } else if(regionName.equals("ThankYou") || regionName.equals("BrandInfo")  ) {
                int count = 0;
                if(regionName.equals("ThankYou"))
                    strQuery = "select * from (select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode='" + ((String) request.getSession().getAttribute("userCountry")) + "' and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and a.shUserId=b.shUserId and approveComment <> '-' and a.brandCode=?" + sortParam + ") where rownum<=3 order by rownum";
                else
                    strQuery = "select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode='" + ((String) request.getSession().getAttribute("userCountry")) + "' and a.shUserId=b.shUserId and b.shUserId=c.id and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and approveComment <> '-' and a.brandCode=?" + sortParam;
                ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setString(1, String.valueOf(brandCode));
            } else if(regionName.equals("BrandDashboard") ) {
                int count = 0;
                strQuery = "select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode='" + ((String) request.getSession().getAttribute("userCountry")) + "' and a.shUserId=b.shUserId and b.shUserId=c.id and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and approveComment <> '-' and a.brandCode=?"+ratingFilterParam + statusFilterParam + responseFilterParam+ sentimentFilterParam + sortParam;
                ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setString(1, String.valueOf(brandCode));
            } else if(regionName.equals("UserDetail")) {
                if(((String) request.getSession().getAttribute("socialshareUrl")).indexOf("my-message") > -1)
                    strQuery = "select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and a.shUserId=b.shUserId and nvl(approveComment, 'N/A')  <> '-' and a.shUserId=?" + sortParam;
                else
                    strQuery = "select * from (select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and a.shUserId=b.shUserId and nvl(approveComment, 'N/A')  <> '-' and a.shUserId=?" + sortParam + ") where rownum<=4 order by rownum";
                ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setLong(1, shSurveyUser.getId());
            }
            LOG.error(" query is "+strQuery);
            long diff = 0, minutes=0;
            ResultSet objRs = ps.executeQuery();
            try {
                objRs.last();
                rowCount = objRs.getRow();
                pageCount = (rowCount / 10);
                if(rowCount % 10 > 0)
                    pageCount++;
                objRs.beforeFirst();
            }
            catch(Exception ex) {
                rowCount = 0;
            }
            request.getSession().setAttribute("paginationSort", sort);
            request.getSession().setAttribute("paginationItemNumber", String.valueOf(rowCount));
            request.getSession().setAttribute("paginationNumber", String.valueOf(pageNo));
            request.getSession().setAttribute("paginationCount", String.valueOf(pageCount));

            boolean showWelcomeMsg = false;
            long tempDate = 0;

            String brandIds = "", uniqueIds = ",";
            if(rowCount > 0) {
                if(regionName.equals("OtherArticles") || regionName.equals("UserDetail")) {
                    while(objRs.next()) {
                        if(!objRs.getString("brandCode").equals("N/A") && brandIds.indexOf(objRs.getString("brandCode") + ",") == -1) {
                            brandIds += objRs.getString("brandCode") + ",";
                        }
                    }
                    brandIds = brandIds.substring(0, brandIds.length() - 1);
                }

                for (int i = (((pageNo - 1) * 10) + 1); i <= (pageNo * 10) ; i++) {
                    objRs.absolute(i);
                    if(i % 10 == 1) {
                        if(regionName.equals("OtherArticles") || regionName.equals("UserDetail")) {
                            objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandIds, "titlevalue", "kw");
                            objHMSortTemp = (HashMap) objHM.get("sorter");
                            objHMIdTitleTemp = (HashMap) objHM.get("title");
                            objHMIdShortTitleTemp = (HashMap)objHM.get("shortTitle");
                            objHMIdLogo1XTemp = (HashMap) objHM.get("logo1X");
                            objHMIdLogo2XTemp = (HashMap) objHM.get("logo2X");
                            objHMIdParentSectorTitleTemp = (HashMap)objHM.get("parentSectorTitle");
                            objHMIdSectorIdTemp = (HashMap)objHM.get("sectorId");
                            objHMIdSectorTitleTemp = (HashMap)objHM.get("sectorTitle");

                            objHMSort.putAll(objHMSortTemp);
                            objHMIdTitle.putAll(objHMIdTitleTemp);
                            objHMIdShortTitle.putAll(objHMIdShortTitleTemp);
                            objHMIdLogo1X.putAll(objHMIdLogo1XTemp);
                            objHMIdLogo2X.putAll(objHMIdLogo2XTemp);
                            objHMIdParentSectorTitle.putAll(objHMIdParentSectorTitleTemp);
                            objHMIdSectorId.putAll(objHMIdSectorIdTemp);
                            objHMIdSectorTitle.putAll(objHMIdSectorTitleTemp);

                            objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandIds, "titlevalue", "ae");
                            objHMSortTemp = (HashMap) objHM.get("sorter");
                            objHMIdTitleTemp = (HashMap) objHM.get("title");
                            objHMIdShortTitleTemp = (HashMap)objHM.get("shortTitle");
                            objHMIdLogo1XTemp = (HashMap) objHM.get("logo1X");
                            objHMIdLogo2XTemp = (HashMap) objHM.get("logo2X");
                            objHMIdParentSectorTitleTemp = (HashMap)objHM.get("parentSectorTitle");
                            objHMIdSectorIdTemp = (HashMap)objHM.get("sectorId");
                            objHMIdSectorTitleTemp = (HashMap)objHM.get("sectorTitle");

                            objHMSort.putAll(objHMSortTemp);
                            objHMIdTitle.putAll(objHMIdTitleTemp);
                            objHMIdShortTitle.putAll(objHMIdShortTitleTemp);
                            objHMIdLogo1X.putAll(objHMIdLogo1XTemp);
                            objHMIdLogo2X.putAll(objHMIdLogo2XTemp);
                            objHMIdParentSectorTitle.putAll(objHMIdParentSectorTitleTemp);
                            objHMIdSectorId.putAll(objHMIdSectorIdTemp);
                            objHMIdSectorTitle.putAll(objHMIdSectorTitleTemp);
	        				
	        				
	        				/*objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandIds);
	        				objHMSort = (HashMap)objHM.get("sorter");
	                		objHMIdTitle = (HashMap)objHM.get("title");
	                		objHMIdShortTitle = (HashMap)objHM.get("shortTitle");	
	                		objHMIdLogo1X = (HashMap)objHM.get("logo1X");
	                		objHMIdLogo2X = (HashMap)objHM.get("logo2X");	
	                		objHMIdParentSectorTitle = (HashMap)objHM.get("parentSectorTitle");	
	                		objHMIdSectorId = (HashMap)objHM.get("sectorId");	
	                		objHMIdSectorTitle = (HashMap)objHM.get("sectorTitle");	*/

                        }
                    }

                    strTempValue = objRs.getString("brandCode");

                    //if(regionName.equals("UserDetail"))

                    if(objHMIdTitle.get(strTempValue) != null || regionName.equals("UserDetail")) {
                        if(!regionName.equals("OtherArticles") || (regionName.equals("OtherArticles") && uniqueIds.indexOf("," + strTempValue + ",") == -1)) {
                            if(regionName.equals("OtherArticles"))
                                uniqueIds += strTempValue + ",";
                            tempDate = objRs.getTimestamp("actionTime").getTime();
                            if(!showWelcomeMsg && regionName.equals("UserDetail") && objRs.getTimestamp("actionTime").getTime() < 1452448800000l) {
                                userComment = new UserComment();
                                userComment.setCommentId(1452448800000l);
                                userComment.setShUserId(10000);
                                userComment.setShUserName("Service Hero");
                                userComment.setBrandCode(0);
                                userComment.setBrandName("Service Hero");
                                userComment.setLogo1x("");
                                userComment.setLogo2x("");
                                userComment.setCountryCode("kw");
                                userComment.setApproved("Y");
                                userComment.setLanguageCode((String)request.getSession().getAttribute("userLanguage"));
                                userComment.setComment("welcomeNewWebsite");
                                userComment.setRatingCode(0);
                                commentList.add(userComment);
                                showWelcomeMsg = true;
                            }

                            userComment = new UserComment();
                            userComment.setCommentId(objRs.getLong("id"));
                            userComment.setShUserId(objRs.getLong("shUserId"));
                            userComment.setShUserName(objRs.getString("personName"));
                            userComment.setBrandCode(objRs.getLong("brandCode"));
                            userComment.setBrandName(objHMIdTitle.get(strTempValue));
                            userComment.setLogo1x(objHMIdLogo1X.get(strTempValue));
                            userComment.setLogo2x(objHMIdLogo2X.get(strTempValue));
                            userComment.setCountryCode(objRs.getString("countryCode"));
                            userComment.setApproved(objRs.getString("commentApproved"));

                            if(regionName .equals("BrandDashboard"))  {
                                userComment.setCommentStatus(objRs.getString("commentStatus"));
                                if(objRs.getString("lockstatus") != null && objRs.getString("lockstatus").toString().equals("Y"))  {
                                    //query to fetch the lockedby branduserid and details
                                    int count = 0;
                                    strQuery = "select b.BRANDUSERID, b.actiontime, c.fname from branduseractivitylog b, brandusers c where b.actiontime in (select max(actiontime) from branduseractivitylog where actiontype = '" + UserActivityLog.ACTIVITY_DASHBOARD_COMMENT_LOCK + "' and actiontypeid = ?) and c.id = b.branduserid and c.id <> ?";
                                    ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                                    ps.setLong(1, userComment.getCommentId());
                                    ps.setLong(2, lBrandUserId );
                                    ResultSet objLockedRs = ps.executeQuery();

                                    while(objLockedRs.next()) {
                                        diff=  (System.currentTimeMillis() - objLockedRs.getTimestamp("actionTime").getTime()) ;
                                        //LOG.error(" user comment id "+diff);
                                        minutes= TimeUnit.MILLISECONDS.toMinutes(diff);
                                        //diff=diff-(minutes *60*1000);
                                        LOG.error(" user comment id "+userComment.getCommentId()+" for "+minutes+ " minutes");
                                       // LOG.error(" user comment id "+minutes);

                                        if(minutes <= 20 ) {    // have to parameterize
                                            userComment.setLockStatus(objRs.getString("lockstatus"));
                                            userComment.setLockByBrandUserId(objLockedRs.getLong("branduserid"));
                                            userComment.setLockByUserName(objLockedRs.getString("fname"));
                                            userComment.setLockDate(new DateTime(objLockedRs.getTimestamp("actionTime")));
                                        }else{
                                            //unlock the comment

                                        }
                                    }

                                }
                            }

                            if(regionName.equals("UserDetail")) {
                                if(objRs.getString("approveComment") == null || objRs.getString("approveComment").equals("N/A")) {
                                    userComment.setLanguageCode(objRs.getString("languageCode"));
                                    userComment.setComment(objRs.getString("originalComment"));
                                } else {
                                    userComment.setLanguageCode(objRs.getString("approvedlanguageCode"));
                                    userComment.setComment(objRs.getString("approveComment"));
                                }
                            } else {
                                userComment.setLanguageCode(objRs.getString("approvedlanguageCode"));
                                userComment.setComment(objRs.getString("approveComment"));
                            }
                            //userComment.setComment(objRs.getString("originalComment"));
                            userComment.setStarRating(objRs.getString("starRating"));
                            userComment.setSubmitDate(new DateTime(objRs.getTimestamp("actionTime")));
                            try {
                                userComment.setRatingCode(Integer.parseInt(objRs.getString("adminRating")));
                            } catch(Exception ex) {
                                userComment.setRatingCode(0);
                            }
                            userComment.setAgreeCount(objRs.getInt("agree"));
                            userComment.setDisagreeCount(objRs.getInt("disagree"));
                            userComment.setFbShareCount(objRs.getInt("fbShare"));
                            userComment.setTwShareCount(objRs.getInt("twShare"));

                            // check if this has child comments
                            childCommentCount = checkHasSubCommentList(connection,userComment.getCommentId()) ;
                            if (childCommentCount >0){
                                userComment.setHasSubComment("Y");
                            }   else {
                                userComment.setHasSubComment("N");
                            }

                            LOG.error("CommentId" + objRs.getLong("id") + " childCommentCount "+childCommentCount);
                            commentList.add(userComment);

                            /*get the child records */
                            if( userComment.getHasSubComment().equalsIgnoreCase("Y") &&(  regionName .equals("BrandDashboard") || regionName.equals("BrandInfo") || regionName.equals("UserDetail"))) {
                                getSubCommentList(connection, request, userComment.getCommentId(), regionName,  commentList) ;
                                 /*(for(int k = 0; i < commentList.size()-1; k++) {
                                    System.out.println(commentList.get(k).toString());
                                }  */
                            }
                        }
                    }

                    if(objRs.isLast())
                        break;
                }
            }

            if(regionName.equals("UserDetail") && (commentList.size() == 0 || tempDate > 1452448800000l)) {
                userComment = new UserComment();
                userComment.setCommentId(1452448800000l);
                userComment.setShUserId(10000);
                userComment.setShUserName("Service Hero");
                userComment.setBrandCode(0);
                userComment.setBrandName("Service Hero");
                userComment.setLogo1x("");
                userComment.setLogo2x("");
                userComment.setCountryCode("kw");
                userComment.setApproved("Y");
                userComment.setLanguageCode((String)request.getSession().getAttribute("userLanguage"));
                userComment.setComment("welcomeNewWebsite");
                userComment.setRatingCode(0);
                commentList.add(userComment);
            }

            if(regionName.equals("OtherArticles") && commentList.size() == 1) {
                commentList.add(userComment);
            }
        } catch(Exception e) {
            LOG.error(e.getMessage(),e);
        }finally {
            Utils.closeQuietly(connection);
        }        
        return commentList;
    }
	
	private int checkHasSubCommentList(Connection connection, long parentCommentid) {
        int childCommentCount = 0;
        PreparedStatement ps = null;
        String strQuery = "select count(*) cnt from v_comment_tree where parentCommentid=? order by actiontime";
        try{
            ps = connection.prepareStatement(strQuery);
            ps.setLong(1, parentCommentid );
            ResultSet objRs = ps.executeQuery();
            while(objRs.next()) {
                childCommentCount =  objRs.getInt("cnt") ;
            }
            objRs.close();
            ps.close();
        } catch(Exception e) {
           LOG.error(e.getMessage(),e);
        }
        return   childCommentCount;
    }
	
	private void getSubCommentList(Connection connection, HttpServletRequest request, long parentCommentid, String regionName,  List<UserComment> commentList ){
        String strQuery = "";
        UserComment usercomment;
        PreparedStatement ps = null;
        int childCommentCount = 0;
        //LOG.error("entered getSubCommentList for commentid "+parentCommentid);
        int count = 0;
        strQuery = "select id, approvecomment, approvedlanguagecode, branduserid, shuserid, personname, actiontime, commentstatus, brandresponsestatus, parentcommentid from v_comment_tree where parentCommentid=? order by actiontime";
        try{
            ps = connection.prepareStatement(strQuery);
            ps.setLong(1, parentCommentid );
            ResultSet objRs = ps.executeQuery();
            // need to display reply or flag comment, by whom when, comment status, privateindicator, brandresponsestatus, a

          //  LOG.error(" getSubCommentList executed commentList " +commentList.size());
           /* for(int k = 0; k < commentList.size(); k++) {
                LOG.error(" - "+(commentList.get(k).toString()));
            }*/

            while(objRs.next()) {
                usercomment = new UserComment();
                usercomment.setCommentId(objRs.getLong("id"));
               // LOG.error("entered getSubCommentList has child rows " + usercomment.getCommentId());
                usercomment.setComment(objRs.getString("approvecomment"));
                usercomment.setLanguageCode(objRs.getString("approvedlanguagecode"));
                if (objRs.getString("BrandUserId") != null) { // is brand response
                    usercomment.setShUserId(objRs.getLong("branduserid"));
                    usercomment.setShUserName(objRs.getString("personname"));
                } else {
                    usercomment.setShUserId(objRs.getLong("shuserid"));
                    usercomment.setShUserName("consumer");
                }


                usercomment.setSubmitDate(new DateTime(objRs.getTimestamp("actiontime")));
                usercomment.setCommentStatus(objRs.getString("commentstatus"));
                usercomment.setBrandResponseStatus(objRs.getString("brandresponsestatus"));

                usercomment.setParentCommentId(objRs.getLong("parentcommentid"));
                // check if this has child comments
                childCommentCount = checkHasSubCommentList(connection,(objRs.getLong("id"))) ;
                if (childCommentCount >0){
                    usercomment.setHasSubComment("Y");
                } else {
                    usercomment.setHasSubComment("N");
                }

                commentList.add(usercomment) ;
                LOG.error("CommentId" + objRs.getLong("id") + " childCommentCount " + childCommentCount);
                /*get the child records */
                if( usercomment.getHasSubComment().equalsIgnoreCase("Y") &&(  regionName .equals("BrandDashboard") || regionName.equals("BrandInfo") || regionName.equals("UserDetail"))) {
                    getSubCommentList(connection, request, usercomment.getCommentId(), regionName,  commentList) ;
                }
            }
            //LOG.error(" getSubCommentList executed commentList after " +commentList.size());
            /*for(int t = 0; t < commentList.size(); t++) {
                LOG.error(" - "+(commentList.get(t).toString()));

            } */
            objRs.close();
            ps.close();
        } catch(Exception e) {
            LOG.error(e.getMessage(),e);
        }
    }

}

