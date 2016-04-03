package com.sdl.dxa.modules.generic.utilclasses;

public class DatabaseQueryField {
	public static final int FIELD_TYPE_STRING = 1;
	public static final int FIELD_TYPE_NUMBER = 2;
	
	public static final int OPERATOR_EQUAL = 1;
	public static final int OPERATOR_NOT_EQUAL = 2;
	public static final int OPERATOR_LESS_THAN = 3;
	public static final int OPERATOR_LESS_THAN_OR_EQUAL = 4;
	public static final int OPERATOR_GREATER_THAN = 5;
	public static final int OPERATOR_GREATER_THAN_OR_EQUAL = 6;
	public static final int OPERATOR_LIKE = 7;
	
	public static final int CONNECTOR_BLANK = 0;
	public static final int CONNECTOR_AND = 1;
	public static final int CONNECTOR_OR = 2;
	public static final int CONNECTOR_COMMA = 3;

	private int fieldType;
	private String fieldName;
	private int operator;
	private String fieldValue;
	private int connector;
	
	public DatabaseQueryField(int fieldType, String fieldName, int operator, String fieldValue, int connector) {
		this.fieldType = fieldType;
		this.fieldName = fieldName;
		this.operator = operator;
		this.fieldValue = fieldValue;
		this.connector = connector;
    }
	
	public int getFieldType() {
		return fieldType;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public int getOperator() {
		return operator;
	}
	
	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	public String getFieldValue() {
		return fieldValue;
	}
	
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public int getConnector() {
		return connector;
	}

	public void setConnector(int connector) {
		this.connector = connector;
	}
	
	
}
