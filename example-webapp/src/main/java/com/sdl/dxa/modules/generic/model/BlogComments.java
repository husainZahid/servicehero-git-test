package com.sdl.dxa.modules.generic.model;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by DELL on 10/18/2015.
 */
public class BlogComments {
	private static final Logger log = LoggerFactory.getLogger(BlogComments.class);

	private long commentid;
	private long parentcommentid;
	private long blogid;
	private String comment;
	private DateTime commentDate;
	private String score;
	private String status;
	private long creatorid;

	public long getCommentid() {
		return commentid;
	}

	public void setCommentid(long commentid) {
		this.commentid = commentid;
	}

	public long getParentcommentid() {
		return parentcommentid;
	}

	public void setParentcommentid(long parentcommentid) {
		this.parentcommentid = parentcommentid;
	}

	public long getBlogid() {
		return blogid;
	}

	public void setBlogid(long blogid) {
		this.blogid = blogid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public DateTime getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(DateTime commentDate) {
		this.commentDate = commentDate;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getCreatorid() {
		return creatorid;
	}

	public void setCreatorid(long creatorid) {
		this.creatorid = creatorid;
	}

	@Override
	public String toString() {
		return "BlogComments{" +
		       "commentid=" + commentid +
		       ", parentcommentid=" + parentcommentid +
		       ", blogid=" + blogid +
		       ", comment='" + comment + '\'' +
		       ", commentDate=" + commentDate +
		       ", score='" + score + '\'' +
		       ", status='" + status + '\'' +
		       ", creatorid='" + creatorid + '\'' +
		       '}';
	}

}
