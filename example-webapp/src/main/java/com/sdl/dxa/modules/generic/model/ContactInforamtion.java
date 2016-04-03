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

@SemanticEntity(entityName = "ContactInformation", vocabulary = SCHEMA_ORG, prefix = "ci", public_ = true)
public class ContactInforamtion extends AbstractEntity {
	@SemanticProperty("ci:postalAddress")
    private List<String> postalAddress;
    
	@SemanticProperty("ci:streetAddress")
    private List<String> streetAddress;
	
	@SemanticProperty("ci:telephone")
    private List<NameValuePair> telephone;
	
	@SemanticProperty("ci:fax")
    private List<NameValuePair> fax;
	
	@SemanticProperty("ci:website")
    private String website;

	@SemanticProperty("ci:emailAddress")
    private List<NameValuePair> emailAddress;
	
	public List<String> getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(List<String> postalAddress) {
		this.postalAddress = postalAddress;
	}

	public List<String> getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(List<String> streetAddress) {
		this.streetAddress = streetAddress;
	}

	public List<NameValuePair> getTelephone() {
		return telephone;
	}

	public void setTelephone(List<NameValuePair> telephone) {
		this.telephone = telephone;
	}

	public List<NameValuePair> getFax() {
		return fax;
	}

	public void setFax(List<NameValuePair> fax) {
		this.fax = fax;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public List<NameValuePair> getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(List<NameValuePair> emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
    public String toString() {
        return "ContactInformation{" +
                "postalAddress=" + postalAddress +
                ", streetAddress=" + streetAddress +
                ", telephone=" + telephone +
                ", fax=" + fax +
                ", website='" + website + '\'' +
                ", emailAddress=" + emailAddress +
                '}';
    }

}
