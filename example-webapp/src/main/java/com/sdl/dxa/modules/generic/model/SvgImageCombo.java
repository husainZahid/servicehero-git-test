package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;

/**
 * SvgImageCombo
 *
 * @author Saurabh
 */

@SemanticEntity(entityName = "SvgImageCombo", vocabulary = SCHEMA_ORG, prefix = "sic", public_ = true)
public class SvgImageCombo extends AbstractEntity {
	@SemanticProperty("sic:caption")
    private String caption;
    
	@SemanticProperty("sic:svgImage")
    private MediaItem svgImage;

	@SemanticProperty("sic:normalImage")
	private MediaItem normalImage;
    
	@SemanticProperty("sic:width")
	private int width;
    
	@SemanticProperty("sic:height")
	private int height;
	
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public MediaItem getSvgImage() {
		return svgImage;
	}

	public void setSvgImage(MediaItem svgImage) {
		this.svgImage = svgImage;
	}

	public MediaItem getNormalImage() {
		return normalImage;
	}

	public void setNormalImage(MediaItem normalImage) {
		this.normalImage = normalImage;
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
        return "SvgImageCombo{" +
                "caption='" + caption + '\'' +
                ", svgImage=" + svgImage +
                ", normalImage=" + normalImage +
                ", width=" + width + 
                ", height=" + height + 
                '}';
    }

}
