package com.khayal.shero;

import com.sdl.dxa.modules.generic.model.CountryRelatedBrandFields;
import com.sdl.dxa.modules.generic.model.CountryRelatedFields;
import com.sdl.dxa.modules.generic.utilclasses.CustomBrokerQuery;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.tridion.query.BrokerQueryException;
import com.tridion.meta.*;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by DELL on 11/1/2015.
 */
public class BrandLuceneIndexer {
    private static final Logger LOG = LoggerFactory.getLogger(BrandLuceneIndexer.class);

    public static final int kw = 0;
    public static final int ae = 1;
    public static final String DEFAULT_WEB_ROOT = "web-inf";
    public static final String DEFAULT_LUCENE_ROOT = "/lucene/brand-search";

    public static final String INDEXER_SERACH_FIELD_SECTOR = "sectorid";
    public static final String INDEXER_SERACH_FIELD_BRAND_CODE = "brandid";
    public static final String INDEXER_SERACH_FIELD_TITLE = "titlevalue";
    public static final String INDEXER_SERACH_FIELD_TITLE_SEARCH = "titlevaluesearch";
    public static final String INDEXER_SERACH_FIELD_SECTOR_TITLE_SEARCH = "sectortitlevaluesearch";
    public static final String INDEXER_SERACH_FIELD_SHORT_NAME= "trimtitlevalue";
    public static final String INDEXER_SERACH_FIELD_SHORTCUT_NAME= "shortcutName";
    public static final String INDEXER_SERACH_FIELD_AWARDS = "awards";

	/*
	PROCESS FLOW:
	1. Run the procedure sh_survey_mgmt.calc_all_brand_trends
	 */

    public static void  populateBrandSearchIndexer(HttpServletRequest request, WebRequestContext webRequestContext, String countryName) {

        int index = 0;
        if(countryName == null) {
            index = kw;
        } else {
            if(countryName.equals("kw"))
                index = kw;
            else if(countryName.equals("ae"))
                index = ae;
        }

        IndexWriter writer = null;
        Directory directory = null;
        try {
            //File dirFile;
            String dirIndexPathFrom, clientName = "";
            
            CharArraySet stopSet = CharArraySet.copy(Version.LUCENE_41, StandardAnalyzer.STOP_WORDS_SET);
            stopSet.add("ال");
            stopSet.add("al");
            stopSet.add("Al");

            String userLanguage = request.getParameter("indexingLanguage");
    	    if (userLanguage == null || userLanguage.length() == 0)
    	    	userLanguage = "N/A";

            if(userLanguage != null && !userLanguage.equals("N/A") && (userLanguage.equals("en") || userLanguage.equals("ar"))) {
	    	    IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_41, new ShingleAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_41, stopSet), 3));
	            conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
	            conf.setWriteLockTimeout(5 * 60 * 1000);
	            //TODO: we have to create document for kw-en, kw-ar, ae-en, ae-ar. So there should be a list
	
	            //dirIndexPathFrom =  request.getServletContext().getRealPath("/"+DEFAULT_WEB_ROOT+DEFAULT_LUCENE_ROOT+"/" +String.valueOf("kw-en/"));
	            final File dirFile = new File(request.getServletContext().getRealPath("/") + File.separator + BrandLuceneIndexer.DEFAULT_WEB_ROOT + File.separator + BrandLuceneIndexer.DEFAULT_LUCENE_ROOT + File.separator + countryName + "-" + userLanguage);
	            LOG.debug("getStaticContentFile: {}", dirFile);
	
	
	            //dirFile = new File(dirIndexPathFrom);
	            if (!dirFile.exists()) {
	                throw new LocalizationFactoryException("File not found: " + dirFile.getPath());
	            }
	            directory = FSDirectory.open(dirFile);
	            writer = new IndexWriter(directory, conf);
	            
	            
	            HashMap<String,String> objHMKuwaitCity = new HashMap<String,String>();
	            HashMap<String,String> objHMUAECity = new HashMap<String,String>();
	            Keyword objKey, objKey1;
	        	Iterator iterator, iterator1;
	        	List<Keyword> objKeyList, objKeyList1;
	            TaxonomyFactory objTaxonomy = new TaxonomyFactory();
	        	if(userLanguage.equals("ar"))
	        	    objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:10-86-512").getKeywordChildren();
	        	else 
	        	    objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:9-86-512").getKeywordChildren();
	        	String countryKeyName = "", strTempk;

	        	iterator = objKeyList.listIterator();
	        	while(iterator.hasNext()) {
	        		objKey = (Keyword) iterator.next();
	        		countryKeyName = objKey.getKeywordName();
	                objKeyList1 = objKey.getKeywordChildren();
	        		iterator1 = objKeyList1.listIterator();
	        		while(iterator1.hasNext()) {
	        			objKey1 = (Keyword) iterator1.next();
	        			strTempk = objKey1.getKeywordURI();
	        			if(countryKeyName.equals("Kuwait"))
	        				objHMKuwaitCity.put(objKey1.getKeywordDescription(), strTempk.substring(strTempk.indexOf("-") + 1, strTempk.lastIndexOf("-")));
	        			else if(countryKeyName.equals("UAE"))
	        				objHMUAECity.put(objKey1.getKeywordDescription(), strTempk.substring(strTempk.indexOf("-") + 1, strTempk.lastIndexOf("-")));
	        		}
	        	}
	            
	            
	            HashMap<String,String> objHMIdComments = new HashMap<String,String>();
	            HashMap<String,String> objHMIdNominations = new HashMap<String,String>();
				HashMap<String,String> objHMIdWins = new HashMap<String,String>();
				HashMap<String,String> objHMIdTrends = new HashMap<String,String>();
				HashMap<String,String> objHMIdStarRating = new HashMap<String,String>();
				HashMap<String,String> objHMIdVotes = new HashMap<String,String>();
				HashMap<String,String> objHMIdVotesByDealers = new HashMap<String,String>();
				HashMap<String,String> objHMIdAvgBrand = new HashMap<String,String>();
	            
	            ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
	        	PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
	        	Connection connection = null;
	        	try { 
	        		/*String strTemp = "";
					Iterator objIt = objHMIdAvgBrand.keySet().iterator();
					while(objIt.hasNext()) {
						strTemp = (String) objIt.next();
						objHMIdComments.remove(strTemp);
	            		objHMIdNominations.remove(strTemp);
	            		objHMIdWins.remove(strTemp);
	            		objHMIdTrends.remove(strTemp);
	            		objHMIdStarRating.remove(strTemp);
	            		objHMIdAvgBrand.remove(strTemp);
					}
					
					objIt = objHMIdVotes.keySet().iterator();
					while(objIt.hasNext()) {
						strTemp = (String) objIt.next();
						objHMIdVotes.remove(strTemp);
					}
					
					objIt = objHMIdVotesByDealers.keySet().iterator();
					while(objIt.hasNext()) {
						strTemp = (String) objIt.next();
						objHMIdVotesByDealers.remove(strTemp);
					}*/
	        		
	        		
	        		PreparedStatement ps = null;
	            	ResultSet objRs = null;
	            	connection = dataSource.getConnection();
	        		String strQuery = "select * from brandAverageScores_" + countryName;
	            	ps = connection.prepareStatement(strQuery);
	            	objRs = ps.executeQuery();
	            	while(objRs.next()) {
	            		objHMIdComments.put(objRs.getString("brandCode"), objRs.getString("comments"));
	            		objHMIdNominations.put(objRs.getString("brandCode"), objRs.getString("nominations"));
	            		objHMIdWins.put(objRs.getString("brandCode"), objRs.getString("wins"));
	            		objHMIdTrends.put(objRs.getString("brandCode"), objRs.getString("trends"));
	            		objHMIdStarRating.put(objRs.getString("brandCode"), objRs.getString("starRating"));
	            		objHMIdAvgBrand.put(objRs.getString("brandCode"), objRs.getString("averageBrand"));
	            	}
	        		objRs.close();
	        		ps.close();
	        		
	        		strQuery = "select sectorCode, brandCode, count(*) from surveySubmission_" + countryName + " where validVote='Y' group by sectorCode, brandCode";
	            	ps = connection.prepareStatement(strQuery);
	            	objRs = ps.executeQuery();
	            	while(objRs.next()) {
	            		objHMIdVotes.put(objRs.getString("brandCode"), String.valueOf(10000000 + objRs.getInt(3)));
	            	}
	        		objRs.close();
	        		ps.close();
	
	        		strQuery = "select sectorCode, brandCode, dealerCode, count(*) from surveySubmission_" + countryName + " where validVote='Y' group by sectorCode, brandCode, dealerCode";
	            	ps = connection.prepareStatement(strQuery);
	            	objRs = ps.executeQuery();
	            	while(objRs.next()) {
	            		objHMIdVotesByDealers.put(objRs.getString("brandCode") + "~" + objRs.getString("dealerCode"), String.valueOf(10000000 + objRs.getInt(4)));
	            	}
	        		objRs.close();
	        		ps.close();
	        		
	        		
	        		
	        	} catch(Exception e) {
	    			LOG.error(e.getMessage(), e);
	    		} finally {
	    			Utils.closeQuietly(connection);
	    		}
	        	
	        	int pubId = 8;
		    	if(userLanguage.indexOf("ar") > -1)
		    		pubId = 10;
		        else   	
		        	pubId = 9;
		    	
	            Map<String, String> objHMBrandPage = new HashMap<String,String>();
	        	objTaxonomy = new TaxonomyFactory();
	    	    objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-4963-512").getKeywordChildren();
	            iterator = objKeyList.listIterator();
	            
	            String strBrandDealer = "";
	            while(iterator.hasNext()) {
	                objKey = (Keyword) iterator.next();
	            	objHMBrandPage.put(objKey.getKeywordName(), objKey.getKeywordDescription());
	            }
	
	            final CustomBrokerQuery brokerQuery = new CustomBrokerQuery();
	            brokerQuery.setPublicationId(Integer.parseInt(webRequestContext.getLocalization().getId()));
	            brokerQuery.setSchemaId(434);
	            //brokerQuery.setSort(contentList.getSort().getKey());
	            List<String> entityIds;
	            Entity entity;
	            
	            int wins = 0, nominations = 0, honorableMention = 0, sectorWins = 0, fiveTimeWins = 0, countryWins = 0;
	            String awardTemp = "", award = "", awardString = "";
	
	            String allBrandValue = "", brandCode = "", active = "";
	            boolean addBrand = false;
	            try {
	                /* first get the brands */
	                Map<String, String> tmBrands = getBrandListFromDB(request, countryName) ;
	                ComponentMetaFactory cmf;
	                ComponentMetaFactory cmfLang1;
	                
	                if(((String) request.getSession().getAttribute("socialshareUrl")).indexOf("/ar/") > -1) {
	                    cmf = new ComponentMetaFactory(10);
	                    cmfLang1 = new ComponentMetaFactory(9);
	                } else {
	                    cmf = new ComponentMetaFactory(9);
	                    cmfLang1 = new ComponentMetaFactory(10);
	                }
	                String strLang1Values = "";
	                ComponentMeta compMeta, compMetaLang1;
	                CustomMeta customMeta, customMetaLang1;
	                ComponentMeta tempCompMeta, tempCompMetaLang1;
	                CustomMeta tempCustomMeta, tempCustomMetaLang1;
	                CountryRelatedBrandFields brandFields, brandFieldsLang1;
	                CountryRelatedFields sectorFields, sectorFieldsLang1, parentSectorFields, parentSectorFieldsLang1;
	                String sectorid, parentSectorid; 
	                String sectorsKeywords = "", shortKeywords, shortcutName, priority;
	                
	                Document doc = new Document();
	                FieldType keywordFieldType = new FieldType();
	                
                    String dealerString = "", dealer= "", dealerName ="", strTemp1 = "", strTemp2 = "", allCityString = "", cityString = "", city = "", cityName = "";
                    //countryFields.setDealer((Dealer) meta.getNameValues().get("dealer").getMultipleValues().get(index));
                    ComponentMeta dealerCompMeta = null ;
                    CustomMeta dealerCustomMeta  = null;
                    String relatedBrandsString = "", rBrands= "", rBrandsName ="";
                    ComponentMeta rBrandCompMeta = null ;
                    CustomMeta rBrandCustomMeta  = null;

	                entityIds = brokerQuery.executeQuery();
	                for (String entityId : entityIds) {
	                    LOG.debug("getEntity: {}", entityId);
	                    addBrand = false;
	                    brandCode = entityId.substring(entityId.indexOf("-") + 1, entityId.lastIndexOf("-"));
	                    compMeta = cmf.getMeta(entityId);
	                    if(entityId.indexOf(":9-") > -1) {
	                    	compMetaLang1 = cmfLang1.getMeta(entityId.replace(":9-", ":10-"));
	                    }  else {
	                    	compMetaLang1 = cmfLang1.getMeta(entityId.replace(":10-", ":9-"));
	                    }
	                    
	                    if (compMeta != null && compMetaLang1 != null) {
	                    	customMeta = compMeta.getCustomMeta();
	                    	customMetaLang1 = compMetaLang1.getCustomMeta();
	                        
	                    	active = customMeta.getFirstValue("active").toString();
	                    	//if(active.indexOf("No") > -1)
	                    	//	addBrand = false;
	                    	//else {
		                        for(int i = 0; i < customMeta.getNameValues().get("countryList").getMultipleValues().size(); i++) {
		                            if(countryName.equals("kw") && ((String) customMeta.getNameValues().get("countryList").getMultipleValues().get(i)).equals("Kuwait")) {
		                                addBrand = true;
		                            } else if(countryName.equals("ae") && ((String) customMeta.getNameValues().get("countryList").getMultipleValues().get(i)).equals("UAE")) {
		                                addBrand = true;
		                            }
		                        }
	                    	//}
	
	                        if(addBrand) 
	                        {
	                        	dealerString = ""; dealer= ""; dealerName =""; allCityString = ""; cityString = ""; city = ""; cityName = "";
	                        	/* get Dealer */
	                            if(customMeta.getNameValues().get("dealer") != null) {
	                                for (int i = 0; i < customMeta.getNameValues().get("dealer").getMultipleValues().size(); i++) {
	                                	cityString = "";	                                	
	                                    dealer = (String) customMeta.getNameValues().get("dealer").getMultipleValues().get(i);
	                                    dealerCompMeta = cmf.getMeta(dealer);
	                                    if (dealerCompMeta != null) {
	                                        dealerCustomMeta = dealerCompMeta.getCustomMeta();
	                                        if (dealerCustomMeta.getNameValues().get("countryName") != null) {
	                                            if (countryName.equals("kw") && ((String) dealerCustomMeta.getNameValues().get("countryName").getFirstValue()).equals("Kuwait")) {
	                                                if (dealerCustomMeta.getNameValues().get("caption") != null) {
	                                                    dealerName = dealerCustomMeta.getNameValues().get("caption").getFirstValue().toString();
	                                                    strTemp1 = objHMBrandPage.get(countryName + "|" + brandCode + "|" + dealer.substring(dealer.indexOf("-") + 1)); 
	                                                    if(strTemp1 == null || strTemp1.length() == 0)
	                                                    	strTemp1 = "0";
	                                                    strTemp2 = objHMIdVotesByDealers.get(brandCode + "~" + dealer.substring(dealer.indexOf("-") + 1)); 
	                                                    if(strTemp2 == null || strTemp2.length() == 0)
	                                                    	strTemp2 = "0";
	                                                    if(!strTemp1.equals("0")) {
	                                                    	if(dealerString.indexOf(dealer.substring(dealer.indexOf("-") + 1) + "|" + dealerName + "|" + strTemp1 + "|" + strTemp2 + "^") == -1) { 
	                                                    		dealerString = dealerString + dealer.substring(dealer.indexOf("-") + 1) + "|" + dealerName + "|" + strTemp1 + "|" + strTemp2 + "^";
	                                                    		cityString += dealer.substring(dealer.indexOf("-") + 1) + ":";
	                        	                                if(dealerCustomMeta.getNameValues().get("cityName") != null) {
		                                                    		for (int j = 0; j < dealerCustomMeta.getNameValues().get("cityName").getMultipleValues().size(); j++) {
		                                                    			strTempk = (String) dealerCustomMeta.getNameValues().get("cityName").getMultipleValues().get(j);
		                                                    			if(objHMKuwaitCity.get(strTempk) != null)
		                                                    				cityString += objHMKuwaitCity.get(strTempk) +"@" + strTempk + "|";
		                        	                                }
	                        	                                }
	                                                    		cityString += "$";
	                                                    	}
	                                                    }
	                                                }
	                                            } else if (countryName.equals("ae") && ((String) dealerCustomMeta.getNameValues().get("countryName").getFirstValue()).equals("UAE")) {
	                                                if (dealerCustomMeta.getNameValues().get("caption") != null) {
	                                                    dealerName = dealerCustomMeta.getNameValues().get("caption").getFirstValue().toString();
	                                                    strTemp1 = objHMBrandPage.get(countryName + "|" + brandCode + "|" + dealer.substring(dealer.indexOf("-") + 1)); 
	                                                    if(strTemp1 == null || strTemp1.length() == 0)
	                                                    	strTemp1 = "0";
	                                                    strTemp2 = objHMIdVotesByDealers.get(brandCode + "~" + dealer.substring(dealer.indexOf("-") + 1)); 
	                                                    if(strTemp2 == null || strTemp2.length() == 0)
	                                                    	strTemp2 = "0";
	                                                    if(!strTemp1.equals("0")) {
	                                                    	if(dealerString.indexOf(dealer.substring(dealer.indexOf("-") + 1) + "|" + dealerName + "|" + strTemp1 + "|" + strTemp2 + "^") == -1) {
	                                                    		dealerString = dealerString + dealer.substring(dealer.indexOf("-") + 1) + "|" + dealerName + "|" + strTemp1 + "|" + strTemp2 + "^";
	                                                    		cityString += dealer.substring(dealer.indexOf("-") + 1) + ":";
	                        	                                if(dealerCustomMeta.getNameValues().get("cityName") != null) {
	                        	                                	for (int j = 0; j < dealerCustomMeta.getNameValues().get("cityName").getMultipleValues().size(); j++) {
		                                                    			strTempk = (String) dealerCustomMeta.getNameValues().get("cityName").getMultipleValues().get(j);
		                                                    			if(objHMUAECity.get(strTempk) != null)
		                                                    				cityString += objHMUAECity.get(strTempk) +"@" + strTempk + "|";
		                        	                                }
	                        	                                }
	                                                    		cityString += "$";
	                                                    	}
	                                                    }
	                                                }
	                                            }
	                                        }
	                                    }
	                                    allCityString += cityString;
	                                }
	                                LOG.debug("dealerString: " + dealerString);
	                                LOG.debug("cityString: " + allCityString);
	                            }
	                            if(dealerString != null && dealerString.length() > 0)
	                            	dealerString = dealerString.substring(0, dealerString.length() - 1);
	                            if(allCityString != null && allCityString.length() > 0)
	                            	allCityString = allCityString.substring(0, allCityString.length() - 1);
	                            
	                            /* get awards*/
	                            wins = 0; nominations = 0; honorableMention = 0; sectorWins = 0; fiveTimeWins = 0; countryWins = 0;
	                            awardTemp = ""; award = ""; awardString = "";
	                            if(countryName.equals("kw") ) {
	    	                        if(customMeta.getNameValues().get("awardYear") != null) {
	    	                        	for(int i = 0; i < customMeta.getNameValues().get("awardYear").getMultipleValues().size(); i++) {
		                                	award = "";
		                                	award += (String) customMeta.getNameValues().get("awardYear").getMultipleValues().get(i) + "|";
		                                	awardTemp = (String) customMeta.getNameValues().get("awardType").getMultipleValues().get(i); 
		                                	if(awardTemp.indexOf("010") > -1) {
		                                		honorableMention++;
		                                	} else if(awardTemp.indexOf("020") > -1) {
		                                		sectorWins++;
		                                	} else if(awardTemp.indexOf("030") > -1) {
		                                		nominations++;
		                                	} else if(awardTemp.indexOf("040") > -1) {
		                                		fiveTimeWins++;
		                                	} else if(awardTemp.indexOf("050") > -1 || awardTemp.indexOf("051") > -1 || awardTemp.indexOf("052") > -1 || awardTemp.indexOf("053") > -1) {
		                                		countryWins++;
		                                	}
		                                	award += awardTemp + "|";
		                                    award += getMediaItemFromCustomMeta(customMeta, "award1X", i) + "|";
		                                    try {
		                                    award += getMediaItemFromCustomMeta(customMeta, "award2X", i);
		                                    } catch(Exception ax) {
			                                    award += "None";
		                                    }
		                                    awardString += award + "^";
		                                }
		                                //LOG.error("awardString: " + entityId + " : " + awardString);
		                            }
	                            }
	                            if(awardString != null && awardString.length() > 0)
	                            	awardString = awardString.substring(0, awardString.length() - 1);
	                            
	                            relatedBrandsString = ""; rBrandsName= ""; rBrands = "";
	                            if(customMeta.getNameValues().get("relatedBrands") != null) {
	                                for (int i = 0; i < customMeta.getNameValues().get("relatedBrands").getMultipleValues().size(); i++) {
	                                	rBrands = (String) customMeta.getNameValues().get("relatedBrands").getMultipleValues().get(i);
	                                	rBrandCompMeta = cmf.getMeta(rBrands);
	                                    if (rBrandCompMeta != null) {
	                                    	rBrandCustomMeta = rBrandCompMeta.getCustomMeta();
	                                    	strTemp1 = getTextFromCustomMeta(rBrandCustomMeta, "sector");
	                                    	tempCompMeta = cmf.getMeta(strTemp1);
	    		                            strTemp1 = strTemp1.substring(strTemp1.indexOf("-") + 1);
	                                    	rBrandsName = strTemp1 + "|" + rBrands.substring(rBrands.indexOf("-") + 1);
	    		                            tempCustomMeta = tempCompMeta.getCustomMeta();
	    		                            sectorFields = getCountryFieldsFromCustomMeta(tempCustomMeta, index);
	                                    	rBrandsName += "|" + sectorFields.getTitle().trim();
	                                    }
	                                	relatedBrandsString += rBrandsName + "^";
	                                }
	                            }
	                            if(relatedBrandsString != null && relatedBrandsString.length() > 0)
	                            	relatedBrandsString = relatedBrandsString.substring(0, relatedBrandsString.length() - 1);
	                            
	                            shortKeywords = getTextFromCustomMeta(customMeta, "shortName");
	                            /*strLang1Values = getTextFromCustomMeta(customMetaLang1, "shortName");
	                            LOG.error("en Value: " + shortCode);
	                            LOG.error("ar Value: " + strLang1Values);*/
	                            
	                            shortcutName = getTextFromCustomMeta(customMeta, "shortcutName");
	                            if(shortcutName == null || shortcutName.length() == 0)
	                            	shortcutName = "???";
	
	                            priority = getTextFromCustomMeta(customMeta, "priority");
	                            if(priority == null || priority.length() == 0)
	                                priority = "No";
	
	                            doc = new Document();
	                            keywordFieldType = new FieldType();
	                            keywordFieldType.setStored(true);
	                            keywordFieldType.setIndexed(true);
	                            keywordFieldType.setTokenized(false);
	                            //doc.add(new Field("weightage", (tmBrands.get(entityId) != null ? tmBrands.get(entityId) : "100000"), keywordFieldType));
	                            doc.add(new Field("weightage", (objHMIdVotes.get(entityId.substring(entityId.indexOf("-") + 1, entityId.lastIndexOf("-"))) != null ? objHMIdVotes.get(entityId.substring(entityId.indexOf("-") + 1, entityId.lastIndexOf("-"))) : "0"), keywordFieldType));
	                            doc.add(new Field("brandid", entityId.substring(entityId.indexOf("-") + 1, entityId.lastIndexOf("-")), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("langid", String.valueOf("en"), Field.Store.YES, Field.Index.NOT_ANALYZED));
	                            doc.add(new Field("countryid", countryName, Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("active", active.substring(0, 1), Field.Store.YES, Field.Index.NOT_ANALYZED));
	
	                            /* Start get sectors*/
	                            try {
		                            sectorsKeywords = "";
		                            sectorid = getTextFromCustomMeta(customMeta, "sector");
		                            tempCompMeta = cmf.getMeta(sectorid);
		                            tempCustomMeta = tempCompMeta.getCustomMeta();
		                            sectorFields = getCountryFieldsFromCustomMeta(tempCustomMeta, index);
		                            
		                            doc.add(new Field("sectortitlevalue", sectorFields.getTitle().trim(), Field.Store.YES, Field.Index.ANALYZED));
		                            doc.add(new Field("sectorid", sectorid.substring(sectorid.indexOf("-") + 1), Field.Store.YES, Field.Index.ANALYZED));
		                            doc.add(new Field("sectorsvgimg", getMediaItemFromCustomMeta(tempCustomMeta, "svgImage", index), Field.Store.YES, Field.Index.ANALYZED));
		                            doc.add(new Field("sectornormalimg", getMediaItemFromCustomMeta(tempCustomMeta, "normalImage", index), Field.Store.YES, Field.Index.ANALYZED));
		
		                            parentSectorid = getTextFromCustomMeta(tempCustomMeta, "parentSector");
		                            tempCompMeta = cmf.getMeta(parentSectorid);
		                            tempCustomMeta = tempCompMeta.getCustomMeta();
		                            parentSectorFields = getCountryFieldsFromCustomMeta(tempCustomMeta, index);
		
		                            doc.add(new Field("parentsectortitlevalue", parentSectorFields.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
		                            doc.add(new Field("parentsectorid", parentSectorid.substring(parentSectorid.indexOf("-") + 1), Field.Store.YES, Field.Index.ANALYZED));
		                            try {
		                                doc.add(new Field("parentsectorsvgimg", getMediaItemFromCustomMeta(tempCustomMeta, "svgImage", index), Field.Store.YES, Field.Index.ANALYZED));
		                                doc.add(new Field("parentsectornormalimg", getMediaItemFromCustomMeta(tempCustomMeta, "normalImage", index), Field.Store.YES, Field.Index.ANALYZED));
		                            } catch (Exception e) {
		                                LOG.debug("Exception )))))) + "  + e.getMessage(), e);
		                            }
		
		                            if(sectorid.indexOf(":9-") > -1) {
		                            	tempCompMetaLang1 = cmfLang1.getMeta(sectorid.replace(":9-", ":10-"));
		                            }  else {
		                            	tempCompMetaLang1 = cmfLang1.getMeta(sectorid.replace(":10-", ":9-"));
		                            }
		                            tempCustomMetaLang1 = tempCompMetaLang1.getCustomMeta();
		                            sectorFieldsLang1 = getCountryFieldsFromCustomMeta(tempCustomMetaLang1, index);
		
		                            if(parentSectorid.indexOf(":9-") > -1) {
		                            	tempCompMetaLang1 = cmfLang1.getMeta(parentSectorid.replace(":9-", ":10-"));
		                            }  else {
		                            	tempCompMetaLang1 = cmfLang1.getMeta(parentSectorid.replace(":10-", ":9-"));
		                            }
		                            tempCustomMetaLang1 = tempCompMetaLang1.getCustomMeta();
		                            parentSectorFieldsLang1 = getCountryFieldsFromCustomMeta(tempCustomMetaLang1, index);
		                            sectorsKeywords += sectorFields.getTitle().trim() + "," + sectorFields.getTitle().trim().replace(" ", ",") + "," + sectorFields.getTitle().trim().replace(" ", "") + ","; 
		                            sectorsKeywords += parentSectorFields.getTitle().trim() + "," + parentSectorFields.getTitle().trim().replace(" ", ",") + "," + parentSectorFields.getTitle().trim().replace(" ", ""); 
		                            sectorsKeywords += sectorFieldsLang1.getTitle().trim() + "," + sectorFieldsLang1.getTitle().trim().replace(" ", ",") + "," + sectorFieldsLang1.getTitle().trim().replace(" ", "") + ","; 
		                            sectorsKeywords += parentSectorFieldsLang1.getTitle().trim() + "," + parentSectorFieldsLang1.getTitle().trim().replace(" ", ",") + "," + parentSectorFieldsLang1.getTitle().trim().replace(" ", ""); 
	                            } catch (Exception e) {
	                                LOG.error("No Sector found = "  + entityId);
	                            }
	                            
	                            /* End get sectors*/
	                            
	                            brandFields = getCountryBrandFieldsFromCustomMeta(customMeta, index, cmf);
	                            doc.add(new Field("titlevalue", brandFields.getTitle().trim(), Field.Store.YES, Field.Index.ANALYZED));
	                            brandFieldsLang1 = getCountryBrandFieldsFromCustomMeta(customMetaLang1, index, cmfLang1);
	                            allBrandValue = brandFields.getTitle().trim() + "," + brandFields.getTitle().trim().replace(" ", ",") + "," + brandFields.getTitle().trim().replace(" ", "") + ",";
	                            allBrandValue += brandFieldsLang1.getTitle().trim() + "," + brandFieldsLang1.getTitle().trim().replace(" ", ",") + "," + brandFieldsLang1.getTitle().trim().replace(" ", "");
	                            doc.add(new Field("titlevaluesearch", stripAccents(allBrandValue.toLowerCase().trim()), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("sectortitlevaluesearch", stripAccents(sectorsKeywords.toLowerCase().trim()), Field.Store.YES, Field.Index.ANALYZED));
	                            allBrandValue += "," + sectorsKeywords;
	                            if (clientName != null && clientName.toString().length() > 0) {
	                            	allBrandValue += "," + clientName;	
	                            }
	                            if (shortKeywords != null && shortKeywords.toString().length() > 0) {
	                            	allBrandValue += "," + shortKeywords.replace(" ", "");	
	                            }
	                            doc.add(new Field("trimtitlevalue", allBrandValue.toLowerCase(), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("shortcutName", shortcutName.trim(), Field.Store.YES, Field.Index.ANALYZED));
	                             
	                            final String logo1x = getMediaItemFromCustomMeta(customMeta, "logo1X", 0);
	                            doc.add(new Field("logo1X", logo1x, Field.Store.YES, Field.Index.ANALYZED));
	                            String logo2x = getMediaItemFromCustomMeta(customMeta, "logo2X", 0);
	                            doc.add(new Field("logo2X", logo2x, Field.Store.YES, Field.Index.ANALYZED));
	                            String imgAltText = getTextFromCustomMeta(customMeta, "caption");
	                            doc.add(new Field("imgAltText", imgAltText.trim(), Field.Store.YES, Field.Index.ANALYZED));
	
	                            doc.add(new Field("priority", priority.substring(0, 1), Field.Store.YES, Field.Index.ANALYZED));
	                            //dealers = "111|Alghanim~112|Babtain";
	                            doc.add(new Field("dealerName", (dealerString.length() > 0 ? dealerString : "N/A"), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("dealercitymap", (allCityString.length() > 0 ? allCityString : "N/A"), Field.Store.YES, Field.Index.ANALYZED));
	                            
	                            doc.add(new Field("votes", ((objHMIdVotes.get(brandCode) != null)?(String)objHMIdVotes.get(brandCode): "0"), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("commentscount", ((objHMIdComments.get(brandCode) != null)?(String)objHMIdComments.get(brandCode): "0"), Field.Store.YES, Field.Index.ANALYZED));
	                            //doc.add(new Field("nominationscount", ((objHMIdNominations.get(brandCode) != null)?(String)objHMIdNominations.get(brandCode): "0"), Field.Store.YES, Field.Index.ANALYZED));
	                            //doc.add(new Field("winscount", ((objHMIdWins.get(brandCode) != null)?(String)objHMIdWins.get(brandCode): "0"), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("nominationscount", String.valueOf(nominations), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("winscount", String.valueOf(sectorWins + fiveTimeWins + countryWins), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("trends", ((objHMIdTrends.get(brandCode) != null)?(String)objHMIdTrends.get(brandCode): "0"), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("starrating", ((objHMIdStarRating.get(brandCode) != null)?(String)objHMIdStarRating.get(brandCode): "-1"), Field.Store.YES, Field.Index.ANALYZED));
	                            doc.add(new Field("avgbrand", ((objHMIdAvgBrand.get(brandCode) != null)?(String)objHMIdAvgBrand.get(brandCode): "0"), Field.Store.YES, Field.Index.ANALYZED));
	
	                            doc.add(new Field("awards", (awardString.length() > 0 ? awardString : "N/A"), Field.Store.YES, Field.Index.ANALYZED));

	                            doc.add(new Field("relatedbrands", (relatedBrandsString.length() > 0 ? relatedBrandsString : "N/A"), Field.Store.YES, Field.Index.ANALYZED));

	                            writer.addDocument(doc);                            
	                        }
	                    }
	                }

	                writer.close();

	            } catch (BrokerQueryException be) {
	                LOG.error("error  " + be.getMessage(), be);
	                //throw new ContentProviderException(be);
	            }
            }
        } catch (Exception e) {
            LOG.error("exception  " + e.getMessage(), e);

        }
    }

    private static CountryRelatedBrandFields getCountryBrandFieldsFromCustomMeta(CustomMeta meta, int index, ComponentMetaFactory cmf) {
        CountryRelatedBrandFields countryFields = new CountryRelatedBrandFields();
        if(meta.getNameValues().get("title") != null)
            countryFields.setTitle((String) meta.getNameValues().get("title").getMultipleValues().get(index));
        if(meta.getNameValues().get("introduction") != null )
            countryFields.setIntroduction((String) meta.getNameValues().get("introduction").getMultipleValues().get(index));
        //if(meta.getNameValues().get("quota") != null )
        //	countryFields.setQuota(Float.parseFloat((String)meta.getNameValues().get("quota").getMultipleValues().get(index)));
        return countryFields;
    }

    private static CountryRelatedFields getCountryFieldsFromCustomMeta(CustomMeta meta, int index) {
        CountryRelatedFields countryFields = new CountryRelatedFields();
        if(meta.getNameValues().get("title") != null )
            countryFields.setTitle((String) meta.getNameValues().get("title").getMultipleValues().get(index));
        if(meta.getNameValues().get("introduction") != null )
            countryFields.setIntroduction((String) meta.getNameValues().get("introduction").getMultipleValues().get(index));
        //if(meta.getNameValues().get("quota") != null )
        //	countryFields.setQuota(Float.parseFloat((String)meta.getNameValues().get("quota").getMultipleValues().get(index)));
        return countryFields;
    }




    private static Map<String,String> getBrandListFromDB(HttpServletRequest request,String country){
        java.util.Map<String,String> objBrandMap=new HashMap<String,String>();
        String strQuery = "SELECT brandCode, userLanguage, count(id) as  cnt,  case when length(count(id)) = 1 then  '10000'||count(id)             when length(count(id)) = 2 then  '1000'||count(id)                when  length(count(id))= 3 then  '100'||count(id)                when length(count(id)) = 4 then  '10'||count(id)                 when length(count(id)) = 5 then  '1'||count(id)                else to_char(count(id) )    end as weight  FROM surveySubmission_"+country+"    GROUP BY brandCode, userLanguage   order by cnt desc";
        ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
        Connection connection = null;
        try {
        	connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(strQuery);
            ResultSet objRs = ps.executeQuery();
            while (objRs.next()) {
                objBrandMap.put( objRs.getString("brandCode"), objRs.getString("weight") );
            }
            objRs.close();
            ps.close();
        } catch(Exception e) {
            LOG.error(e.getMessage(),e);
        }finally {
			Utils.closeQuietly(connection);
        }
        return  objBrandMap;
    }

    private static int mapSchema(String schemaKey, Localization localization) {
        final String[] parts = schemaKey.split("\\.");
        final String configKey = parts.length > 1 ? (parts[0] + ".schemas." + parts[1]) : ("core.schemas." + parts[0]);
        final String schemaId = localization.getConfiguration(configKey);
        try {
            return Integer.parseInt(schemaId);
        } catch (NumberFormatException e) {
            LOG.warn("Error while parsing schema id: {}", schemaId, e);
            return 0;
        }
    }
   
    private static String getTextFromCustomMeta(CustomMeta meta, String fieldName) {
    	String values = "";
    	if(meta.getNameValues().get(fieldName) != null) {
    		try {
	    		for (int i = 0; i < meta.getNameValues().get(fieldName).getMultipleValues().size(); i++) {
	    			values += ((String) meta.getNameValues().get(fieldName).getMultipleValues().get(i)).toLowerCase() + ",";
	    		}
	    		values = values.substring(0, values.length() - 1);
    		} catch(Exception e) {
                LOG.error(e.getMessage(),e);
    			values = meta.getNameValues().containsKey(fieldName) ? meta.getFirstValue(fieldName).toString() : null;
    		}
    	}
    	return values;
    }
    
    /*private static String getTextFromCustomMeta(CustomMeta meta, String fieldName) {
    	return meta.getNameValues().containsKey(fieldName) ? meta.getFirstValue(fieldName).toString() : null;
    }*/

    
    /*private static String getTextFromCustomMeta(CustomMeta meta, String fieldName) {
    	String values = "";
    	if(meta.getNameValues().get(fieldName) != null) {
    		for (int i = 0; i < meta.getNameValues().get(fieldName).getMultipleValues().size(); i++) {
    			values += (String) meta.getNameValues().get(fieldName).getMultipleValues().get(i) + ",";
    		}
    	}
        return values;
    }*/



    private static String getMediaItemFromCustomMeta(CustomMeta meta, String fieldName, int index) {
        if (meta.getNameValues().containsKey(fieldName)) {
            BinaryMetaFactory binaryMetaFactory = new BinaryMetaFactory();
            BinaryMeta binaryMeta = binaryMetaFactory.getMeta( (String)meta.getNameValues().get(fieldName).getMultipleValues().get(index));
            return binaryMeta.getPath();
        }
        return null;
    }
    
    public static String stripAccents(String s) 
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

}
