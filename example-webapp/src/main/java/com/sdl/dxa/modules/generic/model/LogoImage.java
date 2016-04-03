package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;

/**
 * LogoImage
 *
 * @author Saurabh
 */

@SemanticEntity(entityName = "LogoImage", vocabulary = SCHEMA_ORG, prefix = "li", public_ = true)
public class LogoImage extends AbstractEntity {
	@SemanticProperty("li:caption") 
    private String caption;
    
	@SemanticProperty("li:logo1X") 
    private MediaItem logo1X;

	@SemanticProperty("li:logo2X") 
	private MediaItem logo2X;
    
	@SemanticProperty("li:width") 
	private int width;
    
	@SemanticProperty("li:height") 
	private int height;
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public MediaItem getLogo1X() {
		return logo1X;
	}

	public void setLogo1X(MediaItem logo1x) {
		logo1X = logo1x;
	}

	public MediaItem getLogo2X() {
		return logo2X;
	}

	public void setLogo2X(MediaItem logo2x) {
		logo2X = logo2x;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
    public String toString() {
        return "LogoImage{" +
                "caption='" + caption + '\'' +
                ", logo1X=" + logo1X +
                ", logo2X=" + logo2X +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
