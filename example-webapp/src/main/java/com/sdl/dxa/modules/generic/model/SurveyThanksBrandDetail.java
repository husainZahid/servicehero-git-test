package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

/**
 * SurveyThanksBrandDetail
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "SurveyThanksBrandDetail", vocabulary = SCHEMA_ORG, prefix = "stbd", public_ = true)
})
public class SurveyThanksBrandDetail extends AbstractEntity {
	@SemanticProperty("stbd:brandCode") 
    private int brandCode;
    
	@SemanticProperty("stbd:brandName") 
    private String brandName;
    
    @SemanticProperty("stbd:brandImage") 
    private String brandImage;
	
    @SemanticProperty("stbd:brandImage2x") 
    private String brandImage2x;
	
    @SemanticProperty("stbd:voteAverage") 
    private double voteAverage;
    
    @SemanticProperty("stbd:starRating") 
    private String starRating;
   
    @SemanticProperty("stbd:dealerName") 
    private String dealerName;
	
	public int getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(int brandCode) {
		this.brandCode = brandCode;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandImage() {
		return brandImage;
	}

	public void setBrandImage(String brandImage) {
		this.brandImage = brandImage;
	}

	public String getBrandImage2x() {
		return brandImage2x;
	}

	public void setBrandImage2x(String brandImage2x) {
		this.brandImage2x = brandImage2x;
	}

	public double getVoteAverage() {
		return voteAverage;
	}

	public void setVoteAverage(double voteAverage) {
		this.voteAverage = voteAverage;
	}

	public String getStarRating() {
		return starRating;
	}

	public void setStarRating(String starRating) {
		this.starRating = starRating;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	@Override
    public String toString() {
        return "SurveyThanksBrandDetail{" +
                "brandCode=" + brandCode +
                ", brandName='" + brandName + '\'' +
                ", brandImage='" + brandImage + '\'' +
                ", brandImage2x='" + brandImage2x + '\'' +
                ", voteAverage=" + voteAverage +
                ", starRating='" + starRating + '\'' +
                ", dealerName='" + dealerName + '\'' +
                '}';
    }	
}
