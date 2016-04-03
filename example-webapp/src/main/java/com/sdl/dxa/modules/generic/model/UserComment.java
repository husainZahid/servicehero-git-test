package com.sdl.dxa.modules.generic.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import org.joda.time.DateTime;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * UserComment
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "UserComment", vocabulary = SCHEMA_ORG, prefix = "uc", public_ = true)
})
public class UserComment extends AbstractEntity {


	@SemanticProperty("uc:commentId") 	
    private long commentId;

	@SemanticProperty("uc:shUserId") 	
    private long shUserId;

	@SemanticProperty("uc:shUserName") 	
    private String shUserName;

	@SemanticProperty("uc:brandCode") 	
    private long brandCode;

	@SemanticProperty("uc:brandName") 	
    private String brandName;
	
	@SemanticProperty("uc:logo1x") 	
    private String logo1x;
	
	@SemanticProperty("uc:logo2x") 	
    private String logo2x;
	
	@SemanticProperty("uc:countryCode") 	
    private String countryCode;
	
	@SemanticProperty("uc:languageCode") 	
    private String languageCode;

	@SemanticProperty("uc:comment") 
    private String comment;
    
	@SemanticProperty("uc:submitDate") 
    private DateTime submitDate;

	@SemanticProperty("uc:ratingCode") 
    private int ratingCode;

	@SemanticProperty("uc:starRating") 
    private String starRating;
	
	@SemanticProperty("uc:agreeCount") 
    private int agreeCount;
	
	@SemanticProperty("uc:disagreeCount") 
    private int disagreeCount;
	
	@SemanticProperty("uc:fbShareCount") 
    private int fbShareCount;
	
	@SemanticProperty("uc:twShareCount") 
    private int twShareCount;

	@SemanticProperty("uc:approved") 
    private String approved;

	private String lockStatus;
	private String commentStatus;

	private long lockByBrandUserId;

	private String lockByUserName;

	private DateTime lockDate;

	private long parentCommentId;

	private String brandResponseStatus;

	private String hasSubComment;

	private String hasResponseComment;

	private String commentEdited;

	private String commentActionTaken;

	@SemanticProperty("uc:sectorCode")
	private String sectorCode;

	@SemanticProperty("uc:dealerName")
	private String dealerName;

	public long getCommentId() {
		return commentId;
	}

	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}

	public long getShUserId() {
		return shUserId;
	}

	public void setShUserId(long shUserId) {
		this.shUserId = shUserId;
	}

	public String getShUserName() {
		return shUserName;
	}

	public void setShUserName(String shUserName) {
		this.shUserName = shUserName;
	}

	public long getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(long brandCode) {
		this.brandCode = brandCode;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getLogo1x() {
		return logo1x;
	}

	public void setLogo1x(String logo1x) {
		this.logo1x = logo1x;
	}

	public String getLogo2x() {
		return logo2x;
	}

	public void setLogo2x(String logo2x) {
		this.logo2x = logo2x;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public DateTime getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(DateTime submitDate) {
		this.submitDate = submitDate;
	}

	public String getStarRating() {
		return starRating;
	}

	public void setStarRating(String starRating) {
		this.starRating = starRating;
	}

	public int getRatingCode() {
		return ratingCode;
	}

	public void setRatingCode(int ratingCode) {
		this.ratingCode = ratingCode;
	}

	public int getAgreeCount() {
		return agreeCount;
	}

	public void setAgreeCount(int agreeCount) {
		this.agreeCount = agreeCount;
	}

	public int getDisagreeCount() {
		return disagreeCount;
	}

	public void setDisagreeCount(int disagreeCount) {
		this.disagreeCount = disagreeCount;
	}

	public int getFbShareCount() {
		return fbShareCount;
	}

	public void setFbShareCount(int fbShareCount) {
		this.fbShareCount = fbShareCount;
	}

	public int getTwShareCount() {
		return twShareCount;
	}

	public void setTwShareCount(int twShareCount) {
		this.twShareCount = twShareCount;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public String getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(String lockStatus) {
		this.lockStatus = lockStatus;
	}

	public long getLockByBrandUserId() {
		return lockByBrandUserId;
	}

	public void setLockByBrandUserId(long lockByBrandUserId) {
		this.lockByBrandUserId = lockByBrandUserId;
	}

	public String getLockByUserName() {
		return lockByUserName;
	}

	public void setLockByUserName(String lockByUserName) {
		this.lockByUserName = lockByUserName;
	}

	public DateTime getLockDate() {
		return lockDate;
	}

	public void setLockDate(DateTime lockDate) {
		this.lockDate = lockDate;
	}

	public String getCommentStatus() {
		return commentStatus;
	}

	public void setCommentStatus(String commentStatus) {
		this.commentStatus = commentStatus;
	}

	public long getParentCommentId() {
		return parentCommentId;
	}

	public void setParentCommentId(long parentCommentId) {
		this.parentCommentId = parentCommentId;
	}

	public String getBrandResponseStatus() {
		return brandResponseStatus;
	}

	public void setBrandResponseStatus(String brandResponseStatus) {
		this.brandResponseStatus = brandResponseStatus;
	}

	public String getHasSubComment() {
		return hasSubComment;
	}

	public void setHasSubComment(String hasSubComment) {
		this.hasSubComment = hasSubComment;
	}

	public String getHasResponseComment() {
		return hasResponseComment;
	}

	public void setHasResponseComment(String hasResponseComment) {
		this.hasResponseComment = hasResponseComment;
	}

	public String getSectorCode() {
		return sectorCode;
	}

	public void setSectorCode(String sectorCode) {
		this.sectorCode = sectorCode;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getCommentEdited() {
		return commentEdited;
	}

	public void setCommentEdited(String commentEdited) {
		this.commentEdited = commentEdited;
	}

	public String getCommentActionTaken() {
		return commentActionTaken;
	}

	public void setCommentActionTaken(String commentActionTaken) {
		this.commentActionTaken = commentActionTaken;
	}

	@Override
	public String toString() {
		return "UserComment{" +
		       "commentId=" + commentId +
		       ", shUserId=" + shUserId +
		       ", shUserName='" + shUserName + '\'' +
		       ", brandCode=" + brandCode +
		       ", brandName='" + brandName + '\'' +
		       ", logo1x='" + logo1x + '\'' +
		       ", logo2x='" + logo2x + '\'' +
		       ", countryCode='" + countryCode + '\'' +
		       ", languageCode='" + languageCode + '\'' +
		       ", comment='" + comment + '\'' +
		       ", submitDate=" + submitDate +
		       ", ratingCode=" + ratingCode +
		       ", starRating='" + starRating + '\'' +
		       ", agreeCount=" + agreeCount +
		       ", disagreeCount=" + disagreeCount +
		       ", fbShareCount=" + fbShareCount +
		       ", twShareCount=" + twShareCount +
		       ", approved='" + approved + '\'' +
		       ", lockStatus='" + lockStatus + '\'' +
		       ", commentStatus='" + commentStatus + '\'' +
		       ", lockByBrandUserId=" + lockByBrandUserId +
		       ", lockByUserName='" + lockByUserName + '\'' +
		       ", lockDate=" + lockDate +
		       ", parentCommentId=" + parentCommentId +
		       ", brandResponseStatus='" + brandResponseStatus + '\'' +
		       ", hasSubComment='" + hasSubComment + '\'' +
		       ", hasResponseComment='" + hasResponseComment + '\'' +
		       ", commentEdited='" + commentEdited + '\'' +
		       ", commentActionTaken='" + commentActionTaken + '\'' +
		       ", sectorCode='" + sectorCode + '\'' +
		       ", dealerName='" + dealerName + '\'' +
		       '}';
	}
}
