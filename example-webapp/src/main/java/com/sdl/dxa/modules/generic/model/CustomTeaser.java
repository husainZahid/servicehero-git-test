package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Link;
import org.joda.time.DateTime;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
    @SemanticEntity(entityName = "Country", vocabulary = SCHEMA_ORG, prefix = "c"),
    @SemanticEntity(entityName = "Blog", vocabulary = SCHEMA_ORG, prefix = "b"),
    @SemanticEntity(entityName = "Sector", vocabulary = SCHEMA_ORG, prefix = "s"),
    @SemanticEntity(entityName = "Promotion", vocabulary = SDL_CORE, prefix = "p"),
	@SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class CustomTeaser extends AbstractEntity {
	private String tcmId;
	@SemanticProperties({

	})
	private Link link;

	@SemanticProperties({
        @SemanticProperty("c:name"),
        @SemanticProperty("b:name"),
        @SemanticProperty("s:name"),
        @SemanticProperty("p:name"),
        @SemanticProperty("m:name")
	})
	private String title;
	
	@SemanticProperties({
        @SemanticProperty("c:shortName"),
        @SemanticProperty("p:shortName"),
		@SemanticProperty("b:introText"),
		@SemanticProperty("s:introText")
	})
	private String description;

	private String svgImage;

	private String normalImage;
	@SemanticProperties({
	    @SemanticProperty("c:caption"),
	    @SemanticProperty("p:caption"),
		@SemanticProperty("b:caption")
	})
	private String caption;


	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@SemanticProperties({
        @SemanticProperty("c:dateCreated"),
        @SemanticProperty("b:dateCreated"),
        @SemanticProperty("s:dateCreated")
	})
	private DateTime date;

	@SemanticProperties({
        @SemanticProperty("b:author"),
        @SemanticProperty("s:author")
	})
	private String author;

	private CountryRelatedFields countryFields;

	public String getTcmId() {
		return tcmId;
	}

	public void setTcmId(String tcmId) {
		this.tcmId = tcmId;
	}

	public Link getLink() {
	    return link;
	}

	public void setLink(Link link) {
	    this.link = link;
	}


	public String getSvgImage() {
		return svgImage;
	}

	public void setSvgImage(String svgImage) {
		this.svgImage = svgImage;
	}

	public String getNormalImage() {
		return normalImage;
	}

	public void setNormalImage(String normalImage) {
		this.normalImage = normalImage;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {this.date = date;}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {this.author = author;	}
	

	public CountryRelatedFields getCountryFields() {
		return countryFields;
	}

	public void setCountryFields(CountryRelatedFields countryFields) {
		this.countryFields = countryFields;
	}

	@Override
	public String toString() {
		return "CustomTeaser{" +
		       "tcmId='" + tcmId + '\'' +
		       ", link=" + link +
		       ", title='" + title + '\'' +
		       ", description='" + description + '\'' +
		       ", caption='" + caption + '\'' +
		       ", svgImage='" + svgImage + '\'' +
		       ", normalImage='" + normalImage + '\'' +
		       ", date=" + date +
		       ", author='" + author + '\'' +
		       ", countryFields=" + countryFields +
		       '}';
	}
}
