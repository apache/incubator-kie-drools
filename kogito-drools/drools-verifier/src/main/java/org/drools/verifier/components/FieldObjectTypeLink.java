package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class FieldObjectTypeLink extends VerifierComponent {

	private static int index = 0;

	private int fieldId;
	private int objectTypeId;

	public FieldObjectTypeLink() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.FIELD_CLASS_LINK;
	}

	public int getObjectTypeId() {
		return objectTypeId;
	}

	public void setClassId(int classId) {
		this.objectTypeId = classId;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
}
