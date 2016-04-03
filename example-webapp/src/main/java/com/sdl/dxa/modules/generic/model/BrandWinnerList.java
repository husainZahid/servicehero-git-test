package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/14/2015.
 */
@SemanticEntities({
	@SemanticEntity(entityName = "BrandWinnerList", vocabulary = SCHEMA_ORG, prefix = "bw", public_ = true)
})

public class BrandWinnerList {
	@SemanticProperty("bw:brandCode")
	private String brandCode      ;

	@SemanticProperty("bw:brandName")
	private String brandName      ;

	@SemanticProperty("bw:brandLogo1x")
	private String brandLogo1x      ;

	@SemanticProperty("bw:brandLogo2x")
	private String brandLogo2x      ;

	@SemanticProperty("bw:brandAwards")
	private List<BrandAwards>  brandAwards  ;

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandLogo1x() {
		return brandLogo1x;
	}

	public void setBrandLogo1x(String brandLogo1x) {
		this.brandLogo1x = brandLogo1x;
	}

	public String getBrandLogo2x() {
		return brandLogo2x;
	}

	public void setBrandLogo2x(String brandLogo2x) {
		this.brandLogo2x = brandLogo2x;
	}

	public List<BrandAwards> getBrandAwards() {
		return brandAwards;
	}

	public void setBrandAwards(List<BrandAwards> brandAwards) {
		this.brandAwards = brandAwards;
	}

	@Override
	public String toString() {
		return "BrandWinnerList{" +
		       "brandCode='" + brandCode + '\'' +
		       ", brandName='" + brandName + '\'' +
		       ", brandLogo1x='" + brandLogo1x + '\'' +
		       ", brandLogo2x='" + brandLogo2x + '\'' +
		       ", brandAwards=" + brandAwards +
		       '}';
	}
}
