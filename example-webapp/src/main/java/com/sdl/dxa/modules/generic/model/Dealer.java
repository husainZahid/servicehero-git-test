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
import com.sdl.webapp.common.api.model.entity.Paragraph;
import com.sdl.webapp.common.api.model.entity.Tag;

@SemanticEntities({
	@SemanticEntity(entityName = "Dealer", vocabulary = SCHEMA_ORG, prefix = "d", public_ = true),
	@SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class Dealer extends AbstractEntity {
	@SemanticProperty("d:fullName")
    private String fullName;
    
	@SemanticProperty("d:information")
    private List<Paragraph> information;
   
	@SemanticProperty("d:contactInfo")
    private ContactInforamtion contactInfo;
	
	@SemanticProperty("d:mapLocation")
    private MapLocation mapLocation;

	@SemanticProperty("d:countryName")
	private List<Tag> countryName;
	
	@SemanticProperty("d:cityName")
	private List<Tag> cityName;
	
	@SemanticProperty("d:corporateGroupName")
    private CorporateGroup corporateGroupName;

	@SemanticProperty("d:logoImage")
    private LogoImage logoImage;
	
	@SemanticProperty("d:active")
    private String active;

	@SemanticProperties({
		@SemanticProperty("d:dateCreated"),
		@SemanticProperty("m:dateCreated")
	})
    private DateTime date;

	@SemanticProperties({
		@SemanticProperty("d:description"),
		@SemanticProperty("m:description")
	})
    private String description;

	@SemanticProperties({
		@SemanticProperty("d:introText"),
		@SemanticProperty("m:introText")
	})
    private String introText;
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public List<Paragraph> getInformation() {
		return information;
	}

	public void setInformation(List<Paragraph> information) {
		this.information = information;
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
	
	public List<Tag> getCountryName() {
		return countryName;
	}

	public void setCountryName(List<Tag> countryName) {
		this.countryName = countryName;
	}

	public List<Tag> getCityName() {
		return cityName;
	}

	public void setCityName(List<Tag> cityName) {
		this.cityName = cityName;
	}

	public CorporateGroup getCorporateGroupName() {
		return corporateGroupName;
	}

	public void setCorporateGroupName(CorporateGroup corporateGroupName) {
		this.corporateGroupName = corporateGroupName;
	}

	public LogoImage getLogoImage() {
		return logoImage;
	}

	public void setLogoImage(LogoImage logoImage) {
		this.logoImage = logoImage;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
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
        return "Dealer{" +
                "fullName='" + fullName + '\'' +
                ", information=" + information +
                ", contactInfo=" + contactInfo +
                ", mapLocation=" + mapLocation +
                ", countryName=" + countryName +  
                ", cityName=" + cityName + 
                ", corporateGroupName=" + corporateGroupName +  
                ", logoImage=" + logoImage + 
                ", active='" + active + '\'' +
                ", date=" + date + 
                ", description='" + description + '\'' + 
                ", introText='" + introText + '\'' + 
                '}';
    }	
}
