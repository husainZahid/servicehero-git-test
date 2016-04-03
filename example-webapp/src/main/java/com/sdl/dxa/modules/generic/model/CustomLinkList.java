package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Link;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 1/18/2016.
 */
@SemanticEntity(entityName = "CustomLinkList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class CustomLinkList extends AbstractEntity {

	@SemanticProperty("s:headline")
	private String headline;

	@SemanticProperty("s:links")
    private List<Link> links;

	@SemanticProperty("s:country")
	private String country;

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "CustomLinkList{" +
		       "headline='" + headline + '\'' +
		       ", links=" + links +
		       ", country='" + country + '\'' +
		       '}';
	}
}
