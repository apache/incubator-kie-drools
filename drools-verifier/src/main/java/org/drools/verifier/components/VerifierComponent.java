package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class VerifierComponent implements
		Comparable<VerifierComponent> {

	protected String ruleName;
	protected int ruleId;
	protected final int id;

	protected VerifierComponent parent;

	// Order number of this instance under parent.
	protected int orderNumber = 0;

	public abstract VerifierComponentType getComponentType();

	public int compareTo(VerifierComponent o) {
		if (id == o.getId()) {
			return 0;
		}

		return (id > o.getId() ? 1 : -1);
	}

	public VerifierComponent(int id) {
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

	public VerifierComponent getParent() {
		return parent;
	}

	public void setParent(VerifierComponent parent) {
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