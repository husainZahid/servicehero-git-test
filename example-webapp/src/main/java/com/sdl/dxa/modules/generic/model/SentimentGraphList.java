package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
/**
 * Created by Sudha on 2/24/2016.
 */

@SemanticEntities({
	@SemanticEntity(entityName = "SentimentGraphList", vocabulary = SCHEMA_ORG, prefix = "uc", public_ = true)
})
public class SentimentGraphList extends AbstractEntity {

	@SemanticProperty("uc:sentiment")
    private String sentiment;

	@SemanticProperty("uc:cntSentiment")
    private int cntSentiment;

	@SemanticProperty("uc:surveydt")
	private String surveydt;

	@SemanticProperty("uc:surveydtsort")
	private String surveydtsort;

	@SemanticProperty("uc:formatedSurveydt")
	private String formatedSurveydt;

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	public int getCntSentiment() {
		return cntSentiment;
	}

	public void setCntSentiment(int cntSentiment) {
		this.cntSentiment = cntSentiment;
	}

	public String getSurveydt() {
		return surveydt;
	}

	public void setSurveydt(String surveydt) {
		this.surveydt = surveydt;
	}

	public String getSurveydtsort() {
		return surveydtsort;
	}

	public void setSurveydtsort(String surveydtsort) {
		this.surveydtsort = surveydtsort;
	}

	public String getFormatedSurveydt() {
		return formatedSurveydt;
	}

	public void setFormatedSurveydt(String formatedSurveydt) {
		this.formatedSurveydt = formatedSurveydt;
	}

	@Override
	public String toString() {
		return "SentimentGraphList{" +
		       "sentiment=" + sentiment +
		       ", cntSentiment=" + cntSentiment +
		       ", surveydt='" + surveydt + '\'' +
		       ", surveydtsort='" + surveydtsort + '\'' +
		       ", formatedSurveydt='" + formatedSurveydt + '\'' +
		       '}';
	}
}
