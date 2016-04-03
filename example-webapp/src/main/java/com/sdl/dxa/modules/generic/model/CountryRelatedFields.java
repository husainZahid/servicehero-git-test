package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

import java.util.List;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.NameValuePair;
import com.sdl.webapp.common.api.model.entity.Paragraph;

/**
 * CountryRelatedFields
 *
 * @author Saurabh
 */

@SemanticEntity(entityName = "CountryRelatedFields", vocabulary = SCHEMA_ORG, prefix = "crf", public_ = true)
public class CountryRelatedFields extends AbstractEntity {
	@SemanticProperty("crf:title")
	private String title;
    
	@SemanticProperty("crf:introduction")
    private String introduction;

	@SemanticProperty("crf:svgImageCombo")
    private SvgImageCombo svgImageCombo;
	
	@SemanticProperty("crf:quota")
	private int quota;
    
	@SemanticProperty("crf:frequency")
	private String frequency;

	@SemanticProperty("crf:information")
	private List<Paragraph> information;

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

	public SvgImageCombo getSvgImageCombo() {
		return svgImageCombo;
	}

	public void setSvgImageCombo(SvgImageCombo svgImageCombo) {
		this.svgImageCombo = svgImageCombo;
	}

	public int getQuota() {
		return quota;
	}

	public void setQuota(int quota) {
		this.quota = quota;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public List<Paragraph> getInformation() {
		return information;
	}

	public void setInformation(List<Paragraph> information) {
		this.information = information;
	}

	@Override
    public String toString() {
        return "CountryRelatedFields{" +
                "title='" + title + '\'' +
                ", introduction='" + introduction + '\'' +
                ", svgImageCombo=" + svgImageCombo +
                ", quota=" + quota +
                ", frequency='" + frequency + '\'' +
                ", information=" + information +
                '}';
    }
}
