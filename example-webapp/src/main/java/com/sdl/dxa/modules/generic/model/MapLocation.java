package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import java.util.List;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Tag;

/**
 * HTMLFormElement
 *
 * @author Saurabh
 */

@SemanticEntities({
    @SemanticEntity(entityName = "GeoCoordinates", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
    @SemanticEntity(entityName = "MapLocation", vocabulary = SCHEMA_ORG, prefix = "ml", public_ = true)
})
public class MapLocation extends AbstractEntity {
	@SemanticProperty("ml:streetAddress")
    private List<String> streetAddress;
    
	@SemanticProperty("ml:areaName")
    private Tag areaName;
	
	
	@SemanticProperties({
        @SemanticProperty("s:latitude"),
        @SemanticProperty("ml:latitude")
	})
	private double latitude;

	@SemanticProperties({
        @SemanticProperty("s:longitude"),
        @SemanticProperty("ml:longitude")
	})
	private double longitude;

	@SemanticProperty("ml:query")
	private String query;

	public List<String> getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(List<String> streetAddress) {
		this.streetAddress = streetAddress;
	}

	public Tag getAreaName() {
		return areaName;
	}

	public void setAreaName(Tag areaName) {
		this.areaName = areaName;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
    public String toString() {
        return "ContactInformation{" +
                "streetAddress=" + streetAddress +
                ", areaName=" + areaName +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", query='" + query + '\'' +
                '}';
    }

}
