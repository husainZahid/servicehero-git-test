package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

import java.util.ArrayList;
import java.util.List;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.Tag;
@SemanticEntities({
    @SemanticEntity(entityName = "CustomContentQuery", vocabulary = SCHEMA_ORG, prefix = "cq", public_ = true),
    @SemanticEntity(entityName = "ItemList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
    @SemanticEntity(entityName = "ContentQuery", vocabulary = SDL_CORE, prefix = "q")
})
public class CustomContentList extends AbstractEntity {
    @SemanticProperties({
        @SemanticProperty("q:headline"),
        @SemanticProperty("s:headline"),
        @SemanticProperty("cq:headline")
	})
	private String headline;
	
	@SemanticProperties({
        @SemanticProperty("q:link"),
        @SemanticProperty("cq:link")
	})
	private Link link;
	
	@SemanticProperties({
        @SemanticProperty("q:pageSize"),
        @SemanticProperty("cq:pageSize")
	})
	private int pageSize;
	
	@SemanticProperties({
        @SemanticProperty("q:contentType"),
        @SemanticProperty("cq:contentType")
	})
	private Tag contentType;
	
	@SemanticProperties({
        @SemanticProperty("q:sort"),
        @SemanticProperty("cq:sort")
	})
	private Tag sort;
	
	private int start;
	
	private int currentPage = 1;
	
	private boolean hasMore;

	@SemanticProperties({
		@SemanticProperty("q:queryFilters"),
		@SemanticProperty("cq:queryFilters")
	})
    private List<QueryFilter> queryFilters;

	@SemanticProperties({
            @SemanticProperty("s:itemListElement")
    })
	private List<CustomTeaser> itemListElements = new ArrayList<>();
	

	public String getHeadline() {
	    return headline;
	}
	
	public void setHeadline(String headline) {
	    this.headline = headline;
	}
	
	public Link getLink() {
	    return link;
	}
	
	public void setLink(Link link) {
	    this.link = link;
	}
	
	public int getPageSize() {
	    return pageSize;
	}
	
	public void setPageSize(int pageSize) {
	    this.pageSize = pageSize;
	}
	
	public Tag getContentType() {
	    return contentType;
	}
	
	public void setContentType(Tag contentType) {
	    this.contentType = contentType;
	}
	
	public Tag getSort() {
	    return sort;
	}
	
	public void setSort(Tag sort) {
	    this.sort = sort;
	}
	
	public int getStart() {
	    return start;
	}
	
	public void setStart(int start) {
	    this.start = start;
	}
	
	public int getCurrentPage() {
	    return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
	    this.currentPage = currentPage;
	}
	
	public boolean isHasMore() {
	    return hasMore;
	}
	
	public void setHasMore(boolean hasMore) {
	    this.hasMore = hasMore;
	}
	
	public List<CustomTeaser> getItemListElements() {
	    return itemListElements;
	}
	
	public void setItemListElements(List<CustomTeaser> list) {
	    this.itemListElements = list;
	}

	public List<QueryFilter> getQueryFilters() {
		return queryFilters;
	}

	public void setQueryFilters(List<QueryFilter> queryFilters) {
		this.queryFilters = queryFilters;
	}

	@Override
	public String toString() {
	    return "CustomContentList{" +
	            "headline='" + headline + '\'' +
	            ", link=" + link +
	            ", pageSize=" + pageSize +
	            ", contentType=" + contentType +
	            ", sort=" + sort +
	            ", start=" + start +
	            ", currentPage=" + currentPage +
	            ", hasMore=" + hasMore +
	            ", itemListElements=" + itemListElements +
	            ", queryFilters=" + queryFilters +
	            '}';
	}
}
