package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.Paragraph;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * Created by DELL on 11/12/2015.
 */
@SemanticEntities({
	@SemanticEntity(entityName = "Promotion", vocabulary = SCHEMA_ORG, prefix = "p", public_ = true)  ,
	@SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class Promotion   extends AbstractEntity {

	@SemanticProperty("p:headline")
    private String headline;

	@SemanticProperty("p:multimedia")
    private Link link;

	@SemanticProperty("p:content")
    private List<Paragraph> content;

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public List<Paragraph> getContent() {
		return content;
	}

	public void setContent(List<Paragraph> content) {
		this.content = content;
	}

	@Override
		public String toString() {
			return "Promotion{" +
			       "headline='" + headline + '\'' +
			       ", link=" + link +
			       ", content=" + content +
			       '}';
		}

}
