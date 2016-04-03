package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Image;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/21/2015.
 */

@SemanticEntity(entityName = "GeneralPageContent", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class GeneralPageContent extends AbstractEntity {

	@SemanticProperty("s:title")
	private String title ;

	@SemanticProperty("s:introduction")
	private String introduction;

	@SemanticProperty("s:subtitle")
	private String  subtitle;

	@SemanticProperty("s:image")
	private Image image;

	@SemanticProperty("s:ContentParagraph")
	private List<ContentParagraph> pagecontent;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
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

	public List<ContentParagraph> getPagecontent() {
		return pagecontent;
	}

	public void setPagecontent(List<ContentParagraph> pagecontent) {
		this.pagecontent = pagecontent;
	}

	@Override
	public String toString() {
		return "GeneralPageContent{" +
		       "title='" + title + '\'' +
		       ", introduction='" + introduction + '\'' +
		       ", subtitle='" + subtitle + '\'' +
		       ", image=" + image +
		       ", pagecontent=" + pagecontent +
		       '}';
	}
}
