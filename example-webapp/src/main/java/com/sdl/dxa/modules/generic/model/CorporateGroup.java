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

@SemanticEntities({
	@SemanticEntity(entityName = "CorporateGroup", vocabulary = SCHEMA_ORG, prefix = "cg", public_ = true),
	@SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class CorporateGroup extends AbstractEntity {
	@SemanticProperty("cg:groupName")
    private String groupName;
    
	@SemanticProperty("cg:information")
    private List<Paragraph> information;
   
	@SemanticProperty("cg:contactInfo")
    private ContactInforamtion contactInfo;
	
	@SemanticProperty("cg:mapLocation")
    private MapLocation mapLocation;

	@SemanticProperty("cg:countryName")
    private String countryName;

	@SemanticProperty("cg:cityName")
    private String cityName;

	@SemanticProperty("cg:logoImage")
    private LogoImage logoImage;

	@SemanticProperty("cg:active")
    private String active;

	@SemanticProperties({
		@SemanticProperty("cg:dateCreated"),
		@SemanticProperty("m:dateCreated")
	})
    private DateTime date;

	@SemanticProperties({
		@SemanticProperty("cg:description"),
		@SemanticProperty("m:description")
	})
    private String description;

	@SemanticProperties({
		@SemanticProperty("cg:introText"),
		@SemanticProperty("m:introText")
	})
    private String introText;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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
        return "CorporateGroup{" +
                "groupName='" + groupName + '\'' +
                ", information=" + information +
                ", contactInfo=" + contactInfo +
                ", mapLocation=" + mapLocation +
                ", countryName='" + countryName + '\'' + 
                ", cityName='" + cityName + '\'' + 
                ", logoImage=" + logoImage + 
                ", active='" + active + '\'' +
                ", date=" + date + 
                ", description='" + description + '\'' + 
                ", introText='" + introText + '\'' + 
                '}';
    }	
}
