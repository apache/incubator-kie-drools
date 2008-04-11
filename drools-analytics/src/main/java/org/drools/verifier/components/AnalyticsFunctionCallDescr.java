package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsFunctionCallDescr extends AnalyticsComponent {

	private static int index = 0;

	private String name;
	private String arguments;

	public AnalyticsFunctionCallDescr() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.FUNCTION_CALL;
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
