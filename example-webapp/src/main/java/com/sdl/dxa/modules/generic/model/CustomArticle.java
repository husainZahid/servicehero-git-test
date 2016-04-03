package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Image;
import com.sdl.webapp.common.api.model.entity.Paragraph;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/14/2015.
 */
@SemanticEntities({
	@SemanticEntity(entityName = "CustomArticle", vocabulary = SCHEMA_ORG, prefix = "uc", public_ = true)
})

public class CustomArticle extends AbstractEntity {
	@SemanticProperty("uc:title")
	private String title   ;

	@SemanticProperty("uc:subtitle")
	private String subtitle   ;

	@SemanticProperty("uc:image")
    private Image image;

	@SemanticProperty("uc:country")
	private String country;

	@SemanticProperty("uc:articleBody")
    private List<Paragraph> articleBody;

	@SemanticProperty("uc:svgImageCombo")
	private SvgImageCombo svgimagecombo;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List<Paragraph> getArticleBody() {
		return articleBody;
	}

	public void setArticleBody(List<Paragraph> articleBody) {
		this.articleBody = articleBody;
	}

	public SvgImageCombo getSvgimagecombo() {
		return svgimagecombo;
	}

	public void setSvgimagecombo(SvgImageCombo svgimagecombo) {
		this.svgimagecombo = svgimagecombo;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "CustomArticle{" +
		       "title='" + title + '\'' +
		       ", subtitle='" + subtitle + '\'' +
		       ", image=" + image +
		       ", country='" + country + '\'' +
		       ", articleBody=" + articleBody +
		       ", svgimagecombo=" + svgimagecombo +
		       '}';
	}
}
