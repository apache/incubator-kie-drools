package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierMethodAccessDescr extends VerifierComponent {

	private static int index = 0;

	private String methodName;
	private String arguments;

	public VerifierMethodAccessDescr() {
		super(index++);
	}

	@Override
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.METHOD_ACCESSOR;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}
