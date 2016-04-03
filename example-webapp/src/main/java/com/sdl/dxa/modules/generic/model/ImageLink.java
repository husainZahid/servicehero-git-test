package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
/* Created by Sudha on 8/31/2015.
 */
@SemanticEntity(entityName = "ImageLink", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class ImageLink extends AbstractEntity {

	@SemanticProperty("s:link")
    private Link link;

	@SemanticProperty("s:width")
	private String width;

	@SemanticProperty("s:height")
	private String height;

	@SemanticProperty("s:media")
	private MediaItem media;

	@SemanticProperty("s:altImage")
	private MediaItem altImage;

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public MediaItem getMedia() {
		return media;
	}

	public void setMedia(MediaItem media) {
		this.media = media;
	}


	public MediaItem getAltImage() {
		return altImage;
	}

	public void setAltImage(MediaItem altImage) {
		this.altImage = altImage;
	}

	@Override
	public String toString() {
		return "ImageLink{" +
		       "link=" + link +
		       ", width='" + width + '\'' +
		       ", height='" + height + '\'' +
		       ", media=" + media +
		       ", altImage=" + altImage +
		       '}';
	}
}
