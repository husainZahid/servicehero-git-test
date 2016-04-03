package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
/**
 * Created by Sudha on 2/24/2016.
 */

@SemanticEntities({
	@SemanticEntity(entityName = "BrandSentimentGraph", vocabulary = SCHEMA_ORG, prefix = "bs", public_ = true)
})
public class BrandSentimentGraph extends AbstractEntity {

	@SemanticProperty("bs:itemCode")
    private int itemCode;

	@SemanticProperty("bs:itemName")
	private String itemName;

	@SemanticProperty("bs:sectorCode")
	private int sectorCode;

	@SemanticProperty("bs:sectorName")
	private String sectorName;

	@SemanticProperty("bs:dealerCode")
	private int dealerCode;

	@SemanticProperty("bs:dealerName")
	private String dealerName;

	@SemanticProperty("bs:countryCode")
	private String countryCode;

	@SemanticProperty("bs:languageCode")
	private String languageCode;

	@SemanticProperty("bs:sentimentGraph")
	private List<SentimentGraphList> sentimentGraph;

	public int getItemCode() {
		return itemCode;
	}

	public void setItemCode(int itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getSectorCode() {
		return sectorCode;
	}

	public void setSectorCode(int sectorCode) {
		this.sectorCode = sectorCode;
	}

	public String getSectorName() {
		return sectorName;
	}

	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}

	public int getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(int dealerCode) {
		this.dealerCode = dealerCode;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public List<SentimentGraphList> getSentimentGraph() {
		return sentimentGraph;
	}

	public void setSentimentGraph(List<SentimentGraphList> sentimentGraph) {
		this.sentimentGraph = sentimentGraph;
	}

	@Override
	public String toString() {
		return "BrandSentimentGraph{" +
		       "itemCode=" + itemCode +
		       ", itemName='" + itemName + '\'' +
		       ", sectorCode=" + sectorCode +
		       ", sectorName='" + sectorName + '\'' +
		       ", dealerCode=" + dealerCode +
		       ", dealerName='" + dealerName + '\'' +
		       ", countryCode='" + countryCode + '\'' +
		       ", languageCode='" + languageCode + '\'' +
		       ", sentimentGraph=" + sentimentGraph +
		       '}';
	}
}
