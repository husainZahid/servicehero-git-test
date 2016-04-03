package com.sdl.dxa.modules.generic.utilclasses;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.broker.querying.MetadataType;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.Criteria;
import com.tridion.broker.querying.criteria.content.ItemSchemaCriteria;
import com.tridion.broker.querying.criteria.content.PublicationCriteria;
import com.tridion.broker.querying.criteria.metadata.CustomMetaDateRangeCriteria;
import com.tridion.broker.querying.criteria.metadata.CustomMetaKeyCriteria;
import com.tridion.broker.querying.criteria.metadata.CustomMetaValueCriteria;
import com.tridion.broker.querying.criteria.operators.AndCriteria;
import com.tridion.broker.querying.filter.LimitFilter;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.broker.querying.sorting.column.CustomMetaKeyColumn;
import com.tridion.dynamiccontent.ComponentPresentationAssembler;

@Scope("session")
public class SiteLabels {
	private static Logger logger = LoggerFactory.getLogger(SiteLabels.class);
    public TreeMap<String, TreeMap<String, String>> siteLabels = new TreeMap<String, TreeMap<String, String>>();

    public TreeMap<String, TreeMap<String, String>> getSiteLabels() {
		return siteLabels;
	}
	public void setSiteLabels(TreeMap<String, TreeMap<String, String>> siteLabels) {
		this.siteLabels = siteLabels;
	}

	public SiteLabels()
    {
		setDefaltValues();
    }

	public void setDefaltValues() {
		TreeMap<String, String> treeMap;
        String publicationId;
        try
        {
			String currentLanguageCode, previousLanguageCode = "";
			treeMap = new TreeMap<String, String>();

			DatabaseConnection dbConnection = new DatabaseConnection();
	        ResultSet objRs = dbConnection.executeSQLQuery("select c.langCode, a.headerLabel, b.headerValue from SITEHEADERS a, SITEHEADERSVALUE b, siteLanguages c where a.id=b.headerId and b.langId=c.id and a.id>10000 order by c.id, a.id", false);
	        			
			while (objRs.next())
			{
				currentLanguageCode = objRs.getString(1);
				if(!previousLanguageCode.equals("") && !previousLanguageCode.equals(currentLanguageCode)) {
			        logger.debug("Sitelabels initiated: " + previousLanguageCode +  "  " + treeMap.size());
					siteLabels.put(previousLanguageCode, treeMap);
					treeMap = new TreeMap<String, String>();
				}
				treeMap.put(objRs.getString(2), objRs.getString(3));
				previousLanguageCode = currentLanguageCode;
			}
	        objRs.close();
	        dbConnection.getConnection().close();
	        if(!previousLanguageCode.equals(""))
	        {
	        	logger.debug("Sitelabels initiated: " + previousLanguageCode +  "  " + treeMap.size());
				siteLabels.put(previousLanguageCode, treeMap);
	        }

            /*AndCriteria commonCriList, extraCriList, finalCriList;
            Query myQuery = new Query();
            Vector vCriteria = new Vector();
            vCriteria.add(new PublicationCriteria(11));
            vCriteria.add(new ItemSchemaCriteria(7614));
            vCriteria.add(new CustomMetaValueCriteria(new CustomMetaKeyCriteria("MediaType"), "01 - Press Coverage"));
            commonCriList = getANDCriteria(vCriteria);

            CustomMetaKeyColumn customColumn = new CustomMetaKeyColumn("PublishDate", MetadataType.DATE);
            myQuery.addSorting(new SortParameter(customColumn, SortParameter.DESCENDING));
            myQuery.addLimitFilter(new LimitFilter(9999));

            CustomMetaDateRangeCriteria mdDateCriteria = null;
            String[] itemURIs;
            myQuery.setCriteria(commonCriList);
            itemURIs = myQuery.executeQuery();*/
	        
	        logger.debug("Sitelabels initiated: " + SettingsVariables.environmentVariables.get("tridionPublicationIds") +  "  " + SettingsVariables.environmentVariables.get("tridionSiteLabelId"));
	        /*TaxonomyFactory objTaxonomy = new TaxonomyFactory();
		    StringTokenizer objSt = new StringTokenizer(SettingsVariables.environmentVariables.get("tridionPublicationIds"), ",");
	        while (objSt.hasMoreTokens()) {
	            publicationId = objSt.nextToken();
	            treeMap = new TreeMap<String, String>();
	            try {
		        	List<Keyword> objKeyList = objTaxonomy.getTaxonomyKeywords("tcm:" + publicationId + "-" + SettingsVariables.environmentVariables.get("tridionSiteLabelId") + "-512").getKeywordChildren();
		        	logger.error("1: Sitelabels initiated: " + objKeyList.size());
	                Iterator<Keyword> iterator = objKeyList.listIterator();
	                Keyword objKey;
	                while(iterator.hasNext())
	                {
	                    objKey = (Keyword) iterator.next();
	                    treeMap.put(objKey.getKeywordName(), objKey.getKeywordDescription());
			        	logger.error("2: Sitelabels initiated: " + objKey.getKeywordName());
	                }
		        	logger.debug("Sitelabels initiated: " + publicationId +  "  " + treeMap.size());
	                siteLabels.put(publicationId, treeMap);
	            } catch(Exception e) {
	                logger.error("No SiteLabels Taxonomy found for publication: " + publicationId);
	            }
	        }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        	logger.error(e.getMessage(),e);
        }
    }
	
	public String getLabelValue(String headerLabel, String languageIdentifier) {
        String labelValue;
        TreeMap<String, String> objTM = (TreeMap<String, String>) siteLabels.get(languageIdentifier);
        if (objTM == null) {	
                labelValue = headerLabel;
        } else {
            if (objTM.get(headerLabel) == null) {
                labelValue = headerLabel;
            } else {
                labelValue = (String) objTM.get(headerLabel);
            }
        }
        return labelValue;
    }
	
	public static AndCriteria getANDCriteria(Vector vCriteria) {
        AndCriteria commonCriList = null;
        Criteria[] commonCri = new Criteria[vCriteria.size()];
        for(int i = 0; i < vCriteria.size(); i++)
            commonCri[i] = (Criteria)vCriteria.elementAt(i);
        commonCriList =  new AndCriteria(commonCri);
        return commonCriList;
    }
	
}
