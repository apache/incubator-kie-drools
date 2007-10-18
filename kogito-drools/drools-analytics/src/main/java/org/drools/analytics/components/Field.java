package org.drools.analytics.components;

import org.drools.analytics.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public class Field extends AnalyticsComponent implements Cause {

	public static enum FieldType {
		BOOLEAN("boolean"), STRING("String"), INT("int"), DOUBLE("double"), DATE(
				"Date"), VARIABLE("Variable"), OBJECT("Object");

		private String string;

		private FieldType(String string) {
			this.string = string;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	private static int index = 0;

	private int classId;
	private String className;
	private String name;
	private FieldType fieldType;

	private int lineNumber;

	public Field() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.FIELD;
	}

	public CauseType getCauseType() {
		return Cause.CauseType.FIELD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		// Only set fieldType to variable if there is no other fieldType found.
		if (fieldType == FieldType.VARIABLE && this.fieldType == null) {
			this.fieldType = fieldType;
		} else {
			this.fieldType = fieldType;
		}
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String toString() {
		return "Field '" + name + "' from class '" + className + "'";
	}
}