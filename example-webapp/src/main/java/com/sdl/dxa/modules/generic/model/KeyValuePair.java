package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

/**
 * @author saurabh
 *
 */
@SemanticEntity(entityName = "KeyValuePair", vocabulary = SCHEMA_ORG, prefix = "kvp", public_ = true)
public class KeyValuePair extends AbstractEntity {
	@SemanticProperty("kvp:key")
    private String key;
    
	@SemanticProperty("kvp:value")
    private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
    public String toString() {
        return "KeyValuePair{" +
                " key='" + key + '\'' +
                " value='" + value + '\'' +
                '}';
    }	
}
