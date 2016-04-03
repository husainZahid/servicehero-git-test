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
 * @author saurabh
 *
 */
@SemanticEntities({
	@SemanticEntity(entityName = "Sector", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
	@SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class Sector extends AbstractEntity {
	@SemanticProperty("s:isParentSector")
    private Tag isParentSector;
	
	@SemanticProperty("s:parentSector")
    private String parentSector;

	@SemanticProperty("s:active")
    private Tag active;
	
	@SemanticProperty("s:countryList")
    private List<Tag> countryList;

    @SemanticProperty("s:kwDetails")
    private CountryRelatedFields kwDetails;

    @SemanticProperty("s:aeDetails")
    private CountryRelatedFields aeDetails;

	@SemanticProperties({
		@SemanticProperty("s:dateCreated"),
		@SemanticProperty("m:dateCreated")
	})
	private DateTime date;

	@SemanticProperties({
		@SemanticProperty("s:description"),
		@SemanticProperty("m:description")
	})
    private String description;

	@SemanticProperties({
		@SemanticProperty("s:introText"),
		@SemanticProperty("m:introText")
	})
    private String introText;


	public Tag getIsParentSector() {
		return isParentSector;
	}

	public void setIsParentSector(Tag isParentSector) {
		this.isParentSector = isParentSector;
	}

	public String getParentSector() {
		return parentSector;
	}

	public void setParentSector(String parentSector) {
		this.parentSector = parentSector;
	}

	public Tag getActive() {
		return active;
	}

	public void setActive(Tag active) {
		this.active = active;
	}

	public List<Tag> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<Tag> countryList) {
		this.countryList = countryList;
	}
	
	public CountryRelatedFields getKwDetails() {
		return kwDetails;
	}

	public void setKwDetails(CountryRelatedFields kwDetails) {
		this.kwDetails = kwDetails;
	}

	public CountryRelatedFields getAeDetails() {
		return aeDetails;
	}

	public void setAeDetails(CountryRelatedFields aeDetails) {
		this.aeDetails = aeDetails;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIntroText() {
		return introText;
	}

	public void setIntroText(String introText) {
		this.introText = introText;
	}

	@Override
    public String toString() {
        return "Sector{" +
                "isParentSector=" + isParentSector +
                ", parentSector='" + parentSector + '\'' +
                ", active=" + active +
                ", countryList=" + countryList + 
                ", kwDetails=" + kwDetails + 
                ", aeDetails=" + aeDetails + 
                ", date=" + date + 
                ", description='" + description + '\'' + 
                ", introText='" + introText + '\'' + 
                '}';
    }	
}
