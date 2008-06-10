package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public class Field extends VerifierComponent implements Cause {

	public static class FieldType {
		public static final FieldType BOOLEAN = new FieldType("boolean");
		public static final FieldType STRING = new FieldType( "String");
		public static final FieldType INT = new FieldType( "int");
		public static final FieldType DOUBLE = new FieldType( "double");
		public static final FieldType DATE = new FieldType( "Date");
		public static final FieldType VARIABLE = new FieldType( "Variable");
		public static final FieldType OBJECT = new FieldType( "Object");

		private final String string;

		private FieldType(String string) {
			this.string = string;
		}

		@Override
		public   String toString() {
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
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.FIELD;
	}

	public CauseType getCauseType() {
		return CauseType.FIELD;
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