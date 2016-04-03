package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/21/2015.
 */
@SemanticEntity(entityName = "ContentParagraph", vocabulary = SCHEMA_ORG, prefix = "cp", public_ = true)
public class ContentParagraph  extends AbstractEntity {

	@SemanticProperty("cp:subheading")
	private String	subheading;

	@SemanticProperty("cp:paraimage")
	private MediaItem paraimage;

	@SemanticProperty("cp:para")
	private String para;

	@SemanticProperty("cp:subpara")
	private List<ContentSubParagraph>	subpara;

	public String getSubheading() {
		return subheading;
	}

	public void setSubheading(String subheading) {
		this.subheading = subheading;
	}

	public MediaItem getParaimage() {
		return paraimage;
	}

	public void setParaimage(MediaItem paraimage) {
		this.paraimage = paraimage;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	public List<ContentSubParagraph> getSubpara() {
		return subpara;
	}

	public void setSubpara(List<ContentSubParagraph> subpara) {
		this.subpara = subpara;
	}

	@Override
	public String toString() {
		return "ContentParagraph{" +
		       "subheading='" + subheading + '\'' +
		       ", paraimage=" + paraimage +
		       ", para='" + para + '\'' +
		       ", subpara=" + subpara +
		       '}';
	}
}
