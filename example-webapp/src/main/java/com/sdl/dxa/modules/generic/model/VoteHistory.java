package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

import java.util.List;

import org.joda.time.DateTime;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Tag;

/**
 * VoteHistory
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "VoteHistory", vocabulary = SCHEMA_ORG, prefix = "vh", public_ = true)
})
public class VoteHistory extends AbstractEntity {
	@SemanticProperty("vh:countryCode") 
    private String countryCode;
    
	@SemanticProperty("vh:brandCode") 
    private long brandCode;
    
	@SemanticProperty("vh:brandName") 
    private String brandName;
    
	@SemanticProperty("vh:logo1x") 
    private String logo1x;

	@SemanticProperty("vh:logo2x") 
    private String logo2x;

	@SemanticProperty("vh:sectorName") 
    private String sectorName;
	
	@SemanticProperty("vh:dealerCode") 
    private String dealerCode;

	@SemanticProperty("vh:voteDate") 
    private DateTime voteDate;

	@SemanticProperty("vh:voteAverage") 
    private double voteAverage;
		
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public long getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(long brandCode) {
		this.brandCode = brandCode;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getLogo1x() {
		return logo1x;
	}

	public void setLogo1x(String logo1x) {
		this.logo1x = logo1x;
	}

	public String getLogo2x() {
		return logo2x;
	}

	public void setLogo2x(String logo2x) {
		this.logo2x = logo2x;
	}

	public String getSectorName() {
		return sectorName;
	}

	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}

	public String getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}

	public DateTime getVoteDate() {
		return voteDate;
	}

	public void setVoteDate(DateTime voteDate) {
		this.voteDate = voteDate;
	}

	public double getVoteAverage() {
		return voteAverage;
	}

	public void setVoteAverage(double voteAverage) {
		this.voteAverage = voteAverage;
	}

	@Override
    public String toString() {
        return "VoteHistory{" +
        		"countryCode='" + countryCode + '\'' +
                ", brandCode=" + brandCode +
                ", brandName='" + brandName + '\'' +
                ", logo1x='" + logo1x + '\'' +
                ", logo2x='" + logo2x + '\'' +
                ", sectorName='" + sectorName + '\'' +
                ", dealerCode='" + dealerCode + '\'' +
                ", voteDate=" + voteDate +
                ", voteAverage=" + voteAverage +
                '}';
    }	
}
