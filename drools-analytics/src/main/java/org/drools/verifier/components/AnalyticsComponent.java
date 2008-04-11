package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class AnalyticsComponent implements
		Comparable<AnalyticsComponent> {

	protected String ruleName;
	protected int ruleId;
	protected int id;

	protected AnalyticsComponent parent;
	
	// Order number of this instance under parent.
	protected int orderNumber = 0; 

	public abstract AnalyticsComponentType getComponentType();

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

	public AnalyticsComponent getParent() {
		return parent;
	}

	public void setParent(AnalyticsComponent parent) {
		this.parent = parent;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}
}