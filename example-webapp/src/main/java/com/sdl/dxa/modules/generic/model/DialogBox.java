package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;






import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

import java.util.List;

import org.joda.time.DateTime;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.entity.Paragraph;

/**
 * @author saurabh
 *
 */
@SemanticEntities({
	@SemanticEntity(entityName = "DialogBox", vocabulary = SCHEMA_ORG, prefix = "db", public_ = true)
})
public class DialogBox extends AbstractEntity {
	@SemanticProperty("db:blockId")
    private String blockId;
    
	@SemanticProperty("db:heading")
    private String heading;

	@SemanticProperty("db:description")
    private String description;
	
	@SemanticProperty("db:optionButton") 
	private List<KeyValuePair> optionButton;
	
	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<KeyValuePair> getOptionButton() {
		return optionButton;
	}

	public void setOptionButton(List<KeyValuePair> optionButton) {
		this.optionButton = optionButton;
	}

	@Override
    public String toString() {
        return "DialogBox{" +
                " blockId='" + blockId + '\'' +
                ", heading='" + heading + '\'' +
                ", description='" + description + '\'' +
                ", optionButton=" + optionButton + 
                '}';
    }	
}
