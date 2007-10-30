package org.drools.brms.client.modeldriven.testing;

public class VerifyRuleFired implements Expectation {

	public String ruleName;
	public Integer expectedCount;

	/**
	 * If this is true, then we expect it to fire at least once.
	 * False means it should not fire at all (this is an alternative
	 * to specifying an expected count).
	 */
	public Boolean expectedFire;


	public VerifyRuleFired() {}
	public VerifyRuleFired(String ruleName, Integer expectedCount, Boolean expectedFire) {
		this.ruleName = ruleName;
		this.expectedCount = expectedCount;
		this.expectedFire = expectedFire;
	}

	public Boolean successResult;
	public Integer actualResult;


	public boolean wasSuccessful() {
		return successResult.booleanValue();
	}

}
