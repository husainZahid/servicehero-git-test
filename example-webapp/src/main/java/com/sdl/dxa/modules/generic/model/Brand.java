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
 * Brand
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "Brand", vocabulary = SCHEMA_ORG, prefix = "b", public_ = true),
	@SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class Brand extends AbstractEntity {
	@SemanticProperty("b:fullName") 
    private String fullName;
    
	@SemanticProperty("b:contactInfo")
    private ContactInforamtion contactInfo;

	@SemanticProperty("b:mapLocation")
    private MapLocation mapLocation;

	@SemanticProperty("b:logoImage")
    private LogoImage logoImage;

	@SemanticProperty("b:sector")
    private Sector sector;

	@SemanticProperty("b:priority")
    private Tag priority;

	@SemanticProperty("b:active")
    private Tag active;
	
	@SemanticProperty("b:shortcutName")
    private String shortcutName;

	@SemanticProperty("b:shortName")
    private List<String> shortName;

	@SemanticProperty("b:countryList")
    private List<Tag> countryList;

	@SemanticProperty("b:kwDetails")
    private CountryRelatedBrandFields kwDetails;

    @SemanticProperty("b:aeDetails")
    private CountryRelatedBrandFields aeDetails;
    
    @SemanticProperties({
		@SemanticProperty("b:dateCreated"),
		@SemanticProperty("m:dateCreated")
	})
    private DateTime date;

	@SemanticProperties({
		@SemanticProperty("b:description"),
		@SemanticProperty("m:description")
	})
    private String description;

	@SemanticProperties({
		@SemanticProperty("b:introText"),
		@SemanticProperty("m:introText")
	})
    private String introText;
    
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public ContactInforamtion getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInforamtion contactInfo) {
		this.contactInfo = contactInfo;
	}

	public MapLocation getMapLocation() {
		return mapLocation;
	}

	public void setMapLocation(MapLocation mapLocation) {
		this.mapLocation = mapLocation;
	}

	public LogoImage getLogoImage() {
		return logoImage;
	}

	public void setLogoImage(LogoImage logoImage) {
		this.logoImage = logoImage;
	}

	public Sector getSector() {
		return sector;
	}

	public void setSector(Sector sector) {
		this.sector = sector;
	}

	public Tag getPriority() {
		return priority;
	}

	public void setPriority(Tag priority) {
		this.priority = priority;
	}

	public Tag getActive() {
		return active;
	}

	public void setActive(Tag active) {
		this.active = active;
	}
	
	public String getShortcutName() {
		return shortcutName;
	}

	public void setShortcutName(String shortcutName) {
		this.shortcutName = shortcutName;
	}

	public List<String> getShortName() {
		return shortName;
	}

	public void setShortName(List<String> shortName) {
		this.shortName = shortName;
	}

	public List<Tag> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<Tag> countryList) {
		this.countryList = countryList;
	}

	public CountryRelatedBrandFields getKwDetails() {
		return kwDetails;
	}

	public void setKwDetails(CountryRelatedBrandFields kwDetails) {
		this.kwDetails = kwDetails;
	}

	public CountryRelatedBrandFields getAeDetails() {
		return aeDetails;
	}

	public void setAeDetails(CountryRelatedBrandFields aeDetails) {
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
        return "Brand{" +
                "fullName='" + fullName + '\'' +
                ", contactInfo=" + contactInfo +
                ", mapLocation=" + mapLocation +
                ", logoImage=" + logoImage +
                ", sector=" + sector +
                ", priority=" + priority +
                ", active=" + active +
                ", shortcutName='" + shortcutName + '\'' +
                ", shortName=" + shortName +
                ", countryList=" + countryList +
                ", kwDetails=" + kwDetails +
                ", aeDetails=" + aeDetails +
                ", date=" + date + 
                ", description='" + description + '\'' + 
                ", introText='" + introText + '\'' + 
                '}';
    }	
}
