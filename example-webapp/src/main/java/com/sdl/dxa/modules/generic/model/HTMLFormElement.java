package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import java.util.List;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.NameValuePair;

/**
 * HTMLFormElement
 *
 * @author Saurabh
 */

@SemanticEntity(entityName = "HTMLFormElement", vocabulary = SCHEMA_ORG, prefix = "hfe", public_ = true)
public class HTMLFormElement extends AbstractEntity {
	@SemanticProperty("hfe:label")
    private String label;

	@SemanticProperty("hfe:shortLabel")
    private String shortLabel;

	@SemanticProperty("hfe:mandatory")
    private String mandatory;

	@SemanticProperty("hfe:name")
	private String name;
    
	@SemanticProperty("hfe:type")
	private String type;
    
	@SemanticProperty("hfe:validationClass")
	private String validationClass;

	@SemanticProperty("hfe:value")
	private String value;
	
	@SemanticProperty("hfe:dropBoxCategoryId")
	private int dropBoxCategoryId;
    
	@SemanticProperty("hfe:optionValue")
    private List<KeyValuePair> optionValue;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getShortLabel() {
		return shortLabel;
	}

	public void setShortLabel(String shortLabel) {
		this.shortLabel = shortLabel;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValidationClass() {
		return validationClass;
	}

	public void setValidationClass(String validationClass) {
		this.validationClass = validationClass;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getDropBoxCategoryId() {
		return dropBoxCategoryId;
	}

	public void setDropBoxCategoryId(int dropBoxCategoryId) {
		this.dropBoxCategoryId = dropBoxCategoryId;
	}

	public List<KeyValuePair> getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(List<KeyValuePair> optionValue) {
		this.optionValue = optionValue;
	}
	
	@Override
    public String toString() {
        return "HTMLFormElement{" +
                "label='" + label + '\'' +
                ", shortLabel='" + shortLabel + '\'' +
                ", mandatory='" + mandatory + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", validationClass='" + validationClass + '\'' +
                ", value='" + value + '\'' +
                ", dropBoxCategoryId=" + dropBoxCategoryId +
                ", optionValue=" + optionValue +
                '}';
    }

}
