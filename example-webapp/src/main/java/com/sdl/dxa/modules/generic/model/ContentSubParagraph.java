package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/21/2015.
 */
@SemanticEntity(entityName = "ContentSubParagraph", vocabulary = SCHEMA_ORG, prefix = "p", public_ = true)
public class ContentSubParagraph   extends AbstractEntity
{
	@SemanticProperty("p:subheading")
	private String subheading;

	@SemanticProperty("p:subparagraph")
	private String subparagraph;

	@SemanticProperty("p:subsecondpara")
	private List<ContentSubSecondParagraph> subsecondpara;

	public String getSubheading() {
		return subheading;
	}

	public void setSubheading(String subheading) {
		this.subheading = subheading;
	}

	public String getSubparagraph() {
		return subparagraph;
	}

	public void setSubparagraph(String subparagraph) {
		this.subparagraph = subparagraph;
	}

	public List<ContentSubSecondParagraph> getSubsecondpara() {
		return subsecondpara;
	}

	public void setSubsecondpara(List<ContentSubSecondParagraph> subsecondpara) {
		this.subsecondpara = subsecondpara;
	}

	@Override
	public String toString() {
		return "ContentSubParagraph{" +
		       "subheading='" + subheading + '\'' +
		       ", subparagraph='" + subparagraph + '\'' +
		       ", subsecondpara=" + subsecondpara +
		       '}';
	}
}
