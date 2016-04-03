package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Tag;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by Sudha on 11/24/2015.
 */
@SemanticEntities({
	@SemanticEntity(entityName = "CampaignRedirect", vocabulary = SCHEMA_ORG, prefix = "cr", public_ = true)
})
public class CampaignRedirect extends AbstractEntity {
	@SemanticProperty("cr:language")
	private Tag language;

	@SemanticProperty("cr:country")
	private Tag country;
	
	@SemanticProperty("cr:sector")
	private Sector sector;
	
	/*@SemanticProperty("cr:brand")
	private Brand brand;
	
	@SemanticProperty("cr:dealer")
	private Dealer dealer;
	
	@SemanticProperty("cr:redirectPage")
	private AbstractEntity redirectPage;*/

	public Tag getLanguage() {
		return language;
	}

	public void setLanguage(Tag language) {
		this.language = language;
	}

	public Tag getCountry() {
		return country;
	}

	public void setCountry(Tag country) {
		this.country = country;
	}

	public Sector getSector() {
		return sector;
	}

	public void setSector(Sector sector) {
		this.sector = sector;
	}

	/*public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Dealer getDealer() {
		return dealer;
	}

	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}

	public AbstractEntity getRedirectPage() {
		return redirectPage;
	}

	public void setRedirectPage(AbstractEntity redirectPage) {
		this.redirectPage = redirectPage;
	}*/

	@Override
	public String toString() {
		return "CampaignRedirect{" +
		       "language=" + language +
		       ", country=" + country +
		       ", sector=" + sector +
		       /*", brand=" + brand +
		       ", dealer=" + dealer +
		       ", redirectPage=" + redirectPage +*/
		       '}';
	}
}
