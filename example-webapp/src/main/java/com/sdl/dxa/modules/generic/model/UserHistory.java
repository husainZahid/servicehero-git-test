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
import com.sdl.webapp.common.api.model.entity.Tag;

/**
 * MyHistory
 *
 * @author Saurabh
 */
@SemanticEntities({
	@SemanticEntity(entityName = "UserHistory", vocabulary = SCHEMA_ORG, prefix = "uh", public_ = true)
})
public class UserHistory extends AbstractEntity {
	@SemanticProperty("uh:lastVoteDays") 
    private int lastVoteDays;
    
	@SemanticProperty("uh:numberOfVotes") 
    private int numberOfVotes;
	
	@SemanticProperty("uh:voteAverage") 
    private double voteAverage;
	
	@SemanticProperty("uh:voteHistory") 
    private List<VoteHistory> voteHistory;
	
	public int getLastVoteDays() {
		return lastVoteDays;
	}

	public void setLastVoteDays(int lastVoteDays) {
		this.lastVoteDays = lastVoteDays;
	}

	public int getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(int numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

	public double getVoteAverage() {
		return voteAverage;
	}

	public void setVoteAverage(double voteAverage) {
		this.voteAverage = voteAverage;
	}
	
	public List<VoteHistory> getVoteHistory() {
		return voteHistory;
	}

	public void setVoteHistory(List<VoteHistory> voteHistory) {
		this.voteHistory = voteHistory;
	}

	@Override
    public String toString() {
        return "UserHistory{" +
                "lastVoteDays=" + lastVoteDays +
                ", numberOfVotes=" + numberOfVotes +
                ", voteAverage=" + voteAverage +
                ", voteHistory=" + voteHistory +
                '}';
    }	
}
