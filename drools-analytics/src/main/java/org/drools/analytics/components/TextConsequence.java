package org.drools.analytics.components;

public class TextConsequence extends AnalyticsComponent implements Consquence {

	private static int index = 0;
	private String text;

	public TextConsequence() {
		super(index++);
	}

	public ConsequenceType getConsequenceType() {
		return ConsequenceType.TEXT;
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.CONSEQUENCE;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
