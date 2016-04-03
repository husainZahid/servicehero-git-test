package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import java.util.List;


import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

/**
 * SurveyLastVisit
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "SurveyLastVisit", vocabulary = SCHEMA_ORG, prefix = "slv", public_ = true)
})
public class SurveyLastVisit extends AbstractEntity {
	@SemanticProperty("slv:brandCode") 
    private int brandCode;
        
	@SemanticProperty("slv:dealer") 
    private List<KeyValuePair> dealer;
	
	@SemanticProperty("slv:visit") 
	private List<KeyValuePair> visit;
		
	public int getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(int brandCode) {
		this.brandCode = brandCode;
	}

	public List<KeyValuePair> getDealer() {
		return dealer;
	}

	public void setDealer(List<KeyValuePair> dealer) {
		this.dealer = dealer;
	}

	public List<KeyValuePair> getVisit() {
		return visit;
	}

	public void setVisit(List<KeyValuePair> visit) {
		this.visit = visit;
	}

	@Override
    public String toString() {
        return "SurveyLastVisit{" +
                "brandCode=" + brandCode +
                ", dealer=" + dealer +
                ", visit=" + visit +
                '}';
    }	
}
