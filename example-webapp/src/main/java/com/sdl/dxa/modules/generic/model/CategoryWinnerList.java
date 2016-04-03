package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by DELL on 12/14/2015.
 */
@SemanticEntities({
	@SemanticEntity(entityName = "CategoryWinnerList", vocabulary = SCHEMA_ORG, prefix = "cw", public_ = true)
})

public class CategoryWinnerList extends AbstractEntity {

	@SemanticProperty("cw:parentSectorCode")
	private int  parentSectorCode;

	@SemanticProperty("cw:parentSectorName")
	private String  parentSectorName;

	@SemanticProperty("cw:sectorCode")
	private int  sectorCode;

	@SemanticProperty("cw:sectorName")
	private String  sectorName;

	@SemanticProperty("cw:brandList")
	    private List<UserComment> brandList;

	public int getParentSectorCode() {
		return parentSectorCode;
	}

	public void setParentSectorCode(int parentSectorCode) {
		this.parentSectorCode = parentSectorCode;
	}

	public String getParentSectorName() {
		return parentSectorName;
	}

	public void setParentSectorName(String parentSectorName) {
		this.parentSectorName = parentSectorName;
	}

	public int getSectorCode() {
		return sectorCode;
	}

	public void setSectorCode(int sectorCode) {
		this.sectorCode = sectorCode;
	}

	public String getSectorName() {
		return sectorName;
	}

	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}

	public List<UserComment> getBrandList() {
		return brandList;
	}

	public void setBrandList(List<UserComment> brandList) {
		this.brandList = brandList;
	}

	@Override
	public String toString() {
		return "CategoryWinnerList{" +
		       "parentSectorCode=" + parentSectorCode +
		       ", parentSectorName='" + parentSectorName + '\'' +
		       ", sectorCode=" + sectorCode +
		       ", sectorName='" + sectorName + '\'' +
		       ", brandList=" + brandList +
		       '}';
	}
}
