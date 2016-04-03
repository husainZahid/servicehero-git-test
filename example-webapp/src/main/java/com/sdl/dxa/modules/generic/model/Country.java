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
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.entity.Paragraph;

/**
 * @author saurabh
 *
 */
@SemanticEntities({
	@SemanticEntity(entityName = "Country", vocabulary = SCHEMA_ORG, prefix = "c", public_ = true),
	@SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class Country extends AbstractEntity {
	@SemanticProperty("c:fullNname")
    private String fullName;
    
	@SemanticProperty("c:information")
    private List<Paragraph> information;

	@SemanticProperty("c:shortName")
    private String shortName;

	@SemanticProperty("c:svgImageCombo")
    private SvgImageCombo svgImageCombo;

	@SemanticProperty("c:active")
    private String active;

	@SemanticProperties({
		@SemanticProperty("c:dateCreated"),
		@SemanticProperty("m:dateCreated")
	})
    private DateTime date;

	@SemanticProperties({
		@SemanticProperty("c:description"),
		@SemanticProperty("m:description")
	})
    private String description;

	@SemanticProperties({
		@SemanticProperty("c:introText"),
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public SvgImageCombo getSvgImageCombo() {
		return svgImageCombo;
	}

	public void setSvgImageCombo(SvgImageCombo svgImageCombo) {
		this.svgImageCombo = svgImageCombo;
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
        return "Country{" +
                " fullName='" + fullName + '\'' +
                ", information=" + information + 
                ", shortName='" + shortName + '\'' +
                ", svgImageCombo=" + svgImageCombo +
                ", active='" + active + '\'' +
                ", date=" + date + 
                ", description='" + description + '\'' + 
                ", introText='" + introText + '\'' + 
                '}';
    }	
}
