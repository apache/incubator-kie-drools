package org.drools.analytics.components;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class AnalyticsComponent implements
		Comparable<AnalyticsComponent> {

	protected String ruleName;
	protected int id;

	public abstract AnalyticsComponentType getComponentType();

	@Override
	public int compareTo(AnalyticsComponent o) {
		if (id == o.getId()) {
			return 0;
		}

		return (id > o.getId() ? 1 : -1);
	}

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