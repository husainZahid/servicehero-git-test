package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;

/**
 * AwardImage
 *
 * @author Saurabh
 */

@SemanticEntity(entityName = "AwardImage", vocabulary = SCHEMA_ORG, prefix = "ai", public_ = true)
public class AwardImage extends AbstractEntity {
	@SemanticProperty("ai:awardCaption") 
    private String awardCaption;
    
	@SemanticProperty("ai:award1X") 
    private MediaItem award1X;

	@SemanticProperty("ai:award2X") 
    private MediaItem award2X;
    
	@SemanticProperty("ai:awardWidth") 
	private int awardWidth;
    
	@SemanticProperty("ai:awardHeight") 
	private int awardHeight;

	public String getAwardCaption() {
		return awardCaption;
	}

	public void setAwardCaption(String awardCaption) {
		this.awardCaption = awardCaption;
	}

	public MediaItem getAward1X() {
		return award1X;
	}

	public void setAward1X(MediaItem award1x) {
		award1X = award1x;
	}

	public MediaItem getAward2X() {
		return award2X;
	}

	public void setAward2X(MediaItem award2x) {
		award2X = award2x;
	}

	public int getAwardWidth() {
		return awardWidth;
	}

	public void setAwardWidth(int awardWidth) {
		this.awardWidth = awardWidth;
	}

	public int getAwardHeight() {
		return awardHeight;
	}

	public void setAwardHeight(int awardHeight) {
		this.awardHeight = awardHeight;
	}
	
	@Override
    public String toString() {
        return "AwardImage{" +
                "awardCaption='" + awardCaption + '\'' +
                ", award1X=" + award1X +
                ", award2X=" + award2X +
                ", awardWidth=" + awardWidth +
                ", awardHeight=" + awardHeight +
                '}';
    }
}
