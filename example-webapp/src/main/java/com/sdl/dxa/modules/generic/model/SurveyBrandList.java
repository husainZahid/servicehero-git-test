package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * SurveyBrandList
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "SurveyBrandList", vocabulary = SCHEMA_ORG, prefix = "sbl", public_ = true)
})
public class SurveyBrandList extends AbstractEntity {
	@SemanticProperty("sbl:sectorCode") 
    private int sectorCode;
    
	@SemanticProperty("sbl:sectorName") 
    private String sectorName;
	
	@SemanticProperty("sbl:sectorImage") 
    private String sectorImage;
	
	@SemanticProperty("sbl:brandList")
    private List<BrandList> brandList;
	
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

	public String getSectorImage() {
		return sectorImage;
	}

	public void setSectorImage(String sectorImage) {
		this.sectorImage = sectorImage;
	}

	public List<BrandList> getBrandList() {
		return brandList;
	}

	public void setBrandList(List<BrandList> brandList) {
		this.brandList = brandList;
	}

	@Override
    public String toString() {
        return "SurveyBrandList{" +
                "sectorCode=" + sectorCode +
                ", sectorName='" + sectorName + '\'' +
                ", sectorImage='" + sectorImage + '\'' +
                ", brandList=" + brandList +
                '}';
    }	
}
