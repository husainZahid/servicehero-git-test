package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Image;
import com.sdl.webapp.common.api.model.entity.Paragraph;
import org.joda.time.DateTime;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * Created by DELL on 9/16/2015.
 */
@SemanticEntities({
	@SemanticEntity(entityName = "Blog", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)  ,
	@SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class Blog  extends AbstractEntity {

	@SemanticProperty("s:headline")
    private String headline;

    @SemanticProperty("s:image")
    private Image image;

	@SemanticProperties({
			@SemanticProperty("s:dateCreated"),
			@SemanticProperty("m:dateCreated")
	})
    private DateTime date;

    @SemanticProperty("s:about")
    private String description;

    @SemanticProperty("s:blogBody")
    private List<Paragraph> blogBody;

	@SemanticProperties({
		@SemanticProperty("s:author") ,
		@SemanticProperty("m:author")
	})
	private String author;

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
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

	public List<Paragraph> getBlogBody() {
		return blogBody;
	}

	public void setBlogBody(List<Paragraph> blogBody) {
		this.blogBody = blogBody;
	}

	public String getAuthor() {	return author;	}

	public void setAuthor(String author) {	this.author = author;	}

	@Override
	public String toString() {
		return "Blog{" +
		       "headline='" + headline + '\'' +
		       ", image=" + image +
		       ", date=" + date +
		       ", description='" + description + '\'' +
		       ", blogBody=" + blogBody +
		       ", author='" + author + '\'' +
		       '}';
	}
}
