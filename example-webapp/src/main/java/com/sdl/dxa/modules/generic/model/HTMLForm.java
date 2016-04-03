package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import java.util.List;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Tag;

@SemanticEntity(entityName = "HTMLForm", vocabulary = SCHEMA_ORG, prefix = "hf", public_ = true)
public class HTMLForm extends AbstractEntity {
	@SemanticProperty("hf:formTitle")
    private String formTitle;

	@SemanticProperty("hf:name")
    private String name;
    
    @SemanticProperty("hf:submitUrl")
    private String submitUrl;

    @SemanticProperty("hf:method")
    private String method;

    @SemanticProperty("hf:formElement")
    private List<HTMLFormElement> formElement;

    @SemanticProperty("hf:countryCode")
    private Tag countryCode;

    @SemanticProperty("hf:sectorName")
    private Tag sectorName;

    public String getFormTitle() {
		return formTitle;
	}

	public void setFormTitle(String formTitle) {
		this.formTitle = formTitle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<HTMLFormElement> getFormElement() {
		return formElement;
	}

	public void setFormElement(List<HTMLFormElement> formElement) {
		this.formElement = formElement;
	}

	public Tag getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Tag countryCode) {
		this.countryCode = countryCode;
	}

	public Tag getSectorName() {
		return sectorName;
	}

	public void setSectorName(Tag sectorName) {
		this.sectorName = sectorName;
	}

	@Override
    public String toString() {
        return "HTMLForm{" +
                "formTitle='" + formTitle + '\'' +
                ", name='" + name + '\'' +
                ", submitUrl='" + submitUrl + '\'' +
                ", method='" + method + '\'' +
                ", formElement=" + formElement +
                ", countryCode=" + countryCode +
                ", sectorName=" + sectorName +
                '}';
    }	
}
