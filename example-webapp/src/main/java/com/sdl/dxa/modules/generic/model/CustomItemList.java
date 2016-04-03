package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Teaser;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/27/2015.
 */
@SemanticEntity(entityName = "CustomItemList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class CustomItemList  extends AbstractEntity {
	@SemanticProperty("s:headline")
    private String headline;

    @SemanticProperty("s:itemListElement")
    private List<Teaser> itemListElements;

	@SemanticProperty("s:country")
	private String country;


	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<Teaser> getItemListElements() {
        return itemListElements;
    }


    public void setItemListElements(List<Teaser> itemListElements) {
        this.itemListElements = itemListElements;
    }

	@Override
	public String toString() {
		return "CustomItemList{" +
		       "headline='" + headline + '\'' +
		       ", itemListElements=" + itemListElements +
		       ", country='" + country + '\'' +
		       '}';
	}
}
