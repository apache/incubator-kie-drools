package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsMethodAccessDescr extends AnalyticsComponent {

	private static int index = 0;

	private String methodName;
	private String arguments;

	public AnalyticsMethodAccessDescr() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.METHOD_ACCESSOR;
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
