package com.sdl.webapp.addon.controller;

import com.google.common.base.Strings;
import com.khayal.shero.BrandLuceneIndexer;
import com.khayal.shero.SHBrandUser;
import com.khayal.shero.SHSurveyUser;
import com.khayal.shero.UserActivityLog;
import com.khayal.shero.impl.BlogActionImpl;
import com.khayal.shero.services.SHSearchService;
import com.sdl.dxa.modules.generic.model.*;
import com.sdl.dxa.modules.generic.utilclasses.CustomBrokerQuery;
import com.sdl.dxa.modules.generic.utilclasses.TaxonomyComparator;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.ContentResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.entity.ContentList;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.controller.AbstractController;
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import com.sdl.webapp.tridion.query.BrokerQueryException;
import com.tridion.meta.*;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.Utils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.common.controller.RequestAttributeNames.ENTITY_MODEL;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.ENTITY_ACTION_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.LIST_ACTION_NAME;

@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + "Generic/Generic")
public class GenericController extends AbstractController {
    public static final int kw = 0;
    public static final int ae = 1;
    private static String firsttime = "no";

    private static final Logger LOG = LoggerFactory.getLogger(GenericController.class);

    private final WebRequestContext webRequestContext;
    private final ContentProvider contentProvider;
    private final ContentResolver contentResolver;
    private final LocalizationResolver localizationResolver;


    @Autowired
    public GenericController(WebRequestContext webRequestContext, ContentProvider contentProvider, ContentResolver contentResolver, LocalizationResolver localizationResolver) {
        this.webRequestContext = webRequestContext;
        this.contentProvider = contentProvider;
        this.contentResolver = contentResolver;
        this.localizationResolver = localizationResolver;
    }


    @RequestMapping(method = RequestMethod.GET, value = ENTITY_ACTION_NAME + "/{regionName}/{entityId}")
    public String handleGetEntity(HttpServletRequest request, @PathVariable Map<String, String> pathVariables) throws ContentProviderException {

        String regionName = pathVariables.get("regionName");
        String entityId = pathVariables.get("entityId");
        Localization localization = this.webRequestContext.getLocalization();
        String shUserLanguage = "", countryName = "";

        Entity entity = this.getEntityFromRequest(request, regionName, entityId);
        LOG.debug("from: GenericController.handleGetEntity" + request.getRequestURI() + "~" + regionName + "~" + entityId);
        request.getSession().setAttribute("socialshareUrl1", webRequestContext.getFullUrl());


        shUserLanguage = (String) request.getSession().getAttribute("userLanguage");
        if((webRequestContext.getFullUrl() + "/").indexOf("/ar/") > -1) {
            shUserLanguage = "ar";
        } else {
            shUserLanguage = "en";
        }
        if(shUserLanguage == null || shUserLanguage.length() == 0)
            shUserLanguage = "en";
        if(request.getSession().getAttribute("shortcutPage") == null)
            request.getSession().setAttribute("userLanguage", shUserLanguage);

        countryName = (String) request.getSession().getAttribute("userCountry");
        if(webRequestContext.getFullUrl().indexOf("/kw/") > -1)
            countryName = "kw";
        else if(webRequestContext.getFullUrl().indexOf("/ae/") > -1)
            countryName = "ae";
        if(countryName == null || countryName.length() == 0)
            countryName = "kw";
        if(request.getSession().getAttribute("shortcutPage") == null)
            request.getSession().setAttribute("userCountry", countryName);

		/*call lucene indexer*/
        if(entity.getId().equals("684")) { // this is brand lucene indexer
            try {
                request.getSession().setAttribute("socialshareUrl", webRequestContext.getFullUrl());
                BrandLuceneIndexer.populateBrandSearchIndexer(request, webRequestContext, "kw");
                BrandLuceneIndexer.populateBrandSearchIndexer(request, webRequestContext, "ae");
            } catch (Exception e) {
                LOG.error("brand lucene indexer "+e.getMessage(), e);
                throw new InternalServerErrorException("An unexpected error occurred", e);
            }

        }
        /* this is for old and new brand and category linking */
        if(entity.getId().equals("13645")) {
            try{
                LOG.error("called brand old new linking ");

                callFunctionToMapIds(request);

            }   catch (Exception ex){

            }
        }

        if(entity.getId().equals("8111")) { // this isScoreboard Page title  
            String otherUserCountry = "", userCountry = (String) request.getSession().getAttribute("userCountry");
            if(userCountry.equals("kw")) {
                otherUserCountry = "ae";
            } else if(userCountry.equals("ae")) {
                otherUserCountry = "kw";
            }
            String switchCountry = "0";
            HashMap<String,HashMap>  objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR, String.valueOf("" + request.getSession().getAttribute("surveySector")), "titlevalue", otherUserCountry);
            HashMap<String,HashMap> objHMSort = (HashMap)objHM.get("sorter");
            if(objHMSort.size() > 1) {
                switchCountry += "1";
            } else {
                switchCountry += "0";
            }
            request.getSession().setAttribute("switchingCountryPossible", switchCountry);
        }

        if(entity.getId().equals("509")) { // this is User Profile entity
            HTMLForm objHTMLForm = getHTMLFormSelectValues(request, entity, regionName);
            entity = objHTMLForm;
        }

        if(entity.getId().equals("24159")) { // this is Contact us entity
            HTMLForm objHTMLForm = getHTMLFormSelectValues(request, entity, regionName);
            entity = objHTMLForm;
        }

        if(entity.getId().equals("627")) { // this is User History entity
            UserHistory objHistory = getUserHistory(request, entity);
            entity = objHistory;

        }

        if(entity.getId().equals("662") || entity.getId().equals("29690")) { // this is Survey Brand List entity
            BrandSearchList objSurveyBrandList = getSurveyBrandList(request, entity);
            //SurveyBrandList objSurveyBrandList = getSurveyBrandList(request, entity);
            entity = objSurveyBrandList;
        }

        if(entity.getId().equals("8126")) { // this is Survey Brand List entity
            if(request.getSession().getAttribute("brandShortTitle") != null) {
                BrandSearchList objSurveyBrandList = getSimilarBrandList(request, entity, (String) request.getSession().getAttribute("brandShortTitle"));
                entity = objSurveyBrandList;
            }
        }

        if(entity.getId().equals("24405") || entity.getId().equals("24406") || entity.getId().equals("26331")) { // this is Sector Winners Brand List entity
            BrandSearchList objSurveyBrandList = null;
            if(entity.getId().equals("26331")) { // this is Sector Winners Brand List entity
                objSurveyBrandList = getWinnerBrandList(request, entity, "2015?030");
            }
            if(entity.getId().equals("24405")) { // this is Sector Winners Brand List entity
                objSurveyBrandList = getWinnerBrandList(request, entity, "2015?020");
            }
            if(entity.getId().equals("24406")) { // this is Country Winners Brand List entity
                objSurveyBrandList = getWinnerBrandList(request, entity, "2015?05?");
            }
            entity = objSurveyBrandList;
        }


        if(entity.getId().equals("663")) { // this is Survey Brand Last Visit entity
            SurveyLastVisit objSurveyLastVisit = getSurveyLastVisit(request, entity);
            entity = objSurveyLastVisit;
        }

        if(entity.getId().equals("664")) { // this is Survey Thank You Brand Detail entity
            SurveyThanksBrandDetail objSurveyThanksBrandDetail = getSurveyThanksBrandDetail(request, entity);
            entity = objSurveyThanksBrandDetail;
        }

        if(entity.getId().equals("665")) { // this is User Comments entity

            CommentList objCommentList = getCommentList(request, entity, regionName);
            entity = objCommentList;

        }

        if(entity.getId().equals("1124")) { // this is for brand search results entity
            BrandSearchList objBrandSrchList = getSurveyBrandList(request, entity);
            entity = objBrandSrchList;
        }

        if(entity.getId().equals("30168")) { // this is for graph
            BrandSentimentGraph objBrandSentimentChart = getBrandSentimenChart(request, entity);
            entity = objBrandSentimentChart;
        }
 
        /* code here to populate the comments for that blog id */
        if(entity.getId().equals("767")) {  // this is comment entity
            request.getSession().setAttribute("socialshareUrl", webRequestContext.getFullUrl());

            /* get the rating and put in session */
            String returnVal =  BlogActionImpl.getRating(request, request.getSession().getAttribute("blogEntityId").toString()) ;
            String[] parts = returnVal.split("~");
            request.getSession().setAttribute("avgRating", parts[0]);
            request.getSession().setAttribute("cntRating", parts[1]);
            List<BlogComments> comments = BlogActionImpl.getCommentListByBlogId( request, request.getSession().getAttribute("blogEntityId").toString());
            request.setAttribute("comments", comments );
        }

        //SHSurveyUser.activateUser(request, 1000001L);
        //ServiceHeroUserExtra.updateSHUserExtraValues(request, 1000001L);

        request.setAttribute("entity", entity);
        final MvcData mvcData = entity.getMvcData();
        return resolveView(mvcData, "Entity", request);
    }



    @RequestMapping(method = RequestMethod.GET, value = LIST_ACTION_NAME + "/{regionName}/{entityId}")
    public String handleGetList(HttpServletRequest request, @PathVariable Map<String, String> pathVariables) {
        String regionName = pathVariables.get("regionName");
        String entityId = pathVariables.get("entityId");
        
        LOG.trace("handleGetList: regionName={}, entityId={}", regionName, entityId);
        LOG.debug("GenericController: handleGetList: " + request.getRequestURI() + " regionName={}, entityId={}", regionName, entityId);

        final Entity entity = getEntityFromRequest(request, regionName, entityId);
        request.setAttribute(ENTITY_MODEL, entity);

        Localization localization = webRequestContext.getLocalization();
        LOG.trace(" localization {}", localization.getId());

        if (entity instanceof ContentList) {        	
            final ContentList contentList = (ContentList) entity;
            if (contentList.getItemListElements().isEmpty()) {
                // we only take the start from the query string if there is also an id parameter matching the model entity id
                // this means that we are sure that the paging is coming from the right entity (if there is more than one paged list on the page)
                if (contentList.getId().equals(request.getParameter("id"))) {
                    int start = getIntParameter(request, "start", 0);
                    contentList.setCurrentPage((start / contentList.getPageSize()) + 1);
                    contentList.setStart(start);
                }

                try {
                    contentProvider.populateDynamicList(contentList, webRequestContext.getLocalization());
                    //populateDynamicList(contentList, webRequestContext.getLocalization());
                } catch (ContentProviderException e) {
                    LOG.error("An unexpected error occurred", e);
                    throw new InternalServerErrorException("An unexpected error occurred", e);
                }
            }
        }

        if (entity instanceof CustomContentList) {        	
        	//final CustomContentList contentList1 = new CustomContentList();
        	//List<QueryFilter> queryFilters = new ArrayList<>();
        	//queryFilters.addAll("","");
        	//contentList1.setQueryFilters();
            final CustomContentList contentList = (CustomContentList) entity;
            if (contentList.getItemListElements().isEmpty()) {            	
                // we only take the start from the query string if there is also an id parameter matching the model entity id
                // this means that we are sure that the paging is coming from the right entity (if there is more than one paged list on the page)
                if (contentList.getId().equals(request.getParameter("id"))) {                	
                    int start = getIntParameter(request, "start", 0);                    
                    contentList.setCurrentPage((start / contentList.getPageSize()) + 1);                    
                    contentList.setStart(start);
                }

                try {
                    //contentProvider.populateDynamicList(contentList, webRequestContext.getLocalization());
                    populateDynamicList(request, contentList);
                } catch (ContentProviderException e) {
                    LOG.error("An unexpected error occurred", e);
                    throw new InternalServerErrorException("An unexpected error occurred", e);
                }
            }
        }



        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return resolveView(mvcData, "Entity", request);
        //return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }

    private int getIntParameter(HttpServletRequest request, String parameterName, int defaultValue) {
        final String parameter = request.getParameter(parameterName);
        return !Strings.isNullOrEmpty(parameter) ? Integer.parseInt(parameter) : defaultValue;
    }


    public void populateDynamicList(HttpServletRequest request, CustomContentList contentList) throws ContentProviderException {
        final CustomBrokerQuery brokerQuery = new CustomBrokerQuery();
        brokerQuery.setStart(contentList.getStart());
        brokerQuery.setPublicationId(Integer.parseInt(webRequestContext.getLocalization().getId()));
        brokerQuery.setPageSize(contentList.getPageSize());
        brokerQuery.setSchemaId(mapSchema(contentList.getContentType().getKey(), webRequestContext.getLocalization()));
        brokerQuery.setSort(contentList.getSort().getKey());
        if(contentList.getQueryFilters() != null)
            brokerQuery.setQueryFilters(contentList.getQueryFilters());
        //final List<QueryFilter> queryFilters1 = contentList.getQueryFilters();               
        /*Multimap<String, String> keywordFilters = ArrayListMultimap.create();
        //keywordFilters.put("tcm:3-86-512", "tcm:3-346-1024");
        keywordFilters.put("active", "Yes");         */

        List<String> entityIds;
        Entity entity;
        List<CustomTeaser> customTeaserList = new ArrayList<>();
        try {
            final ComponentMetaFactory cmf;
            if(((String) request.getSession().getAttribute("socialshareUrl1") + "/").indexOf("/ar/") > -1)
                cmf = new ComponentMetaFactory(10);
            else
                cmf = new ComponentMetaFactory(9);
            entityIds = brokerQuery.executeQuery();
            CustomTeaser objTeaser = null;
            for (String entityId : entityIds) {
                final ComponentMeta compMeta = cmf.getMeta(entityId);
                if (compMeta != null) {
                    final CustomMeta customMeta = compMeta.getCustomMeta();
                    String countryName = (String)request.getSession().getAttribute("userCountry");
                    int index = 0;
                    if(countryName == null) {
                        index = kw;
                    } else {
                        if(countryName.equals("kw"))
                            index = kw;
                        else if(countryName.equals("ae"))
                            index = ae;
                    }

                    if(customMeta.getNameValues().get("countryList") != null) {
                        for(int i = 0; i < customMeta.getNameValues().get("countryList").getMultipleValues().size(); i++) {
                            if(countryName.equals("kw") && ((String) customMeta.getNameValues().get("countryList").getMultipleValues().get(i)).equals("Kuwait")) {
                                objTeaser = getCustomTeaserFromMeta(request, compMeta, i);
                                if(objTeaser != null)
                                    customTeaserList.add(objTeaser);
                            } else if(countryName.equals("ae") && ((String) customMeta.getNameValues().get("countryList").getMultipleValues().get(i)).equals("UAE")) {
                                objTeaser = getCustomTeaserFromMeta(request, compMeta, i);
                                if(objTeaser != null)
                                    customTeaserList.add(objTeaser);
                            }
                        }
                    } else {
                        objTeaser = getCustomTeaserFromMeta(request, compMeta, 0);
                        if(objTeaser != null)
                            customTeaserList.add(objTeaser);
                    }
                }
            }
        } catch (BrokerQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new ContentProviderException(e);
        }
        contentList.setItemListElements(customTeaserList);

        // Resolve links
        for (CustomTeaser item : customTeaserList) {

            item.getLink().setUrl(contentResolver.resolveLink(item.getLink().getUrl(), null));
        }

        contentList.setHasMore(brokerQuery.isHasMore());
    }

    private int mapSchema(String schemaKey, Localization localization) {
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

    private CustomTeaser getCustomTeaserFromMeta(HttpServletRequest request, ComponentMeta compMeta, int index) {
        CustomTeaser result = null;
        /*String countryName = (String)request.getSession().getAttribute("userCountry");
    	int index = 0;
    	if(countryName == null) {
    		index = kw;
    	} else { 
	    	if(countryName.equals("kw"))
	    		index = kw;
	    	else if(countryName.equals("ae"))
	    		index = ae;
    	}*/

        final CustomMeta customMeta = compMeta.getCustomMeta();
        String active = "";
        //LOG.error("test:" + getTextFromCustomMeta(customMeta, "active"));
        try {
            active = getTextFromCustomMeta(customMeta, "active");
        } catch(Exception re) {
            active = "Yes";
        }
        if(active.indexOf("Yes") > -1) {
            result = new CustomTeaser();

            Link link = new Link();
            link.setUrl("tcm:" + compMeta.getPublicationId() + "-" + compMeta.getId());
            result.setLink(link);

            result.setTcmId("tcm:" + compMeta.getPublicationId() + "-" + compMeta.getId());

            DateTime date = getDateFromCustomMeta(customMeta, "dateCreated");
            result.setDate(date != null ? date : new DateTime(compMeta.getLastPublicationDate()));

            String headline = getTextFromCustomMeta(customMeta, "name");
            result.setTitle(headline != null ? headline : compMeta.getTitle());

            String description = "";
            if(getTextFromCustomMeta(customMeta, "shortName") != null)
                description = getTextFromCustomMeta(customMeta, "shortName");
            if(getTextFromCustomMeta(customMeta, "introText") != null)
                description = getTextFromCustomMeta(customMeta, "introText");
            result.setDescription(description);

            String author = getTextFromCustomMeta(customMeta, "author");
            result.setAuthor(author);

            String caption =  getTextFromCustomMeta(customMeta, "caption");

            result.setCaption(caption);

            String svgImage = getMediaItemFromCustomMeta(customMeta, "svgImage", index);
            result.setSvgImage(svgImage);

            String normalImage = "";
            if (customMeta.getNameValues().containsKey("thumbnail"))
                normalImage = getMediaItemFromCustomMeta(customMeta, "thumbnail", index);
            if (customMeta.getNameValues().containsKey("normalImage"))
                normalImage = getMediaItemFromCustomMeta(customMeta, "normalImage", index);
            if (customMeta.getNameValues().containsKey("logo1X"))
                normalImage = getMediaItemFromCustomMeta(customMeta, "logo1X", index);
            result.setNormalImage(normalImage);

            final CountryRelatedFields countryFields;
            if(customMeta.getNameValues().get("quota") != null) {
                countryFields = getCountryFieldsFromCustomMeta(customMeta, index);
                result.setCountryFields(countryFields);
            }
        }
        return result;
    }

    private String getTextFromCustomMeta(CustomMeta meta, String fieldName) {
        return meta.getNameValues().containsKey(fieldName) ? meta.getFirstValue(fieldName).toString() : null;
    }

    private CountryRelatedFields getCountryFieldsFromCustomMeta(CustomMeta meta, int index) {
        CountryRelatedFields countryFields = new CountryRelatedFields();
        if(meta.getNameValues().get("title") != null )
            countryFields.setTitle((String)meta.getNameValues().get("title").getMultipleValues().get(index));
        if(meta.getNameValues().get("introduction") != null )
            countryFields.setIntroduction((String)meta.getNameValues().get("introduction").getMultipleValues().get(index));
        //if(meta.getNameValues().get("quota") != null )        	
        //	countryFields.setQuota(Float.parseFloat((String)meta.getNameValues().get("quota").getMultipleValues().get(index)));
        return countryFields;
    }

    private DateTime getDateFromCustomMeta(CustomMeta meta, String fieldName) {
        if (meta.getNameValues().containsKey(fieldName)) {
            Object firstValue = meta.getFirstValue(fieldName);
            if (!firstValue.equals("")) {
                return new DateTime(firstValue);
            }
        }
        return null;
    }

    private String getMediaItemFromCustomMeta(CustomMeta meta, String fieldName, int index) {
        if (meta.getNameValues().containsKey(fieldName)) {
            BinaryMetaFactory binaryMetaFactory = new BinaryMetaFactory();
            BinaryMeta binaryMeta = binaryMetaFactory.getMeta( (String)meta.getNameValues().get(fieldName).getMultipleValues().get(index));
            return binaryMeta.getPath();
        }
        return null;
    }

    private HTMLForm getHTMLFormSelectValues(HttpServletRequest request, Entity objEntity, String regionName) {
        HTMLForm objLocalEntity = (HTMLForm) objEntity;
        String showSelGovernorate = "false";
        int pubId = 8;
        if(((String) request.getSession().getAttribute("socialshareUrl") + "/").indexOf("/ar/") > -1)
            pubId = 10;
        else
            pubId = 9;

        TaxonomyComparator objComp = new TaxonomyComparator();
        /* check if survey User is there and see if user has country of residence filled if so then populate governorate with selected option */
        SHSurveyUser shSurveyUser = (SHSurveyUser)request.getSession().getAttribute("shSurveyUser");
        if( shSurveyUser != null && (shSurveyUser.getResidence() != null  && ( shSurveyUser.getResidence().equals("35367") || shSurveyUser.getResidence().equals("35368"))) )
        {
            showSelGovernorate = "true";
        }

        String key;
        Keyword objKey;
        KeyValuePair keyValuePair;
        List<KeyValuePair> KeyValuePairList;
        TaxonomyFactory objTaxonomy = new TaxonomyFactory();
        List<HTMLFormElement> objElements =  objLocalEntity.getFormElement();

        for (HTMLFormElement element: objElements) {
            if(element.getDropBoxCategoryId() != 0) {
                List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-" + element.getDropBoxCategoryId() + "-512").getKeywordChildren();
                KeyValuePairList = new ArrayList<KeyValuePair>();
                Collections.sort(objKeyList, objComp);
                Iterator iterator = objKeyList.listIterator();
                while(iterator.hasNext()) {
                    objKey = (Keyword) iterator.next();

                   if(element.getDropBoxCategoryId() == 160 && showSelGovernorate.equals("true")) {

                        // check the userCountry
                        String countryCode = "";
                        if ( shSurveyUser.getResidence().equals("35367"))
                            countryCode = "kw";
                        else if (shSurveyUser.getResidence().equals("35368"))
                            countryCode = "ae";
                        if (objKey.getKeywordName().indexOf(countryCode) > -1) {
                            keyValuePair = new KeyValuePair();
                            key = objKey.getKeywordKey();
                            List<Keyword> objChildKeyList = objKey.getKeywordChildren();
                            KeyValuePairList = new ArrayList<KeyValuePair>();
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

                            if (element.getDropBoxCategoryId() == 153)
                                keyValuePair.setKey(objKey.getKeywordKey() + "_" + key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")));
                            else
                                keyValuePair.setKey(key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")) + "_" + objKey.getKeywordKey());
                            keyValuePair.setValue(objKey.getKeywordDescription());
                            KeyValuePairList.add(keyValuePair);
                        }
                   }
                }
                element.setOptionValue(KeyValuePairList);
            }
        }
        return objLocalEntity;
    }


    private UserHistory getUserHistory(HttpServletRequest request, Entity objEntity) {
        UserHistory objLocalEntity = (UserHistory) objEntity;

        SHSurveyUser shSurveyUser = (SHSurveyUser)request.getSession().getAttribute("shSurveyUser");
        String countryCode =  (String)request.getSession().getAttribute("userCountry");
        String userLanguage =  (String)request.getSession().getAttribute("userLanguage");
        objLocalEntity.setNumberOfVotes(Integer.parseInt(String.valueOf(shSurveyUser.getNumberOfVotes())));
        objLocalEntity.setVoteAverage(shSurveyUser.getVotingAverage());
        List<VoteHistory> voteHistory = new ArrayList<VoteHistory>();
        long lastVoteDate = 0;
        VoteHistory objVote;
        String brandIds_kw = "";
        String brandIds_ae = "";

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
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
        }finally {
            Utils.closeQuietly(connection);
        }
        objLocalEntity.setLastVoteDays((int)((System.currentTimeMillis() - lastVoteDate) / 86400000));
        objLocalEntity.setVoteHistory(voteHistory);

        return objLocalEntity;
    }

    private BrandSearchList getSurveyBrandList(HttpServletRequest request, Entity objEntity) {
    	/* this method is used by serachbrand as well
    	   If this contains srchText then this is coming from brand search
    	 */
        BrandSearchList objLocalEntity = (BrandSearchList) objEntity;
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
            HashMap<String,String> objHMIdDealersCity  = new HashMap<String,String>();
            HashMap<String,String> objHMIdRelatedBrands  = new HashMap<String,String>();
           


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
            HashMap<String,String> objHMIdDealersCityTemp  = new HashMap<String,String>();
            HashMap<String,String> objHMIdRelatedBrandsTemp  = new HashMap<String,String>();
            
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

            List<BrandSearch> brandSrchList = new ArrayList<BrandSearch>();

            if(strSrchText != "0") {
                request.getSession().setAttribute("searchBrand", request.getParameter("srchTextFull"));
                brandSrchList = SHSearchService.searchBrand(request,  request.getParameter("srchText"), "0");
                objLocalEntity.setBrandDtl(brandSrchList);
                //request.getSession().setAttribute("surveySector", sectorCode);
            } else {
                if (!sectorCode.equals("0") || objEntity.getId().equals("29690")) {
                	StringTokenizer objSt1;
                    String dealerName;
                           LOG.debug(" brand selection "+request.getSession().getAttribute("shBrandUser"));
                    TreeMap<String,String> objTMAlphaSort = new TreeMap<String,String>();
                    TreeMap<String,String> objTMTop18Sort = new TreeMap<String,String>();

                    if(objEntity.getId().equals("29690")) {
                        SHBrandUser shBrandUser = (SHBrandUser)request.getSession().getAttribute("shBrandUser");
                        String assignedBrands = shBrandUser.getAssignedBrands();
                        LOG.debug(" entered brand selection "+assignedBrands);
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
    	                    objHMIdDealersCityTemp = (HashMap) objHM.get("dealersCity");
    	                    objHMIdRelatedBrandsTemp = (HashMap) objHM.get("relatedBrands");

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
                            objHMIdDealersCity.putAll(objHMIdDealersCityTemp);
                            objHMIdRelatedBrands.putAll(objHMIdRelatedBrandsTemp);
    	                    objHMIdRelatedBrandsTemp = (HashMap) objHM.get("relatedBrands");

                            
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
    	                    objHMIdDealersCityTemp = (HashMap) objHM.get("dealersCity");
    	                    objHMIdRelatedBrandsTemp = (HashMap) objHM.get("relatedBrands");

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
                            objHMIdDealersCity.putAll(objHMIdDealersCityTemp);
    	                    objHMIdRelatedBrands.putAll(objHMIdRelatedBrandsTemp);
 
                            objHMIdComments.putAll(objHMIdCommentsTemp);
                            objHMIdNominations.putAll(objHMIdNominationsTemp);
                            objHMIdWins.putAll(objHMIdWinsTemp);
                            objHMIdTrends.putAll(objHMIdTrendsTemp);
                            objHMIdStarRating.putAll(objHMIdStarRatingTemp);
                        }                        
                    	
                    } else {
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
	                    objHMIdDealersCity = (HashMap) objHM.get("dealersCity");
	                    objHMIdRelatedBrands = (HashMap) objHM.get("relatedBrands");
	                    
	                    objHMIdComments = (HashMap)objHM.get("commentsCount");
	                    objHMIdNominations = (HashMap)objHM.get("nominationsCount");
	                    objHMIdWins = (HashMap)objHM.get("winsCount");
	                    objHMIdTrends = (HashMap)objHM.get("trends");
	                    objHMIdStarRating = (HashMap)objHM.get("starRating");

                    }

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
                        if(objHMIdCountry.get(strTempValue).equals(userCountry) || objEntity.getId().equals("29690")) {
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
                                objBrandList.setDealerCityName(objHMIdDealersCity.get(strTempValue));
                                objBrandList.setRelatedBrands(objHMIdRelatedBrands.get(strTempValue));

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

                    objLocalEntity.setBrandDtl(brandSrchList);
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

        return objLocalEntity;
    }

    private SurveyLastVisit getSurveyLastVisit(HttpServletRequest request, Entity objEntity) {
        SurveyLastVisit objLocalEntity = (SurveyLastVisit) objEntity;
        try {
            String brandCode, strTemp, strTempValue = "", key, keyValue;
            HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
            HashMap<String,String> objHMSort = new HashMap<String,String>();
            HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdDelears = new HashMap<String,String>();
            HashMap<String,String> objHMIdDelearsCity = new HashMap<String,String>();

            int pubId = 8;
            if(((String) request.getSession().getAttribute("socialshareUrl1") + "/").indexOf("/ar/") > -1)
                pubId = 10;
            else
                pubId = 9;

            brandCode = request.getParameter("brandCode");
            if(brandCode == null && brandCode.length() == 0)
                brandCode = "0";

            if(!brandCode.equals("0")) {
                objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandCode);

                objLocalEntity.setBrandCode(Integer.parseInt(brandCode));
                objHMSort = (HashMap)objHM.get("sorter");
                objHMIdTitle = (HashMap)objHM.get("title");
                objHMIdDelears = (HashMap)objHM.get("dealers");
                objHMIdDelearsCity = (HashMap)objHM.get("dealersCity");
                List<KeyValuePair> KeyValuePairList = new ArrayList<KeyValuePair>();
                KeyValuePair keyValuePair;
                String dealers = "N/A";

                List<String> titles = new ArrayList<String>(objHMSort.keySet());
                Collections.sort(titles);
                for (String title: titles) {
                    strTempValue = objHMSort.get(title);
                    //strTempValue = strTempValue.substring(strTempValue.indexOf("-") + 1, strTempValue.lastIndexOf("-"));
                    request.getSession().setAttribute("surveyBrand", brandCode);
                    request.getSession().setAttribute("surveyBrandName", (String) objHMIdTitle.get(strTempValue));
                    dealers = (String) objHMIdDelears.get(strTempValue);
                    if(dealers == null || dealers.length() == 0)
                        dealers = "N/A";
                }
                if(!dealers.equals("N/A")) {
                    StringTokenizer objSt, objSt1;
                    objSt = new StringTokenizer(dealers, "^");
                    while(objSt.hasMoreTokens()) {
                        strTemp = objSt.nextToken();
                        objSt1 = new StringTokenizer(strTemp, "|");
                        keyValuePair = new KeyValuePair();
                        keyValuePair.setKey(objSt1.nextToken());
                        keyValuePair.setValue(objSt1.nextToken());
                        if(!objSt1.nextToken().equals("null"))
                            KeyValuePairList.add(keyValuePair);
                    }
                }
                
                if(objHMIdDelearsCity.get(strTempValue) != null && !((String)objHMIdDelearsCity.get(strTempValue)).equals("N/A"))
                	request.getSession().setAttribute("surveyDealersCity", objHMIdDelearsCity.get(strTempValue));
                else 
                	request.getSession().removeAttribute("surveyDealersCity");
                
                objLocalEntity.setDealer(KeyValuePairList);

                KeyValuePairList = new ArrayList<KeyValuePair>();
                TaxonomyFactory objTaxonomy = new TaxonomyFactory();
                List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-91-512").getKeywordChildren();
                Collections.sort(objKeyList);
                Iterator iterator = objKeyList.listIterator();
                Keyword objKey;
                while(iterator.hasNext()) {
                    objKey = (Keyword) iterator.next();
                    keyValuePair = new KeyValuePair();
                    key = objKey.getKeywordURI();
                    keyValuePair.setKey( key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")));
                    keyValuePair.setValue(objKey.getKeywordDescription());
                    KeyValuePairList.add(keyValuePair);
                }
                objLocalEntity.setVisit(KeyValuePairList);
            } else {
                //Wrong sector info - No Brands
            }
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return objLocalEntity;
    }

    private SurveyThanksBrandDetail getSurveyThanksBrandDetail(HttpServletRequest request, Entity objEntity) {
        SurveyThanksBrandDetail objLocalEntity = (SurveyThanksBrandDetail) objEntity;
        double voteAverage = 0;
        String strTemp = "", brandCode, strTempValue = "", key, keyValue;

        brandCode =  "" + request.getSession().getAttribute("surveyBrand");
        if(brandCode == null || brandCode.length() == 0) {
            brandCode = "0";
        }
        strTemp = (String) request.getSession().getAttribute("surveyVoteAverage");
        if(!(strTemp == null || strTemp.length() == 0)) {
            voteAverage = Double.parseDouble(strTemp);
        }

        request.getSession().setAttribute("votingDetailsVar", "true");
        HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
        HashMap<String,String> objHMSort = new HashMap<String,String>();
        HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
        HashMap<String,String> objHMIdLogo1X = new HashMap<String,String>();
        HashMap<String,String> objHMIdLogo2X = new HashMap<String,String>();
        HashMap<String,String> objHMIdStarRating = new HashMap<String,String>();
        HashMap<String,String> objHMIdDealers = new HashMap<String,String>();

        objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, brandCode);

        objLocalEntity.setBrandCode(Integer.parseInt(brandCode));
        objHMSort = (HashMap)objHM.get("sorter");
        objHMIdTitle = (HashMap)objHM.get("title");
        objHMIdLogo1X = (HashMap)objHM.get("logo1X");
        objHMIdLogo2X = (HashMap)objHM.get("logo2X");
        objHMIdStarRating = (HashMap)objHM.get("starRating");
        objHMIdDealers = (HashMap)objHM.get("dealers");

        List<String> titles = new ArrayList<String>(objHMSort.keySet());
        Collections.sort(titles);
        for (String title: titles) {
            strTempValue = objHMSort.get(title);
            //strTempValue = strTempValue.substring(strTempValue.indexOf("-") + 1, strTempValue.lastIndexOf("-"));
            objLocalEntity.setBrandCode(Integer.parseInt(brandCode));
            objLocalEntity.setBrandName(objHMIdTitle.get(strTempValue));
            request.getSession().setAttribute("surveyBrandName", (String) objHMIdTitle.get(strTempValue));
            objLocalEntity.setBrandImage(objHMIdLogo1X.get(strTempValue));
            objLocalEntity.setBrandImage2x(objHMIdLogo2X.get(strTempValue));
            objLocalEntity.setVoteAverage(voteAverage);
            objLocalEntity.setStarRating(objHMIdStarRating.get(strTempValue));
            objLocalEntity.setDealerName(objHMIdDealers.get(strTempValue));
        }
        return objLocalEntity;
    }

    private CommentList getCommentList(HttpServletRequest request, Entity objEntity, String regionName) {
        CommentList objLocalEntity = (CommentList) objEntity;
        long lBrandUserId = 0;
        String childCommentCount = "";
        ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
        List<UserComment> commentList = new ArrayList<UserComment>();
        String strQuery = "", strTemp = "", sBrandUserCountry ="", queryChildComments= "true";
        UserComment userComment = null;
        Connection connection = null;

        try {
            String sort, sortParam = "", filterbyNone, filterbyRating, ratingFilterParam="", filterbyResponse, responseFilterParam = "", filterbyStatus, statusFilterParam = "", filterbySentiment, sentimentFilterParam="", commentsPerPage="";
            int pageNo, cmtPerPage= 10;
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
            if(((String) request.getSession().getAttribute("socialshareUrl1") + "/").indexOf("/ar/") > -1)
                pubId = 10;
            else
                pubId = 9;


            if(regionName.equals("BrandDashboard") || regionName.equals("UserDetail")) {
                //first populate the dropdowns
                // star rating
                String key;
                Keyword objKey;
                KeyValuePair keyValuePair;
                List<KeyValuePair> KeyValuePairList;
                TaxonomyComparator objComp = new TaxonomyComparator();
                TaxonomyFactory objTaxonomy = new TaxonomyFactory();
                List<Keyword> objKeyListStarRating = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-6403-512").getKeywordChildren();
                KeyValuePairList = new ArrayList<KeyValuePair>();
                Collections.sort(objKeyListStarRating, objComp);
                Iterator iterator = objKeyListStarRating.listIterator();
                while(iterator.hasNext()) {
                    objKey = (Keyword) iterator.next();
                    if(objKey.getKeywordName().indexOf("00") == -1) {
                        keyValuePair = new KeyValuePair();
                        keyValuePair.setKey(objKey.getKeywordKey());
                        keyValuePair.setValue(objKey.getKeywordDescription());
                        KeyValuePairList.add(keyValuePair);
                    }
                }
                objLocalEntity.setObjRating(KeyValuePairList);

                List<Keyword> objKeyListResponse = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-6402-512").getKeywordChildren();
                KeyValuePairList = new ArrayList<KeyValuePair>();
                Collections.sort(objKeyListResponse, objComp);
                Iterator iteratorResponse = objKeyListResponse.listIterator();
                while(iteratorResponse.hasNext()) {
                    objKey = (Keyword) iteratorResponse.next();
                    objKey.getKeywordChildren();
                    if(objKey.getKeywordName().indexOf("00") == -1) {
                        key = objKey.getKeywordURI();
                        keyValuePair = new KeyValuePair();
                        keyValuePair.setKey(key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")));
                        keyValuePair.setValue(objKey.getKeywordDescription());
                        KeyValuePairList.add(keyValuePair);
                    }
                }
                objLocalEntity.setObjResponse(KeyValuePairList);

                List<Keyword> objKeyListStatus = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-6401-512").getKeywordChildren();
                KeyValuePairList = new ArrayList<KeyValuePair>();
                Collections.sort(objKeyListResponse, objComp);
                Iterator iteratorStatus = objKeyListStatus.listIterator();
                while(iteratorStatus.hasNext()) {
                    objKey = (Keyword) iteratorStatus.next();
                    if(objKey.getKeywordName().indexOf("00") == -1) {
                        key = objKey.getKeywordURI();
                        keyValuePair = new KeyValuePair();
                        keyValuePair.setKey(key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")));
                        keyValuePair.setValue(objKey.getKeywordDescription());
                        KeyValuePairList.add(keyValuePair);
                    }
                }
                objLocalEntity.setObjStatus(KeyValuePairList);

                List<Keyword> objKeyListSentiment = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-156-512").getKeywordChildren();
                KeyValuePairList = new ArrayList<KeyValuePair>();
                Collections.sort(objKeyListSentiment, objComp);
                Iterator iteratorSentiment = objKeyListSentiment.listIterator();
                String keyName = "";
                while(iteratorSentiment.hasNext()) {
                    objKey = (Keyword) iteratorSentiment.next();
                    if(objKey.getKeywordName().indexOf("00") == -1) {
                        key = objKey.getKeywordURI();
                        keyName =  objKey.getKeywordName();
                        keyValuePair = new KeyValuePair();
                        keyValuePair.setKey(objKey.getKeywordKey());
                        keyValuePair.setValue(keyName.substring(keyName.indexOf(" - ") + 2));
                        KeyValuePairList.add(keyValuePair);
                    }
                }
                objLocalEntity.setObjSentiment(KeyValuePairList);

                List<Keyword> objKeyListCommentsPerPage = objTaxonomy.getTaxonomyKeywords("tcm:" + pubId + "-6652-512").getKeywordChildren();
                KeyValuePairList = new ArrayList<KeyValuePair>();
                Collections.sort(objKeyListCommentsPerPage, objComp);
                Iterator iteratorPerPage = objKeyListCommentsPerPage.listIterator();
                while(iteratorPerPage.hasNext()) {
                    objKey = (Keyword) iteratorPerPage.next();
                    if(objKey.getKeywordName().indexOf("00") == -1) {
                        key = objKey.getKeywordURI();
                        keyName =  objKey.getKeywordName();
                        keyValuePair = new KeyValuePair();
                        keyValuePair.setKey(keyName.substring(keyName.indexOf("- ") + 2));
                        keyValuePair.setValue(objKey.getKeywordDescription());
                        KeyValuePairList.add(keyValuePair);
                    }
                }
                objLocalEntity.setObjCommentsPerPage(KeyValuePairList);

                commentsPerPage = request.getParameter("count");
                if(commentsPerPage == null || commentsPerPage.length() == 0) {
                    cmtPerPage = 10;

                } else {
                    try	{
                        cmtPerPage = Integer.parseInt(commentsPerPage);
                    } catch(Exception ex) {
                        cmtPerPage = 10;
                    }
                }


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
                LOG.debug(" filterbyNone " + filterbyNone);
                LOG.debug(" filterbyRating "+request.getParameter("filterbyRating"));
                LOG.debug(" filterbyResponse "+request.getParameter("filterbyResponse"));
                LOG.debug(" filterbySentiment "+request.getParameter("filterbySentiment"));
                LOG.debug(" filterbyStatus "+request.getParameter("filterbyStatus"));

                //if (filterbyNone == null || filterbyNone.length() == 0) {
                if(request.getParameterNames().toString().length() > 0){
                    filterbyRating = request.getParameter("filterbyRating");
                    if (filterbyRating == null || filterbyRating.length() == 0 || filterbyRating.equals("none")) {
                        if(filterbyRating != null && filterbyRating.equals("none")) {
                            LOG.debug("filterbyRating then is  "+filterbyRating);
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

                    if (filterbyResponse != null && filterbyResponse.toString().equals("26517") ) {
                        responseFilterParam = " and helpful like 'Y'";
                    }else if (filterbyResponse != null && filterbyResponse.toString().equals("26518") ) {
                        responseFilterParam = " and nothelpful like 'Y'";
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
                            sentimentFilterParam = " and SENTIMENT_TWO like 'Y' ";
                        else if (filterbySentiment.equals("1"))
                            sentimentFilterParam = " and SENTIMENT_ONE like 'Y' ";
                        else if (filterbySentiment.equals("0"))
                            sentimentFilterParam = " and SENTIMENT_ZERO like 'Y' ";
                        else if (filterbySentiment.equals("-1"))
                            sentimentFilterParam = " and SENTIMENT_MINUSONE like 'Y' ";
                        else if (filterbySentiment.equals("-2"))
                            sentimentFilterParam = " and SENTIMENT_MINUSTWO like 'Y' ";
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
                    // status-private  - 29661
                    */
                    if (filterbyStatus != null && filterbyStatus.length() > 0) {
                        if (filterbyStatus.equals("26512")) {
                            // activeFilters++;
                            //request.getSession().setAttribute("paginationFilterByStatus", filterbyStatus);
                            statusFilterParam = " and NEW_IND like 'Y' ";
                        }else if (filterbyStatus.equals("26513")){
                            statusFilterParam = " and FLAGGED like 'Y' ";
                        }else if (filterbyStatus.equals("26514")){
                            statusFilterParam = " and REPLIED like 'Y' ";
                        }else if (filterbyStatus.equals("26515")){
                            statusFilterParam = " and OKAY like 'Y' ";
                        }else if (filterbyStatus.equals("29661")){
                            statusFilterParam = " and PRIVATE_IND like 'Y' ";
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
                        LOG.debug(" regionname is BrandDashboard and brand param is  "+strTemp );
                        if (strTemp == null || strTemp.length() == 0 || strTemp.indexOf("~") == -1){
                            brandCode = 0;
                        }   else {
                            String[] strBrand = strTemp.split("~");
                            //StringTokenizer strBrand = new java.util.StringTokenizer(strTemp, "~");
                            if (strBrand.length == 4) {
                                sBrandUserCountry = (strBrand[0]);
                                sectorcode = Integer.parseInt(strBrand[1]);
                                brandCode = Integer.parseInt(strBrand[2]);
                                String strTempDealer = strBrand[3];
                                if (strTempDealer == null || strTempDealer.length() == 0 || strTempDealer.equals("N/A"))
                                    lBrandUserDealer = 0;
                                else
                                    lBrandUserDealer = Integer.parseInt(strTempDealer);
                            }else{
                                brandCode = 0;
                            }
                        }

                    }else{
                        strTemp = String.valueOf(request.getSession().getAttribute("surveyBrand"));
                        if(strTemp == null || strTemp.length() == 0)
                            brandCode = 0;
                        else
                            brandCode = Integer.parseInt(strTemp);
                    }

                }
                if(regionName.equals("BrandDashboard")) {
                    //request.getSession().setAttribute("dashboardBrand", String.valueOf(brandCode));
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
                    objLocalEntity.setItemCode(brandCode);
                    objLocalEntity.setItemName(objHMIdTitle.get(strTempValue));
                    objLocalEntity.setItemImage(objHMIdLogo1X.get(strTempValue));
                    objLocalEntity.setItemImage2x(objHMIdLogo2X.get(strTempValue));
                    objLocalEntity.setParentSectorName(objHMIdParentSectorTitle.get(strTempValue));
                    objLocalEntity.setSectorCode(Integer.parseInt(objHMIdSectorId.get(strTempValue)));
                    request.getSession().setAttribute("surveySector", String.valueOf(Long.parseLong(objHMIdSectorId.get(strTempValue))));
                    objLocalEntity.setSectorName(objHMIdSectorTitle.get(strTempValue));
                    objLocalEntity.setDealerName(objHMIdDealers.get(strTempValue));

                    objLocalEntity.setComments(Integer.parseInt(objHMIdComments.get(strTempValue)));
                    objLocalEntity.setNominations(Integer.parseInt(objHMIdNominations.get(strTempValue)));
                    objLocalEntity.setWins(Integer.parseInt(objHMIdWins.get(strTempValue)));
                    objLocalEntity.setTrends(Integer.parseInt(objHMIdTrends.get(strTempValue)));
                    objLocalEntity.setStarRating(objHMIdStarRating.get(strTempValue));
                    objLocalEntity.setAwards(objHMIdAwards.get(strTempValue));

                    /* code here to insert count of comments : call function */
                   if(regionName.equals("BrandDashboard")) {

                       // set the last refreshtim
                       request.getSession().setAttribute("lastRefreshTime", System.currentTimeMillis())    ;

                       SHBrandUser shBrandUser = (SHBrandUser)request.getSession().getAttribute("shBrandUser");

                       lBrandUserId =  shBrandUser.getShUserId();



                       String sReturnStatus  =  "";
                       LOG.debug("Calling sh_brand_dashboard countrycode ="+sBrandUserCountry +", categoryCode=" + objLocalEntity.getSectorCode() + ", brandCode=" + objLocalEntity.getItemCode() + ", dealerCode=" + lBrandUserDealer );

                       LOG.debug(" Brand User is  "+lBrandUserId );
                        CallableStatement callableStatement = null;
                        String calcBrandDashboardCounts = "{call sh_brand_dashboard(?,?,?,?,?,?)}";
                        try {


                            callableStatement = connection.prepareCall(calcBrandDashboardCounts);
                            callableStatement.setLong(1, lBrandUserId);
                            callableStatement.setString(2, sBrandUserCountry);
                            callableStatement.setLong(3, objLocalEntity.getSectorCode());
                            callableStatement.setLong(4, objLocalEntity.getItemCode());
                            callableStatement.setLong(5, lBrandUserDealer);
                            callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);

                            // execute getDBUSERByUserId store procedure
                            callableStatement.executeUpdate();

                            sReturnStatus = callableStatement.getString(6);
                            PreparedStatement ps = null;
                            if(sReturnStatus.length() >0 && sReturnStatus.startsWith("ERROR") ) {
                                LOG.debug("Calling sh_brand_dashboard procedure gave error: countrycode ="+sBrandUserCountry +", categoryCode=" + objLocalEntity.getSectorCode() + ", brandCode=" + objLocalEntity.getItemCode() + ", dealerCode=" + lBrandUserDealer );
                                LOG.debug(sReturnStatus);
                            } else if(sReturnStatus.length() >0 && sReturnStatus.startsWith("SUCCESS") ) {
                                // then fetch the values from brandaveragescores

                                strQuery = "select newcomments, flagcomments, thisyearcomments, nvl(twittershares, 0) twittershares, nvl(fbshares, 0) fbshares, comments from brandaveragescores_" + sBrandUserCountry +" where brandCode =? and  dealercode = ?";
                                ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                                ps.setString(1, String.valueOf(objLocalEntity.getItemCode()));
                                ps.setString(2, String.valueOf(lBrandUserDealer));
                                ResultSet objRs = ps.executeQuery();
                                try {
                                    while(objRs.next()) {

                                        objLocalEntity.setNewComments(Integer.parseInt(objRs.getString("newcomments")));
                                        objLocalEntity.setFlagComments(Integer.parseInt(objRs.getString("flagcomments")));
                                        objLocalEntity.setCurrentYearComments(Integer.parseInt(objRs.getString("thisyearcomments")));
                                        objLocalEntity.setFbShare(Integer.parseInt(objRs.getString("fbshares")));
                                        objLocalEntity.setTwShare(Integer.parseInt(objRs.getString("twittershares")));
                                        objLocalEntity.setComments(Integer.parseInt(objRs.getString("comments")));

                                    }
                                }
                                catch(Exception ex) {
                                    LOG.error("error fetching comments counts "+ex.getMessage());
                                    LOG.error(ex.getMessage(),ex);
                                }
                            }

                        } catch (SQLException e) {

                            LOG.error("error fetching comments counts "+e.getMessage());
                            LOG.error(e.getMessage(),e);


                        }
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
                    strQuery = "select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode='" + ((String) request.getSession().getAttribute("userCountry")) + "' and a.shUserId=b.shUserId and b.shUserId=c.id and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and approveComment <> '-' and a.brandCode=? and a.parentcommentid = 0" + sortParam;
                ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setString(1, String.valueOf(brandCode));
            } else if(regionName.equals("BrandDashboard") ) {
                if(brandCode == 0){
                    request.getSession().setAttribute("brandDashboardError", "true");
                }
                int count = 0;
                if(ratingFilterParam.length() >0 || statusFilterParam.length() > 0 || responseFilterParam.length() > 0  || sentimentFilterParam.length() > 0){
                    String filteredIdList = "0";
                    strQuery = "SELECT id from SURVEYCOMMENTS_SUMMARY where countryCode='" + sBrandUserCountry + "' and brandCode=? and dealercode = ?" +ratingFilterParam + statusFilterParam + responseFilterParam+ sentimentFilterParam;
                    LOG.debug(" summary query is "+strQuery);

                    try{
                        ps = connection.prepareStatement(strQuery);
                        ps.setString(1, String.valueOf(brandCode));
                        ps.setString(2, String.valueOf(lBrandUserDealer));
                        ResultSet objSummaryRs = ps.executeQuery();
                        while(objSummaryRs.next()) {
                            filteredIdList = filteredIdList +","+ objSummaryRs.getLong("id");
                        }
                        objSummaryRs.close();
                        ps.close();
                    } catch(Exception e) {
                       LOG.error(e.getMessage(),e);
                    }
                    if(filteredIdList.length() >3) {
                        strQuery = "select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode='" +sBrandUserCountry + "' and a.shUserId=b.shUserId and b.shUserId=c.id and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and approveComment <> '-' and a.brandCode=?  and a.parentcommentid = 0 and a.id in (" + filteredIdList + ")" + sortParam;
                    }else{
                        strQuery = "select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode='" + sBrandUserCountry + "' and a.shUserId=b.shUserId and b.shUserId=c.id and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and approveComment <> '-' and a.brandCode=?  and a.parentcommentid = 0 and a.id in (" + filteredIdList + ")" + sortParam;
                        queryChildComments = "false";
                    }
                } else{
                    strQuery = "select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and a.countryCode='" + sBrandUserCountry + "' and a.shUserId=b.shUserId and b.shUserId=c.id and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and commentApproved='Y' and approveComment <> '-' and a.brandCode=?  and a.parentcommentid = 0 "+ sortParam;
                }

                ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setString(1, String.valueOf(brandCode));
            } else if(regionName.equals("UserDetail")) {
                if(((String) request.getSession().getAttribute("socialshareUrl")).indexOf("my-message") > -1)
                    strQuery = "select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and parentcommentid = 0 and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and a.shUserId=b.shUserId and nvl(approveComment, 'N/A')  <> '-' and a.shUserId=?" + sortParam;
                else
                    strQuery = "select * from (select a.*, b.actionTime, c.personName From surveyComments a, userActivityLog b, serviceHeroUsers c where a.id=b.actionTypeId and parentcommentid = 0 and a.shuserId=c.id and b.actionType='" + UserActivityLog.ACTIVITY_COMMENT + "' and a.shUserId=b.shUserId and nvl(approveComment, 'N/A')  <> '-' and a.shUserId=?" + sortParam + ") where rownum<=4 order by rownum";
                ps = connection.prepareStatement(strQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setLong(1, shSurveyUser.getId());
            }
            LOG.debug(" query is "+strQuery);
            long diff = 0, minutes=0;
            ResultSet objRs = ps.executeQuery();
            try {
                objRs.last();
                rowCount = objRs.getRow();
                pageCount = (rowCount / cmtPerPage);
                if(rowCount % cmtPerPage > 0)
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

                for (int i = (((pageNo - 1) * cmtPerPage) + 1); i <= (pageNo * cmtPerPage) ; i++) {
                    objRs.absolute(i);
                    if(i % cmtPerPage == 1) {
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
                                userComment.setLanguageCode((String) request.getSession().getAttribute("userLanguage"));
                                userComment.setComment("welcomeNewWebsite");
                                //userComment.setRatingCode(0);// removed by Sudha on 23/03/2016 because 0 is a valid star rating; this sould be -99 or null
                                userComment.setRatingCode(-99);

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
                            userComment.setSectorCode(objRs.getString("sectorcode"));
                            userComment.setDealerName(objRs.getString("dealercode"));

                            if(regionName .equals("BrandDashboard"))  {
                                String dashboardPageTitle = objLocalEntity.getItemName()+ " | "+objLocalEntity.getSectorName()+ " | ";
                                request.getSession().setAttribute("dashboardPageTitle", dashboardPageTitle);

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
                                        LOG.debug(" user comment id "+userComment.getCommentId()+" for "+minutes+ " minutes");
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
                                userComment.setCommentActionTaken(checkHasBrandTakenAction(connection,userComment.getCommentId()));
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
                                //userComment.setRatingCode(0);//// removed by Sudha on 23/03/2016 because 0 is a valid star rating; this sould be -99 or null
                                userComment.setRatingCode(-99);
                            }
                            userComment.setAgreeCount(objRs.getInt("agree"));
                            userComment.setDisagreeCount(objRs.getInt("disagree"));
                            userComment.setFbShareCount(objRs.getInt("fbShare"));
                            userComment.setTwShareCount(objRs.getInt("twShare"));

                            // check if this has child comments
                            childCommentCount = checkHasSubCommentList(connection,userComment.getCommentId(), "false") ;
                            if (childCommentCount.length() >2){
                                userComment.setHasSubComment("Y");
                            }   else {
                                userComment.setHasSubComment("N");
                            }

                            // check if this has child comments
                            childCommentCount = checkHasSubCommentList(connection,(objRs.getLong("id")), "true") ;
                            LOG.debug("CommentId" + objRs.getLong("id") + " childCommentCount " + childCommentCount);
                            if (childCommentCount.length() > 2){
                                // check if that child has child response
                                LOG.debug("childCommentCount " + childCommentCount);
                                String[] parts = childCommentCount.split(",");
                                for (String part : parts) {
                                    if(part.length() > 1) {
                                        childCommentCount = checkHasSubCommentList(connection, Long.parseLong(part), "true");
                                        LOG.debug("CommentId sub" + part + " childCommentCount " + childCommentCount);
                                        if (childCommentCount.length() > 2) {
                                            LOG.debug("childCommentCount sub " + childCommentCount);
                                            userComment.setHasResponseComment("Y");
                                            break;
                                        } else {
                                            userComment.setHasResponseComment("N");
                                        }
                                    } else{
                                        userComment.setHasResponseComment("N");
                                    }
                                }
                            } else {
                                userComment.setHasResponseComment("N");
                            }


                            LOG.debug("CommentId" + objRs.getLong("id") + " childCommentCount "+childCommentCount);
                            commentList.add(userComment);

                            /*get the child records */
                            if(regionName .equals("BrandDashboard") || regionName.equals("BrandInfo") || regionName.equals("UserDetail")) {
                                if(queryChildComments.equals("true")) {
                                    if((regionName .equals("BrandInfo") || regionName .equals("UserDetail")) && userComment.getHasSubComment().equals("Y")) {
                                        getSubCommentList(connection, request, userComment.getCommentId(), regionName, commentList);
                                    }else if(regionName .equals("BrandDashboard")){
                                        getSubCommentList(connection, request, userComment.getCommentId(), regionName, commentList);
                                    }
                                }

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
                userComment.setLanguageCode((String) request.getSession().getAttribute("userLanguage"));
                userComment.setComment("welcomeNewWebsite");
                //userComment.setRatingCode(0); // removed by Sudha on 23/03/2016 because 0 is a valid star rating; this sould be -99 or null
                userComment.setRatingCode(-99);
                commentList.add(userComment);
            }

            if(regionName.equals("OtherArticles") && commentList.size() == 1) {
                commentList.add(userComment);
            }
            if(regionName .equals("BrandDashboard"))  {
                LOG.debug("Comment ids are ");
                for(int k = 0; k < commentList.size()-1; k++) {
                    LOG.debug(commentList.get(k).getCommentId() + " - "+ commentList.get(k).getHasSubComment() +" - "+commentList.get(k).getCommentStatus());

                }
            }
        } catch(Exception e) {
            LOG.error(e.getMessage(),e);
        }finally {
            Utils.closeQuietly(connection);
        }

        objLocalEntity.setUserComments(commentList);
        return objLocalEntity;
    }

    private String checkHasSubCommentList(Connection connection, long parentCommentid, String isParentBrandResponse) {
        String childCommentId = "";

        PreparedStatement ps = null;

        String strQuery = "select id from v_comment_tree where parentCommentid=?  and commentstatus in ( 26514, 29661) order by actiontime";
        if(isParentBrandResponse.equals("true")){
            strQuery = "select id from v_comment_tree where parentCommentid=?  and nvl(commentstatus, 26514) in ( 26514, 29661) order by actiontime";
        }
        try{
            ps = connection.prepareStatement(strQuery);
            ps.setLong(1, parentCommentid );
            ResultSet objRs = ps.executeQuery();
            while(objRs.next()) {
                childCommentId = childCommentId +","+ objRs.getLong("id");
            }
            objRs.close();
            ps.close();
        } catch(Exception e) {
           LOG.error(e.getMessage(),e);
        }
        return  childCommentId;
    }

    private String checkHasBrandTakenAction(Connection connection, long parentCommentid){
        String hasTakenAction = "false";

        PreparedStatement ps = null;

        String strQuery = "SELECT actiontime FROM v_comment_tree WHERE parentcommentid = ? and shuserid is null  and EXISTS ( SELECT 1 FROM v_comment_tree where parentcommentid = ? and shuserid is  null " +
                          "    and commentstatus in (  26513, 26515) ) and  NOT EXISTS ( SELECT 1 FROM v_comment_tree where  parentcommentid = ? and shuserid is  null " +
                          "    and commentstatus =  26514 )  order by actiontime";

        try{
            ps = connection.prepareStatement(strQuery);
            ps.setLong(1, parentCommentid);
            ps.setLong(2, parentCommentid);
            ps.setLong(3, parentCommentid);
            ResultSet objRs = ps.executeQuery();
            if(objRs.next()) {
                if(objRs.getTimestamp(1) != null) {
                    hasTakenAction = "true";
                    DateTime dt = new DateTime(objRs.getTimestamp("actiontime"));
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");
                    fmt = DateTimeFormat.forPattern("dd.MM.yyyy");
                    hasTakenAction = "<tri:resource key=\"core.UserProfileViewByBrand\" /> "+fmt.print(dt);
                }
            }
            objRs.close();
            ps.close();
        } catch(Exception e) {
           LOG.error(e.getMessage(),e);
        }

        return hasTakenAction;
    }

    /* if region is brandinfo, only display public messages and brand responses not flag comment
        if region is dashboard then show all
        nmaster commentid commentstatus should be new when ever brand reponds, or consumenr reponds
        consumenr marking a comment as private should mark the comment as privatein comment status
     */
    private void getSubCommentList(Connection connection, HttpServletRequest request, long parentCommentid, String regionName,  List<UserComment> commentList){
        String strQuery = "";
        UserComment usercomment;
        PreparedStatement ps = null;
        String childCommentCount = "";
        String isParentBrandResponse = "false";
        //LOG.error("entered getSubCommentList for commentid "+parentCommentid);
        int count = 0;
        if(regionName .equals("BrandInfo") || regionName .equals("UserDetail")) {
            strQuery = "select id, originalcomment, approvecomment, commentapproved, approvedtime, approvedlanguagecode,adminRating, agree, disagree, fbshare, twshare, branduserid, shuserid, personname, actiontime, commentstatus, brandresponsestatus, parentcommentid from v_comment_tree where parentCommentid=? and nvl(commentstatus, 26514) in ( 26514, 29661) order by actiontime";
        }else{
            strQuery = "select id, originalcomment, approvecomment, commentapproved, approvedtime, approvedlanguagecode,adminRating, agree, disagree, fbshare, twshare, branduserid, shuserid, personname, actiontime, commentstatus, brandresponsestatus, parentcommentid from v_comment_tree where parentCommentid=? and nvl(commentapproved, 'Y') <> 'N'";
        }
        LOG.debug("Query in getSubCommentList " + strQuery);
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

                usercomment.setLanguageCode(objRs.getString("approvedlanguagecode"));
                if (objRs.getString("BrandUserId") != null) { // is brand response
                    usercomment.setShUserId(objRs.getLong("branduserid"));
                    usercomment.setShUserName(objRs.getString("personname"));
                    usercomment.setComment(objRs.getString("approvecomment"));
                } else {
                    usercomment.setApproved(objRs.getString("commentapproved"));
                    usercomment.setShUserId(objRs.getLong("shuserid"));
                    usercomment.setShUserName("consumer");
                    if(objRs.getString("commentapproved").equals("X") || objRs.getString("commentapproved").equals("N")) {
                        usercomment.setComment(objRs.getString("originalcomment"));
                        usercomment.setRatingCode(-99);
                    }  else{
                        DateTime dt = new DateTime(objRs.getTimestamp("approvedtime"));
                        DateTimeFormatter fmt =   DateTimeFormat.forPattern("dd.MM.yyyy");
                        if(regionName .equals("BrandInfo") || regionName .equals("UserDetail")) {
                            fmt = DateTimeFormat.forPattern("dd.MM.yyyy");
                        }else{
                            fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
                        }
                        if(! objRs.getString("originalcomment").equals(objRs.getString("approvecomment")) )     {
                            //usercomment.setCommentEdited("Edited by moderator on "+fmt.print(dt));
                            usercomment.setCommentEdited("<tri:resource key=\"core.DashboardEditedByModerator\" />  "+fmt.print(dt));

                        }  else{
                            //usercomment.setCommentEdited("Moderated on "+fmt.print(dt));
                            usercomment.setCommentEdited("<tri:resource key=\"core.DashboardModeratedBy\" />  "+fmt.print(dt));
                        }
                        usercomment.setComment(objRs.getString("approvecomment"));
                        try {
                            usercomment.setRatingCode(Integer.parseInt(objRs.getString("adminRating")));
                        } catch(Exception ex) {
                            //userComment.setRatingCode(0);//// removed by Sudha on 23/03/2016 because 0 is a valid star rating; this sould be -99 or null
                            usercomment.setRatingCode(-99);
                        }
                        usercomment.setAgreeCount(objRs.getInt("agree"));
                        usercomment.setDisagreeCount(objRs.getInt("disagree"));
                        usercomment.setFbShareCount(objRs.getInt("fbShare"));
                        usercomment.setTwShareCount(objRs.getInt("twShare"));
                    }
                }


                usercomment.setSubmitDate(new DateTime(objRs.getTimestamp("actiontime")));
                usercomment.setCommentStatus(objRs.getString("commentstatus"));
                usercomment.setBrandResponseStatus(objRs.getString("brandresponsestatus"));

                usercomment.setParentCommentId(objRs.getLong("parentcommentid"));
                if(usercomment.getCommentStatus()!= null && usercomment.getCommentStatus().equals("26514")){
                    isParentBrandResponse = "true";
                }
                // check if this has child comments
                childCommentCount = checkHasSubCommentList(connection,(objRs.getLong("id")), isParentBrandResponse) ;
                LOG.debug("CommentId" + objRs.getLong("id") + " childCommentCount " + childCommentCount);
                if (childCommentCount.length() > 2){
                    usercomment.setHasSubComment("Y");
                    // check if that child has child response
                    LOG.debug("childCommentCount " + childCommentCount);
                    String[] parts = childCommentCount.split(",");
                    for (String part : parts) {
                        if(part.length() > 1) {
                            childCommentCount = checkHasSubCommentList(connection, Long.parseLong(part), isParentBrandResponse);
                            LOG.debug("CommentId sub" + part + " childCommentCount " + childCommentCount);
                            if (childCommentCount.length() > 2) {
                                LOG.debug("childCommentCount sub " + childCommentCount);
                                usercomment.setHasResponseComment("Y");
                                break;
                            } else {
                                usercomment.setHasResponseComment("N");
                            }
                        } else{
                            usercomment.setHasResponseComment("N");
                        }
                    }
                } else {
                    usercomment.setHasSubComment("N");
                    usercomment.setHasResponseComment("N");
                }

                commentList.add(usercomment);
                LOG.debug("CommentId" + objRs.getLong("id") + " childCommentCount " + childCommentCount);
                /*get the child records */
                if(regionName .equals("BrandDashboard") || regionName.equals("BrandInfo") || regionName.equals("UserDetail")) {
                    if((regionName .equals("BrandInfo") || regionName .equals("UserDetail")) && usercomment.getHasSubComment().equals("Y")) {
                        getSubCommentList(connection, request, usercomment.getCommentId(), regionName, commentList);
                    }else if(regionName .equals("BrandDashboard")){
                        getSubCommentList(connection, request, usercomment.getCommentId(), regionName, commentList);
                    }

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

    private BrandSearchList getBrandSearchList(HttpServletRequest request, Entity objEntity) {
        BrandSearchList objLocalEntity = (BrandSearchList) objEntity;
        try {
            String sector = request.getParameter("sectorCode");
            request.getSession().setAttribute("searchBrand", request.getParameter("srchTextFull"));
            List<BrandSearch> brandList = SHSearchService.searchBrand(request, request.getParameter("srchText"), sector);
            if(brandList != null)
                objLocalEntity.setBrandDtl(brandList);

        } catch(Exception e) {
            LOG.error(e.getMessage(),e);
        }finally {
        }
        return objLocalEntity;
    }

    private BrandSearchList getSimilarBrandList(HttpServletRequest request, Entity objEntity, String strSrchText) {
    	/* this method is used by serachbrand as well
    	   If this contains srchText then this is coming from brand search
    	 */
        BrandSearchList objLocalEntity = (BrandSearchList) objEntity;
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
            HashMap<String,String> objHMIdCountry = new HashMap<String,String>();
            HashMap<String,String> objHMIdPriority = new HashMap<String,String>();
            HashMap<String,String> objHMIdStarRating = new HashMap<String,String>();

            List<BrandSearch> brandSrchList = new ArrayList<BrandSearch>();

            if(strSrchText == null || strSrchText.length() == 0)
                strSrchText = "0";
            if(strSrchText.indexOf("/") > 01)
                strSrchText = strSrchText.replace("/", "");
            String userLanguage = (String) request.getSession().getAttribute("userCountry");
            objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SHORT_NAME, strSrchText, "votes~15");
            //objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_SECTOR, (String) request.getSession().getAttribute("surveySector"), "votes~15");
            objHMSort = (HashMap) objHM.get("sorter");
            objHMIdTitle = (HashMap) objHM.get("title");
            objHMIdLogo1X = (HashMap) objHM.get("logo1X");
            objHMIdLogo2X = (HashMap) objHM.get("logo2X");
            objHMIdSector = (HashMap) objHM.get("sectorId");
            objHMIdParentSector = (HashMap) objHM.get("parentSectorId");
            objHMIdCountry = (HashMap) objHM.get("countryCode");
            objHMIdPriority = (HashMap) objHM.get("priority");
            objHMIdStarRating = (HashMap)objHM.get("starRating");

            int count = 0;
            List<String> sorterList = new ArrayList<String>(objHMSort.keySet());
            String infoPageBrand = (String) request.getSession().getAttribute("surveyBrand");
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
                        objBrandList.setCountryCode(objHMIdCountry.get(strTempValue));
                        objBrandList.setPreferedBrand(objHMIdPriority.get(strTempValue));
                        objBrandList.setStarRating(objHMIdStarRating.get(strTempValue));
                        brandSrchList.add(objBrandList);
                        if(count > 5)
                            break;
                    }
                }
            }
            objLocalEntity.setBrandDtl(brandSrchList);
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return objLocalEntity;
    }


    private BrandSearchList getWinnerBrandList(HttpServletRequest request, Entity objEntity, String strSrchText) {
    	/* this method is used by serachbrand as well
    	   If this contains srchText then this is coming from brand search
    	 */
        BrandSearchList objLocalEntity = (BrandSearchList) objEntity;
        try {
            BrandSearch objBrandList;
            String sectorCode, strTempValue = "";
            HashMap<String,HashMap> objHM = new HashMap<String,HashMap>();
            HashMap<String,String> objHMSort = new HashMap<String,String>();
            HashMap<String,String> objHMIdTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo1X = new HashMap<String,String>();
            HashMap<String,String> objHMIdLogo2X = new HashMap<String,String>();
            HashMap<String,String> objHMIdSector = new HashMap<String,String>();
            HashMap<String,String> objHMIdSectorTitle = new HashMap<String,String>();
            HashMap<String,String> objHMIdParentSector = new HashMap<String,String>();
            HashMap<String,String> objHMIdCountry = new HashMap<String,String>();
            HashMap<String,String> objHMIdPriority = new HashMap<String,String>();
            HashMap<String,String> objHMIdStarRating = new HashMap<String,String>();

            List<BrandSearch> brandSrchList = new ArrayList<BrandSearch>();

            if(strSrchText == null || strSrchText.length() == 0)
                strSrchText = "0";
            if(strSrchText.indexOf("/") > 01)
                strSrchText = strSrchText.replace("/", "");
            String userLanguage = (String) request.getSession().getAttribute("userCountry");
            objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_AWARDS, strSrchText);
            objHMSort = (HashMap) objHM.get("sorter");
            objHMIdTitle = (HashMap) objHM.get("title");
            objHMIdLogo1X = (HashMap) objHM.get("logo1X");
            objHMIdLogo2X = (HashMap) objHM.get("logo2X");
            objHMIdSector = (HashMap) objHM.get("sectorId");
            objHMIdSectorTitle = (HashMap) objHM.get("sectorTitle");
            objHMIdParentSector = (HashMap) objHM.get("parentSectorId");
            objHMIdCountry = (HashMap) objHM.get("countryCode");
            objHMIdPriority = (HashMap) objHM.get("priority");
            objHMIdStarRating = (HashMap)objHM.get("starRating");

            int count = 0, maxCount = 0;
            List<String> sorterList = new ArrayList<String>(objHMSort.keySet());
            if(strSrchText.indexOf("?030") > -1)
                maxCount = 5;
            else
                maxCount = 2;

            if(strSrchText.indexOf("?05?") == -1)
                Collections.shuffle(sorterList);

            for (String sorterValue : sorterList) {
                strTempValue = objHMSort.get(sorterValue);
                if(objHMIdCountry.get(strTempValue).equals(userLanguage)) {
                    count++;
                    objBrandList = new BrandSearch();
                    objBrandList.setBrandCode(strTempValue);
                    objBrandList.setBrandName(objHMIdTitle.get(strTempValue));
                    objBrandList.setBrandLogo1x(objHMIdLogo1X.get(strTempValue));
                    objBrandList.setBrandLogo2x(objHMIdLogo2X.get(strTempValue));
                    objBrandList.setSectorCode(objHMIdSector.get(strTempValue));
                    objBrandList.setSectorName(objHMIdSectorTitle.get(strTempValue));
                    objBrandList.setParentSectorCode(objHMIdParentSector.get(strTempValue));
                    objBrandList.setCountryCode(objHMIdCountry.get(strTempValue));
                    objBrandList.setPreferedBrand(objHMIdPriority.get(strTempValue));
                    objBrandList.setStarRating(objHMIdStarRating.get(strTempValue));

                    brandSrchList.add(objBrandList);
                    if(count > maxCount)
                        break;
                }
            }
            objLocalEntity.setBrandDtl(brandSrchList);
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return objLocalEntity;
    }

    /* this function is a temp function written to map old brand ids with new in a table*/
    private void callFunctionToMapIds(HttpServletRequest request){
        //only read publicationId = 9
        //value is new
        // description is old
        TaxonomyFactory objTaxonomy = new TaxonomyFactory();
        String brandMapCategoryNames = "2878";
        String parentSectorMapCategoryNames = "2825";
        String SectorMapCategoryNames = "2826";
        String publicationId = "9";
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        int result = 0;
        ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
        Connection connection = null;

        try{
            /* first import the brands */
            connection = dataSource.getConnection();
            List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + publicationId + "-" + brandMapCategoryNames + "-512").getKeywordChildren();
            LOG.debug("categoryId Taxonomy found for publication: " + publicationId);
            Collections.sort(objKeyList);
            Iterator iterator = objKeyList.listIterator();
            Keyword objKey;
            String key;
            int count = 0;
            String oldSector = "", oldBrandid = "", newSectorId = "", newBrandid = "", newDealerId = "";
            StringTokenizer objSt1, objSt2;
            while (iterator.hasNext()) {
                //objKey = (Keyword) iterator.next();
                objKey = (Keyword) iterator.next();
                key = objKey.getKeywordURI();
                String sql = "INSERT INTO BRAND_MAP (USERLANGUAGE, OLD_SECTOR_ID, OLD_BRAND_ID, OLD_DEALER_ID, NEW_SECTOR_ID, NEW_BRAND_ID, NEW_DEALER_ID)"
                             + " VALUES (?, ?, ?, ?, ?, ?, ?)";

                objSt1 = new java.util.StringTokenizer((String)objKey.getKeywordName(), "|");
                oldSector = objSt1.nextToken(); //sector
                oldBrandid = objSt1.nextToken(); //brandid

                objSt2 = new java.util.StringTokenizer((String)objKey.getKeywordDescription(), "|");
                newSectorId = objSt2.nextToken(); //sector
                newBrandid = objSt2.nextToken(); //brand
                newDealerId = objSt2.nextToken(); //dealer

                PreparedStatement ps = connection.prepareStatement(sql);// 10011|10014   800|1992|1098
                ps.setString(1, "en");
                ps.setLong(2, Long.parseLong(oldSector));
                ps.setLong(3, Long.parseLong(oldBrandid));
                ps.setLong(4, 0);
                ps.setLong(5, Long.parseLong(newSectorId));
                ps.setLong(6, Long.parseLong(newBrandid));
                if(newDealerId.equals("NA")) newDealerId = "0";
                ps.setLong(7, Long.parseLong(newDealerId));
                result = ps.executeUpdate();
                ps.close();
                // LOG.error(result + " brand row inserted. ");
                count++;


                //treeMap.put(objKey.getKeywordKey() + "_" + key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")), objKey.getKeywordDescription());
            }
            LOG.debug("count of brands : " + count);
            // parent sector
            /* first import the brands */
            count = 0;
            List<Keyword> objParentSectoKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + publicationId + "-" + parentSectorMapCategoryNames + "-512").getKeywordChildren();
            LOG.debug("categoryId Taxonomy found for publication: " + publicationId);
            Collections.sort(objKeyList);
            Iterator seconditerator = objParentSectoKeyList.listIterator();
            Keyword objSecondKey;
            String secondkey;
            while (seconditerator.hasNext()) {
                //objKey = (Keyword) iterator.next();
                objSecondKey = (Keyword) seconditerator.next();
                secondkey = objSecondKey.getKeywordURI();
                String sql = "INSERT INTO PARENT_SECTOR_MAP (USERLANGUAGE, OLD_SECTOR_ID, NEW_SECTOR_ID)"
                             + " VALUES (?, ?, ?)";


                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, "en");
                ps.setLong(2, Long.parseLong(objSecondKey.getKeywordDescription()));
                ps.setLong(3, Long.parseLong(objSecondKey.getKeywordName()));
                result = ps.executeUpdate();
                ps.close();
                //LOG.error(result + " parent sector row inserted. ");
                count++;

                //treeMap.put(objKey.getKeywordKey() + "_" + key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")), objKey.getKeywordDescription());
            }

            LOG.debug("count of parent sectors : " + count);
            count = 0;
            List<Keyword> objSubSectoKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + publicationId + "-" + SectorMapCategoryNames + "-512").getKeywordChildren();
            LOG.debug("categoryId Taxonomy found for publication: " + publicationId);
            Collections.sort(objKeyList);
            Iterator thirditerator = objSubSectoKeyList.listIterator();
            Keyword objThirdKey;
            String thirdkey;
            while (thirditerator.hasNext()) {
                //objKey = (Keyword) iterator.next();
                objThirdKey = (Keyword) thirditerator.next();
                thirdkey = objThirdKey.getKeywordURI();
                String sql = "INSERT INTO NEW_SECTOR_MAP (USERLANGUAGE, OLD_SECTOR_ID, NEW_SECTOR_ID)"
                             + " VALUES (?, ?, ?)";

                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, "en");
                ps.setLong(2, Long.parseLong(objThirdKey.getKeywordDescription()));
                ps.setLong(3, Long.parseLong(objThirdKey.getKeywordName()));
                result = ps.executeUpdate();
                ps.close();
                //LOG.error(result + " parent sector row inserted. ");
                count++;

                //treeMap.put(objKey.getKeywordKey() + "_" + key.substring(key.indexOf("-") + 1, key.lastIndexOf("-")), objKey.getKeywordDescription());
            }



            LOG.debug("count of sectors : " + count);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            LOG.error("No CategoryLabels Taxonomy found for publication: " + publicationId);

        }finally {
            Utils.closeQuietly(connection);
        }

    }
    
    /*private Localization createLocalization(PublicationMapping publicationMapping) throws LocalizationResolverException {
        final String id = Integer.toString(publicationMapping.getPublicationId());
        final String path = getPublicationMappingPath(publicationMapping);

        try {
            return localizationFactory.createLocalization(id, path);
        } catch (LocalizationFactoryException e) {
            throw new LocalizationResolverException("Exception while creating localization: [" + id + "] " + path, e);
        }
    } */

    /**
     * Gets the publication mapping path. The returned path always starts with a "/" and does not end with a "/", unless
     * the path is the root path "/" itself.
     *
     * @param publicationMapping The publication mapping.
     * @return The publication mapping path.
     */
    /*private String getPublicationMappingPath(PublicationMapping publicationMapping) {
        String path = Strings.nullToEmpty(publicationMapping.getPath());
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    } */

    /**
     *
     * @param //comments
     * @return
     */
  /*  @RequestMapping(value = "/addBlogComment", method = RequestMethod.POST)
    public ModelAndView  addBlogComment(@ModelAttribute("blogComments") BlogComments comments) {
         // do some thing here, e.g: update database, ...
        return new ModelAndView("viewUser", "blogComments", comments);
    }   */


    private BrandSentimentGraph getBrandSentimenChart(HttpServletRequest request, Entity objEntity) {
        ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
        PoolingDataSource<PoolableConnection> dataSource = (PoolingDataSource<PoolableConnection>) context.getBean("dataSource");
        Connection connection = null;
        BrandSentimentGraph objLocalEntity = (BrandSentimentGraph) objEntity;
        try {
            String otherUserCountry = "", sBrandUserCountry = "", strSurveyTable = "surveysubmission_kw", strBrandDtl= "";
            int  endDateYear =0, endDateMonth = 0, endDateDay = 0;
            String startDate = "", endDate="", strTempValue= "";
            int  brandCode = 0,sectorcode = 0, lBrandUserDealer = 0;
            List<SentimentGraphList> sentimentsList = new ArrayList<SentimentGraphList>();
            SentimentGraphList sentiment = null;

            strBrandDtl = request.getParameter("brand");
            LOG.debug(" regionname is BrandDashboard and inside graph and brand param is  "+strBrandDtl );
            if (strBrandDtl == null || strBrandDtl.length() == 0 || strBrandDtl.indexOf("~") == -1){
                brandCode = 0;
            }   else {

                String[] strBrand = strBrandDtl.split("~");
                //StringTokenizer strBrand = new java.util.StringTokenizer(strTemp, "~");
                if (strBrand.length == 4) {
                    sBrandUserCountry = (strBrand[0]);
                    sectorcode = Integer.parseInt(strBrand[1]);
                    brandCode = Integer.parseInt(strBrand[2]);
                    String strTempDealer = strBrand[3];
                    if (strTempDealer == null || strTempDealer.length() == 0 || strTempDealer.equals("N/A") )
                        lBrandUserDealer = 0;
                    else
                        lBrandUserDealer = Integer.parseInt(strTempDealer);
                } else{
                    brandCode = 0;
                }
            }
            if(sBrandUserCountry.equals("kw")) {
               strSurveyTable = "surveysubmission_kw";
               otherUserCountry = "ae";
            } else if(sBrandUserCountry.equals("ae")) {
               otherUserCountry = "kw";
               strSurveyTable = "surveysubmission_ae";
            }

            if(brandCode != 0) {
                LOG.debug(" sectorcode  " + sectorcode + " brandCode is  " + brandCode + " lBrandUserDealer " + lBrandUserDealer);
                HashMap<String, HashMap> objHM = new HashMap<String, HashMap>();
                HashMap<String, String> objHMSort = new HashMap<String, String>();
                HashMap<String, String> objHMIdTitle = new HashMap<String, String>();
                HashMap<String, String> objHMIdSectorId = new HashMap<String, String>();
                HashMap<String, String> objHMIdSectorTitle = new HashMap<String, String>();
                HashMap<String, String> objHMIdDealers = new HashMap<String, String>();

                objHM = SHSearchService.searchBrands(request, BrandLuceneIndexer.INDEXER_SERACH_FIELD_BRAND_CODE, Long.toString(brandCode));

                objHMSort = (HashMap) objHM.get("sorter");
                objHMIdTitle = (HashMap) objHM.get("title");
                objHMIdSectorId = (HashMap) objHM.get("sectorId");
                objHMIdSectorTitle = (HashMap) objHM.get("sectorTitle");
                objHMIdDealers = (HashMap) objHM.get("dealers");

                List<String> sorterList = new ArrayList<String>(objHMSort.keySet());
                Collections.sort(sorterList);
                for (String sortValue : sorterList) {
                    strTempValue = objHMSort.get(sortValue);
                    objLocalEntity.setItemCode(brandCode);
                    objLocalEntity.setItemName(objHMIdTitle.get(strTempValue));
                    objLocalEntity.setSectorCode(sectorcode);
                    objLocalEntity.setSectorName(objHMIdSectorTitle.get(strTempValue));
                    objLocalEntity.setDealerCode(lBrandUserDealer);
                    objLocalEntity.setDealerName(objHMIdDealers.get(strTempValue));

                }
                //take it for 2 years from sys date
                DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
                Calendar cal = Calendar.getInstance();
                startDate = dateFormat.format(cal.getTime());
                LOG.debug(dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
                cal.add(Calendar.YEAR, -2);
                endDateYear = cal.YEAR;
                endDateMonth = cal.MONTH;
                endDateDay = cal.DATE;
                endDate = dateFormat.format(cal.getTime());

                /* first import the brands */
                connection = dataSource.getConnection();
                String query = "";
                ResultSet result = null;
                Statement stmt = null;

                query = "SELECT avg(a.adminrating) as sentiment, count(a.adminrating) as cntSentiment, to_char(b.actiontime,'MM.DD.YYYY') as surveydt, to_char(b.actiontime,'YYYYMMDD') as surveydtsort, to_char(b.actiontime,'YYYY, MM, DD') as surveydt1 from surveycomments a, useractivitylog b," + strSurveyTable + " c where a.commentapproved='Y' and c.brandcode=" + brandCode + " and b.actiontypeid=c.id and a.surveyvotingid=c.id GROUP BY to_char(b.actiontime,'MM.DD.YYYY'), to_char(b.actiontime,'YYYY, MM, DD'), to_char(b.actiontime,'YYYYMMDD') having count(a.adminrating)>1 and to_timestamp(to_char(b.actiontime,'MM.DD.YYYY'), 'MM.DD.YYYY') <= to_timestamp('" + startDate + "', 'MM.DD.YYYY') \n" +
                        "and to_timestamp(to_char(b.actiontime,'MM.DD.YYYY'), 'MM.DD.YYYY') >= to_timestamp('" + endDate + "', 'MM.DD.YYYY')  order by surveydtsort";

                stmt = connection.createStatement();
                LOG.debug("Query is " + query);
                result = stmt.executeQuery(query);
                int rowCount = 0;

                LOG.debug("Query result is " + result.next());

                java.util.Calendar calendar = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("Asia/Kuwait"));
                calendar.set(endDateDay, endDateMonth, endDateYear, 0, 0, 0);
                int yyyy, mm, dd;
                String[] surveyDateFields = new String[3];

                while (result.next()) {
                    surveyDateFields = result.getString("surveydt").split("\\.");
                    yyyy = Integer.parseInt(surveyDateFields[2]);
                    mm = Integer.parseInt(surveyDateFields[0]);
                    dd = Integer.parseInt(surveyDateFields[1]);
                    calendar.set(yyyy, mm, dd);
                    //if(rowCount > 1)
                    //out.println(",");
                    //LOG.error("//" + yyyy + "-" + mm + "-" + dd + ";");
                    //LOG.error("//" + calendar.getTime() + ";");
                    sentiment = new SentimentGraphList();
                    sentiment.setSurveydt(result.getString("surveydt"));
                    sentiment.setSentiment(result.getString("sentiment"));
                    sentiment.setCntSentiment(result.getInt("cntSentiment"));
                    sentiment.setSurveydtsort(result.getString("surveydtsort"));
                    sentiment.setFormatedSurveydt(result.getString("surveydt1"));
                    sentimentsList.add(sentiment);

                    // LOG.error("\tohlc.push([" + calendar.getTimeInMillis() + "," + df2.format(result.getFloat("sentiment")) + "," + result.getInt("cntSentiment") + ",'" + result.getString("surveydt") + "']);");


                    //LOG.error("\tvolume.push([" + calendar.getTimeInMillis() + "," + result.getInt("cntSentiment") + "]);");

                    rowCount++;
                }

                result.close();
                stmt.close();
                connection.close();
            }else{
                request.getSession().setAttribute("brandDashboardError", "true");
            }
            objLocalEntity.setSentimentGraph(sentimentsList);

        }catch (SQLException e)
        {
            LOG.error(e.getMessage(), e);
            LOG.error("Error querying BrandSentimentGraph for brand" );

        }finally {
            Utils.closeQuietly(connection);
        }
        return objLocalEntity;
    }
}
