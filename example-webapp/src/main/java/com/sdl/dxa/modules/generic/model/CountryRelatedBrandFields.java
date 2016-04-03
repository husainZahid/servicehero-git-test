package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Paragraph;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * CountryRelatedFields
 *
 * @author Saurabh
 */

	@SemanticEntity(entityName = "CountryRelatedBrandFields", vocabulary = SCHEMA_ORG, prefix = "crbf", public_ = true)
public class CountryRelatedBrandFields extends AbstractEntity {
	@SemanticProperty("crbf:title")
	private String title;
    
	@SemanticProperty("crbf:introduction")
    private String introduction;

	@SemanticProperty("crbf:quota")
	private int quota;

	@SemanticProperty("crbf:dealer")
	private List<Dealer> dealer;

	public List<Dealer> getDealer() {
		return dealer;
	}

	public void setDealer(List<Dealer> dealer) {
		this.dealer = dealer;
	}

	@SemanticProperty("crbf:awards")
	private List<BrandAwards> awards;
	
	@SemanticProperty("crbf:information")
	private List<Paragraph> information;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public int getQuota() {
		return quota;
	}

	public void setQuota(int quota) {
		this.quota = quota;
	}

	public List<BrandAwards> getAwards() {
		return awards;
	}

	public void setAwards(List<BrandAwards> awards) {
		this.awards = awards;
	}

	public List<Paragraph> getInformation() {
		return information;
	}

	public void setInformation(List<Paragraph> information) {
		this.information = information;
	}

	@Override
    public String toString() {
        return "CountryRelatedBrandFields{" +
                " title='" + title + '\'' +
                ", introduction='" + introduction + '\'' +
                ", quota=" + quota +
                ", awards=" + awards +
                ", information=" + information +
                '}';
    }
}
