package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

/**
 * @author saurabh
 *
 */
@SemanticEntity(entityName = "PageTitle", vocabulary = SCHEMA_ORG, prefix = "pt", public_ = true)
public class PageTitle extends AbstractEntity {
	@SemanticProperty("pt:pageTitle")
    private String pageTitle;
    
	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	@Override
    public String toString() {
        return "PageTitle{" +
                " pageTitle='" + pageTitle + '\'' +
                '}';
    }	
}
