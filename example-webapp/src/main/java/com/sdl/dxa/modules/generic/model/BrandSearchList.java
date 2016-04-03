package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by Sudha on 11/24/2015.
 */
@SemanticEntities({
	@SemanticEntity(entityName = "BrandSearchList", vocabulary = SCHEMA_ORG, prefix = "bl", public_ = true)
})

public class BrandSearchList  extends AbstractEntity {
	@SemanticProperty("bl:brandDtl")
	private List<BrandSearch> brandDtl;

	public List<BrandSearch> getBrandDtl() {
		return brandDtl;
	}

	public void setBrandDtl(List<BrandSearch> brandDtl) {
		this.brandDtl = brandDtl;
	}

	@Override
	public String toString() {
		return "BrandSearchList{" +
		       "brandDtl=" + brandDtl +
		       '}';
	}
}
