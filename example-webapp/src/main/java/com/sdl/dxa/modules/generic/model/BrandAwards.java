package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Tag;

/**
 * BrandAwards
 *
 * @author Saurabh
 */
@SemanticEntity(entityName = "BrandAwards", vocabulary = SCHEMA_ORG, prefix = "ba", public_ = true)
public class BrandAwards extends AbstractEntity {
	 
	@SemanticProperty("ba:awardYear") 
    private Tag awardYear;
    	
	@SemanticProperty("ba:awardType") 
	private Tag awardType;
   	
	@SemanticProperty("ba:awardImage") 
    private AwardImage awardImage;
	
	public Tag getAwardYear() {
		return awardYear;
	}

	public void setAwardYear(Tag awardYear) {
		this.awardYear = awardYear;
	}

	public Tag getAwardType() {
		return awardType;
	}

	public void setAwardType(Tag awardType) {
		this.awardType = awardType;
	}

	public AwardImage getAwardImage() {
		return awardImage;
	}

	public void setAwardImage(AwardImage awardImage) {
		this.awardImage = awardImage;
	}
	
	@Override
    public String toString() {
        return "BrandAwards{" +
                "awardYear=" + awardYear +
                ", awardType=" + awardType +
                ", awardImage=" + awardImage +
                '}';
    }	
}
