package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Image;
import com.sdl.webapp.common.api.model.entity.Paragraph;
import org.joda.time.DateTime;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/27/2015.
 */
@SemanticEntity(entityName = "GenericArticle", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class GenericArticle extends AbstractEntity {


	    @SemanticProperty("s:headline")
	    private String headline;

	    @SemanticProperty("s:image")
	    private Image image;

	    @SemanticProperty("s:dateCreated")
	    private DateTime date;

	    @SemanticProperty("s:about")
	    private String description;

	    @SemanticProperty("s:articleBody")
	    private List<Paragraph> articleBody;

		@SemanticProperty("s:country")
		private String country;


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

	    public List<Paragraph> getArticleBody() {
	        return articleBody;
	    }

	    public void setArticleBody(List<Paragraph> articleBody) {
	        this.articleBody = articleBody;
	    }

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

	@Override
	public String toString() {
		return "GenericArticle{" +
		       "headline='" + headline + '\'' +
		       ", country='" + country + '\'' +
		       ", image=" + image +
		       ", date=" + date +
		       ", description='" + description + '\'' +
		       ", articleBody=" + articleBody +
		       '}';
	}
}
