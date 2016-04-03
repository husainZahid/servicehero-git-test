package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * CommentList
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "CommentList", vocabulary = SCHEMA_ORG, prefix = "cl", public_ = true)
})
public class CommentList extends AbstractEntity {
	@SemanticProperty("cl:itemCode") 
    private int itemCode;
    
	@SemanticProperty("cl:itemName") 
    private String itemName;
	
	@SemanticProperty("cl:itemImage") 
    private String itemImage;
	
	@SemanticProperty("cl:itemImage2x") 
    private String itemImage2x;
	
	@SemanticProperty("cl:sectorCode") 
    private int sectorCode;
	
	@SemanticProperty("cl:sectorName") 
    private String sectorName;
	
	@SemanticProperty("cl:parentSectorName") 
    private String parentSectorName;
	
	@SemanticProperty("cl:dealerName") 
    private String dealerName;
	
	@SemanticProperty("cl:comments") 
    private int comments;
	
	@SemanticProperty("cl:nominations") 
    private int nominations;
	
	@SemanticProperty("cl:wins") 
    private int wins;
	
	@SemanticProperty("cl:trends") 
    private int trends;
	
	@SemanticProperty("cl:starRating") 
    private String starRating;
	
	@SemanticProperty("cl:awards") 
    private String awards;

	@SemanticProperty("cl:flagComments")
    private int flagComments;

	@SemanticProperty("cl:newComments")
	private int newComments;

	@SemanticProperty("cl:currentYearComments")
	private int currentYearComments;

	@SemanticProperty("cl:fbShare")
	private int fbShare;

	@SemanticProperty("cl:twShare")
	private int twShare;

	@SemanticProperty("cl:userComments")
    private List<UserComment> userComments;


	private List<KeyValuePair> objRating;
	private List<KeyValuePair> objResponse;
	private List<KeyValuePair> objStatus;
	private List<KeyValuePair> objSentiment;
	private List<KeyValuePair> objCommentsPerPage;

	private String selectedStarRatingFilter;
	private String selectedResponseFilter;
	private String selectedStatusFilter;
	private String selectedSentimentFilter;

	
	public int getItemCode() {
		return itemCode;
	}

	public void setItemCode(int itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemImage() {
		return itemImage;
	}

	public void setItemImage(String itemImage) {
		this.itemImage = itemImage;
	}

	public String getItemImage2x() {
		return itemImage2x;
	}

	public void setItemImage2x(String itemImage2x) {
		this.itemImage2x = itemImage2x;
	}

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

	public String getParentSectorName() {
		return parentSectorName;
	}

	public void setParentSectorName(String parentSectorName) {
		this.parentSectorName = parentSectorName;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
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

	public String getAwards() {
		return awards;
	}

	public void setAwards(String awards) {
		this.awards = awards;
	}

	public int getFlagComments() {
		return flagComments;
	}

	public void setFlagComments(int flagComments) {
		this.flagComments = flagComments;
	}

	public int getNewComments() {
		return newComments;
	}

	public void setNewComments(int newComments) {
		this.newComments = newComments;
	}

	public int getCurrentYearComments() {
		return currentYearComments;
	}

	public void setCurrentYearComments(int currentYearComments) {
		this.currentYearComments = currentYearComments;
	}

	public int getFbShare() {
		return fbShare;
	}

	public void setFbShare(int fbShare) {
		this.fbShare = fbShare;
	}

	public int getTwShare() {
		return twShare;
	}

	public void setTwShare(int twShare) {
		this.twShare = twShare;
	}

	public List<UserComment> getUserComments() {
		return userComments;
	}

	public void setUserComments(List<UserComment> userComments) {
		this.userComments = userComments;
	}

	public List<KeyValuePair> getObjRating() {
		return objRating;
	}

	public void setObjRating(List<KeyValuePair> objRating) {
		this.objRating = objRating;
	}

	public List<KeyValuePair> getObjResponse() {
		return objResponse;
	}

	public void setObjResponse(List<KeyValuePair> objResponse) {
		this.objResponse = objResponse;
	}

	public List<KeyValuePair> getObjStatus() {
		return objStatus;
	}

	public void setObjStatus(List<KeyValuePair> objStatus) {
		this.objStatus = objStatus;
	}

	public List<KeyValuePair> getObjSentiment() {
		return objSentiment;
	}

	public void setObjSentiment(List<KeyValuePair> objSentiment) {
		this.objSentiment = objSentiment;
	}

	public String getSelectedStarRatingFilter() {
		return selectedStarRatingFilter;
	}

	public void setSelectedStarRatingFilter(String selectedStarRatingFilter) {
		this.selectedStarRatingFilter = selectedStarRatingFilter;
	}

	public String getSelectedResponseFilter() {
		return selectedResponseFilter;
	}

	public void setSelectedResponseFilter(String selectedResponseFilter) {
		this.selectedResponseFilter = selectedResponseFilter;
	}

	public String getSelectedStatusFilter() {
		return selectedStatusFilter;
	}

	public void setSelectedStatusFilter(String selectedStatusFilter) {
		this.selectedStatusFilter = selectedStatusFilter;
	}

	public String getSelectedSentimentFilter() {
		return selectedSentimentFilter;
	}

	public void setSelectedSentimentFilter(String selectedSentimentFilter) {
		this.selectedSentimentFilter = selectedSentimentFilter;
	}

	public List<KeyValuePair> getObjCommentsPerPage() {
		return objCommentsPerPage;
	}

	public void setObjCommentsPerPage(List<KeyValuePair> objCommentsPerPage) {
		this.objCommentsPerPage = objCommentsPerPage;
	}

	@Override
	public String toString() {
		return "CommentList{" +
		       "itemCode=" + itemCode +
		       ", itemName='" + itemName + '\'' +
		       ", itemImage='" + itemImage + '\'' +
		       ", itemImage2x='" + itemImage2x + '\'' +
		       ", sectorCode=" + sectorCode +
		       ", sectorName='" + sectorName + '\'' +
		       ", parentSectorName='" + parentSectorName + '\'' +
		       ", dealerName='" + dealerName + '\'' +
		       ", comments=" + comments +
		       ", nominations=" + nominations +
		       ", wins=" + wins +
		       ", trends=" + trends +
		       ", starRating='" + starRating + '\'' +
		       ", awards='" + awards + '\'' +
		       ", flagComments=" + flagComments +
		       ", newComments=" + newComments +
		       ", currentYearComments=" + currentYearComments +
		       ", fbShare=" + fbShare +
		       ", twShare=" + twShare +
		       ", userComments=" + userComments +
		       ", objRating=" + objRating +
		       ", objResponse=" + objResponse +
		       ", objStatus=" + objStatus +
		       ", objSentiment=" + objSentiment +
		       ", objCommentsPerPage=" + objCommentsPerPage +
		       ", selectedStarRatingFilter='" + selectedStarRatingFilter + '\'' +
		       ", selectedResponseFilter='" + selectedResponseFilter + '\'' +
		       ", selectedStatusFilter='" + selectedStatusFilter + '\'' +
		       ", selectedSentimentFilter='" + selectedSentimentFilter + '\'' +
		       '}';
	}
}
