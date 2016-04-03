package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;


import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

/**
 * BrandList
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "BrandList", vocabulary = SCHEMA_ORG, prefix = "bl", public_ = true)
})
public class BrandList extends AbstractEntity {
	@SemanticProperty("bl:brandCode") 
    private int brandCode;
    
	@SemanticProperty("bl:brandName") 
    private String brandName;
	
	@SemanticProperty("bl:brandLogo1x") 
    private String brandLogo1x;

	@SemanticProperty("bl:brandLogo2x") 
    private String brandLogo2x;

	@SemanticProperty("bl:preferedBrand") 
    private String preferedBrand;
	
	public String getBrandName() {
		return brandName;
	}

	public int getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(int brandCode) {
		this.brandCode = brandCode;
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

	public String getPreferedBrand() {
		return preferedBrand;
	}

	public void setPreferedBrand(String preferedBrand) {
		this.preferedBrand = preferedBrand;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	@Override
    public String toString() {
        return "BrandList{" +
                "brandCode=" + brandCode +
                ", brandName='" + brandName + '\'' +
                ", brandLogo1x='" + brandLogo1x + '\'' +
                ", brandLogo2x='" + brandLogo2x + '\'' +
                ", preferedBrand='" + preferedBrand + '\'' +
                '}';
    }	
}
