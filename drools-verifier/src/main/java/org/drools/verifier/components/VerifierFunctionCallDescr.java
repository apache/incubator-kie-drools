package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierFunctionCallDescr extends VerifierComponent {

	private static int index = 0;

	private String name;
	private String arguments;

	public VerifierFunctionCallDescr() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.FUNCTION_CALL;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
