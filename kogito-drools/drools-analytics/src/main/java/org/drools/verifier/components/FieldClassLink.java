package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class FieldClassLink extends AnalyticsComponent {

	private static int index = 0;

	private int fieldId;
	private int classId;

	public FieldClassLink() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.FIELD_CLASS_LINK;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
}
