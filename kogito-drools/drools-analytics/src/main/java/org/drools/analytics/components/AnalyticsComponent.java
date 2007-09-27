package org.drools.analytics.components;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class AnalyticsComponent {

	protected String ruleName;
	protected int id;

	public abstract AnalyticsComponentType getComponentType();

	public AnalyticsComponent(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}