package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * Created by Sudha on 11/24/2015.
 */
@SemanticEntities({
	@SemanticEntity(entityName = "BrandSearch", vocabulary = SCHEMA_ORG, prefix = "bl", public_ = true)
})
public class BrandSearch extends AbstractEntity {
	@SemanticProperty("bl:brandCode")
		private String brandCode;

		@SemanticProperty("bl:brandName")
		private String brandName;

		@SemanticProperty("bl:brandLogo1x")
	    private String brandLogo1x;

		@SemanticProperty("bl:brandLogo2x")
	    private String brandLogo2x;

		@SemanticProperty("bl:sectorCode")
		private String sectorCode;

		@SemanticProperty("bl:sectorImage")
		private String sectorImage;

		@SemanticProperty("bl:sectorName")
		private String sectorName;

		@SemanticProperty("bl:parentSectorCode")
		private String parentSectorCode;

		@SemanticProperty("bl:parentSectorName")
		private String parentSectorName;

		@SemanticProperty("bl:countryCode")
		private String countryCode;

		@SemanticProperty("bl:preferedBrand")
		private String preferedBrand;
		
		@SemanticProperty("bl:dealerName")
		private String dealerName;
		
		@SemanticProperty("bl:relatedBrands")
		private String relatedBrands;

		@SemanticProperty("bl:dealerCityName")
		private String dealerCityName;

		@SemanticProperty("bl:comments") 
	    private int comments;
		
		@SemanticProperty("bl:nominations") 
	    private int nominations;
		
		@SemanticProperty("bl:wins") 
	    private int wins;
		
		@SemanticProperty("bl:trends") 
	    private int trends;
		
		@SemanticProperty("bl:starRating") 
	    private String starRating;

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

		public String getSectorCode() {
			return sectorCode;
		}

		public void setSectorCode(String sectorCode) {
			this.sectorCode = sectorCode;
		}

		public String getParentSectorCode() {
			return parentSectorCode;
		}

		public void setParentSectorCode(String parentSectorCode) {
			this.parentSectorCode = parentSectorCode;
		}

		public String getParentSectorName() {
			return parentSectorName;
		}

		public void setParentSectorName(String parentSectorName) {
			this.parentSectorName = parentSectorName;
		}

		public String getCountryCode() {
			return countryCode;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public String getPreferedBrand() {
			return preferedBrand;
		}

		public void setPreferedBrand(String preferedBrand) {
			this.preferedBrand = preferedBrand;
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
		
		public String getDealerName() {
			return dealerName;
		}

		public void setDealerName(String dealerName) {
			this.dealerName = dealerName;
		}

		public String getDealerCityName() {
			return dealerCityName;
		}

		public void setDealerCityName(String dealerCityName) {
			this.dealerCityName = dealerCityName;
		}
		
		public String getRelatedBrands() {
			return relatedBrands;
		}

		public void setRelatedBrands(String relatedBrands) {
			this.relatedBrands = relatedBrands;
		}

		public int getComments() {
			return comments;
		}

		public void setComments(int comments) {
			this.comments = comments;
		}

		public int getNominations() {
			return nominations;
		}

		public void setNominations(int nominations) {
			this.nominations = nominations;
		}

		public int getWins() {
			return wins;
		}

		public void setWins(int wins) {
			this.wins = wins;
		}

		public int getTrends() {
			return trends;
		}

		public void setTrends(int trends) {
			this.trends = trends;
		}

		public String getStarRating() {
			return starRating;
		}

		public void setStarRating(String starRating) {
			this.starRating = starRating;
		}

		@Override
		public String toString() {
			return "BrandSearch{" +
			       "brandCode='" + brandCode + '\'' +
			       ", brandName='" + brandName + '\'' +
			       ", brandLogo1x='" + brandLogo1x + '\'' +
			       ", brandLogo2x='" + brandLogo2x + '\'' +
			       ", sectorCode='" + sectorCode + '\'' +
			       ", parentSectorCode='" + parentSectorCode + '\'' +
			       ", countryCode='" + countryCode + '\'' +
			       ", preferedBrand='" + preferedBrand + '\'' +
			       ", sectorName='" + sectorName + '\'' +
			       ", sectorImage='" + sectorImage + '\'' +
			       ", parentSectorName='" + sectorName + '\'' +
			       ", dealerName='" + dealerName + '\'' +
			       ", dealerCityName='" + dealerCityName + '\'' +
			       ", relatedBrands='" + relatedBrands + '\'' +
			       ", comments=" + comments +
	               ", nominations=" + nominations +
	               ", wins=" + wins +
	               ", trends=" + trends +
	               ", starRating='" + starRating + '\'' +
			       '}';
		}
}
