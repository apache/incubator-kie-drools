package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierFieldAccessDescr extends VerifierComponent {

	private static int index = 0;

	private String fieldName;
	private String argument;

	public VerifierFieldAccessDescr() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.FIELD_ACCESSOR;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
