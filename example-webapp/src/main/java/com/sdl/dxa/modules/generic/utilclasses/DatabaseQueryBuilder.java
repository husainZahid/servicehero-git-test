package com.sdl.dxa.modules.generic.utilclasses;

import java.util.List;

public class DatabaseQueryBuilder {
	public static final int QUERY_SELECT = 1;
	public static final int QUERY_UPDATE = 2;
	private int queryType;
	private String tableName;
	private List<DatabaseQueryField> columnNameValues;
	private List<DatabaseQueryField> identifierNameValues;
	
	public DatabaseQueryBuilder(int queryType, String tableName) {
		this.queryType = queryType;
		this.tableName = tableName;
    }

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public List<DatabaseQueryField> getColumnNameValues() {
		return columnNameValues;
	}

	public void setColumnNameValues(List<DatabaseQueryField> columnNameValues) {
		this.columnNameValues = columnNameValues;
	}

	public List<DatabaseQueryField> getIdentifierNameValues() {
		return identifierNameValues;
	}

	public void setIdentifierNameValues(
			List<DatabaseQueryField> identifierNameValues) {
		this.identifierNameValues = identifierNameValues;
	}
	
	public String getQuery() {
		String operator = "", connector = "";
		int count = 0;
		StringBuffer objSb = new StringBuffer();
		if(queryType == QUERY_SELECT)
			objSb.append("Select * from " + tableName);
		else 
			objSb.append("Update " + tableName + " Set");
		if(queryType == QUERY_UPDATE) {
			for (DatabaseQueryField columnNameValue : columnNameValues) {
				count++;
				if(columnNameValue.getOperator() == DatabaseQueryField.OPERATOR_EQUAL) {
					operator = " = "; 
				} else if(columnNameValue.getOperator() == DatabaseQueryField.OPERATOR_NOT_EQUAL) {
					operator = "<>"; 
				} else if(columnNameValue.getOperator() == DatabaseQueryField.OPERATOR_LESS_THAN) {
					operator = "<"; 
				} else if(columnNameValue.getOperator() == DatabaseQueryField.OPERATOR_LESS_THAN_OR_EQUAL) {
					operator = "<="; 
				} else if(columnNameValue.getOperator() == DatabaseQueryField.OPERATOR_GREATER_THAN) {
					operator = ">"; 
				} else if(columnNameValue.getOperator() == DatabaseQueryField.OPERATOR_GREATER_THAN_OR_EQUAL) {
					operator = ">="; 
				}
				
				if(columnNameValue.getConnector() == DatabaseQueryField.CONNECTOR_COMMA) {
					connector = ",";
				} else if(columnNameValue.getConnector() == DatabaseQueryField.CONNECTOR_BLANK) {	
					connector = "";
				}
				if(columnNameValues.size() == count)
					connector = "";
				
				if(columnNameValue.getFieldType() == DatabaseQueryField.FIELD_TYPE_STRING) {
					objSb.append(" " + columnNameValue.getFieldName() + operator + "'" + columnNameValue.getFieldValue() + "'" + connector);
				} else {
					objSb.append(" " + columnNameValue.getFieldName() + operator + columnNameValue.getFieldValue() + connector);
				}
			}
		}
		objSb.append(" where");
		for (DatabaseQueryField identifierNameValue : identifierNameValues) {
			if(identifierNameValue.getOperator() == DatabaseQueryField.OPERATOR_EQUAL) {
				operator = " = "; 
			} else if(identifierNameValue.getOperator() == DatabaseQueryField.OPERATOR_NOT_EQUAL) {
				operator = "<>"; 
			} else if(identifierNameValue.getOperator() == DatabaseQueryField.OPERATOR_LESS_THAN) {
				operator = "<"; 
			} else if(identifierNameValue.getOperator() == DatabaseQueryField.OPERATOR_LESS_THAN_OR_EQUAL) {
				operator = "<="; 
			} else if(identifierNameValue.getOperator() == DatabaseQueryField.OPERATOR_GREATER_THAN) {
				operator = ">"; 
			} else if(identifierNameValue.getOperator() == DatabaseQueryField.OPERATOR_GREATER_THAN_OR_EQUAL) {
				operator = ">="; 
			}
			
			if(identifierNameValue.getConnector() == DatabaseQueryField.CONNECTOR_AND) {
				connector = " and";
			} else if(identifierNameValue.getConnector() == DatabaseQueryField.CONNECTOR_OR) {
				connector = " or";
			} else if(identifierNameValue.getConnector() == DatabaseQueryField.CONNECTOR_BLANK) {	
				connector = "";
			}
			
			if(identifierNameValue.getFieldType() == DatabaseQueryField.FIELD_TYPE_STRING) {
				objSb.append(" " + identifierNameValue.getFieldName() + operator + "'" + identifierNameValue.getFieldValue() + "'" + connector);
			} else {
				objSb.append(" " + identifierNameValue.getFieldName() + operator + identifierNameValue.getFieldValue() + connector);
			}
		}
		return objSb.toString();
	}
}
