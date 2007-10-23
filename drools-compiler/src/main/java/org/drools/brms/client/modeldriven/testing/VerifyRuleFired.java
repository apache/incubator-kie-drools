package org.drools.brms.client.modeldriven.testing;

public class VerifyRuleFired implements Assertion {

	public String ruleName;
	public Integer expectedCount;

	/**
	 * If this is true, then we expect it to fire at least once.
	 * False means it should not fire at all (this is an alternative
	 * to specifying an expected count).
	 */
	public Boolean expectedFire;

	public Boolean success;
	public Integer actual;

}
