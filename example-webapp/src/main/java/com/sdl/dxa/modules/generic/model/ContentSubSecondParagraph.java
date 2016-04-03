package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/21/2015.
 */
@SemanticEntity(entityName = "ContentSubSecondParagraph", vocabulary = SCHEMA_ORG, prefix = "x", public_ = true)
public class ContentSubSecondParagraph   extends AbstractEntity {

	@SemanticProperty("x:subsecheading")
	private String subsecheading;

	@SemanticProperty("x:subsecparagraph")
	private String subsecparagraph;

	public String getSubsecheading() {
		return subsecheading;
	}

	public void setSubsecheading(String subsecheading) {
		this.subsecheading = subsecheading;
	}

	public String getSubsecparagraph() {
		return subsecparagraph;
	}

	public void setSubsecparagraph(String subsecparagraph) {
		this.subsecparagraph = subsecparagraph;
	}

	@Override
	public String toString() {
		return "ContentSubSecondParagraph{" +
		       "subsecheading='" + subsecheading + '\'' +
		       ", subsecparagraph='" + subsecparagraph + '\'' +
		       '}';
	}
}
