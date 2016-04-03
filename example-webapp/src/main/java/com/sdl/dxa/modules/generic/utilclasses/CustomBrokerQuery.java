package com.sdl.dxa.modules.generic.utilclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sdl.dxa.modules.generic.model.QueryFilter;
import com.sdl.webapp.tridion.query.BrokerQueryException;
import com.tridion.broker.StorageException;
import com.tridion.broker.querying.MetadataType;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.Criteria;
import com.tridion.broker.querying.criteria.content.ItemSchemaCriteria;
import com.tridion.broker.querying.criteria.content.ItemTitleCriteria;
import com.tridion.broker.querying.criteria.content.PublicationCriteria;
import com.tridion.broker.querying.criteria.metadata.CustomMetaKeyCriteria;
import com.tridion.broker.querying.criteria.metadata.CustomMetaValueCriteria;
import com.tridion.broker.querying.criteria.operators.AndCriteria;
import com.tridion.broker.querying.criteria.taxonomy.TaxonomyKeywordCriteria;
import com.tridion.broker.querying.filter.LimitFilter;
import com.tridion.broker.querying.filter.PagingFilter;
import com.tridion.broker.querying.sorting.SortColumn;
import com.tridion.broker.querying.sorting.SortDirection;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.broker.querying.sorting.column.CustomMetaKeyColumn;

public class CustomBrokerQuery {
    private int schemaId;
    private int publicationId;
    private String title = null;
    private int maxResults;
    private String sort;
    private int start;
    private int pageSize;
    private Multimap<String, String> keywordFilters = ArrayListMultimap.create();
    private List<QueryFilter> queryFilters;

    private boolean hasMore;

    public int getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(int schemaId) {
        this.schemaId = schemaId;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Multimap<String, String> getKeywordFilters() {
        return keywordFilters;
    }

    public void setKeywordFilters(Multimap<String, String> keywordFilters) {
        this.keywordFilters = keywordFilters;
    }

    public List<QueryFilter> getQueryFilters() {
		return queryFilters;
	}

	public void setQueryFilters(List<QueryFilter> queryFilters) {
		this.queryFilters = queryFilters;
	}

	public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<String> executeQuery() throws BrokerQueryException {
        final Query query = new Query(buildCriteria());

        if (!Strings.isNullOrEmpty(sort) && !sort.toLowerCase().equals("none")) {
            query.addSorting(getSortParameter());
        }

        if (maxResults > 0) {
            query.setResultFilter(new LimitFilter(maxResults));
        }

        if (pageSize > 0) {
            // We set the page size to one more than what we need, to see if there are more pages to come...
            query.setResultFilter(new PagingFilter(start, pageSize + 1));
        }

        try {
            final String[] ids = query.executeQuery();
            List<String> entityIds = new ArrayList<>();
            for ( String id : ids ) {
                entityIds.add(id);
            }
            return entityIds;
        } catch (StorageException e) {
            throw new BrokerQueryException("Exception while executing broker query", e);
        }
    }

    private Criteria buildCriteria() {
        final List<Criteria> children = new ArrayList<>();

        if (schemaId > 0) {
            children.add(new ItemSchemaCriteria(schemaId));
        }

        if (publicationId > 0) {
            children.add(new PublicationCriteria(publicationId));
        }
        if (title != null) {
            children.add(new ItemTitleCriteria(title));
        }

        /*for (Map.Entry<String, String> entry : keywordFilters.entries()) {
            children.add(new TaxonomyKeywordCriteria(entry.getKey(), entry.getValue(), true));
        }*/
        if(queryFilters != null) {
	        for (QueryFilter queryFilter : queryFilters) {
	        	if(queryFilter.getFilterType().equals("MetaCriteria")) {
	                children.add(new CustomMetaValueCriteria(new CustomMetaKeyCriteria(queryFilter.getFieldName()), queryFilter.getFieldValue()));
	        	} else if(queryFilter.getFilterType().equals("TaxonomyCriteria")) {
	        	    children.add(new TaxonomyKeywordCriteria(queryFilter.getFieldName(), queryFilter.getFieldValue(), true));
	        	}
	        }
        }
        

        return new AndCriteria(children.toArray(new Criteria[children.size()]));
    }

    private SortParameter getSortParameter() {
        SortDirection dir = sort.toLowerCase().endsWith("asc") ? SortDirection.ASCENDING : SortDirection.DESCENDING;
        return new SortParameter(getSortColumn(), dir);
    }

    private SortColumn getSortColumn() {
        final String sortTrim = sort.trim();
        final int pos = sortTrim.indexOf(' ');
        final String sortCol = pos > 0 ? sortTrim.substring(0, pos) : sortTrim;
        switch (sortCol.toLowerCase()) {
            case "title":
                return SortParameter.ITEMS_TITLE;

            case "pubdate":
                return SortParameter.ITEMS_LAST_PUBLISHED_DATE;

            default:
                // Default is to assume that its a custom metadata date field
                return new CustomMetaKeyColumn(sort, MetadataType.DATE);
        }
    }
}
