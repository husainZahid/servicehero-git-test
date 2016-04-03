package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * @author saurabh
 *
 */
@SemanticEntity(entityName = "CodeBlock", vocabulary = SCHEMA_ORG, prefix = "cb", public_ = true)
public class CodeBlock extends AbstractEntity {
	@SemanticProperty("cb:code")
    private String code;

	@SemanticProperty("cb:country")
	private String country;

    
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "CodeBlock{" +
		      " country='" + country + '\'' +
		       ", code='" + code + '\'' +

		       '}';
	}
}
