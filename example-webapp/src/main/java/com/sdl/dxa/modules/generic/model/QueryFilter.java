package com.sdl.dxa.modules.generic.model;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

import org.joda.time.DateTime;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;

/**
 * QueryFilters
 *
 * @author Saurabh
 */

@SemanticEntity(entityName = "QueryFilter", vocabulary = SCHEMA_ORG, prefix = "qf", public_ = true)
public class QueryFilter extends AbstractEntity {
	@SemanticProperty("qf:filterType")
    private String filterType;
    
	@SemanticProperty("qf:fieldName")
    private String fieldName;

	@SemanticProperty("qf:fieldValue")
	private String fieldValue;
    
	@SemanticProperty("qf:toDate")
	private DateTime toDate;
    
	@SemanticProperty("qf:fromDate")
	private DateTime fromDate;

	@SemanticProperty("qf:operator")
	private String operator;
	
	@SemanticProperty("qf:booleanParam")
	private String booleanParam;

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public DateTime getToDate() {
		return toDate;
	}

	public void setToDate(DateTime toDate) {
		this.toDate = toDate;
	}

	public DateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(DateTime fromDate) {
		this.fromDate = fromDate;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getBooleanParam() {
		return booleanParam;
	}

	public void setBooleanParam(String booleanParam) {
		this.booleanParam = booleanParam;
	}

	@Override
    public String toString() {
        return "QueryFilter{" +
                "filterType='" + filterType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldValue='" + fieldValue + '\'' +
                ", toDate=" + toDate + 
                ", fromDate=" + fromDate + 
                ", operator='" + operator + '\'' +
                ", booleanParam='" + booleanParam + '\'' +
                '}';
    }
}
