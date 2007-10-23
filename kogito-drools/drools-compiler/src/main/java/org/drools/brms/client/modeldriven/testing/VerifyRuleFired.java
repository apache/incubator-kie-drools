package org.drools.brms.client.modeldriven.testing;

public class VerifyRuleFired implements Assertion {

	public String ruleName;
	public Integer expectedCount;
	public Boolean expectNotFire;
	public Boolean expectFire;

	public Boolean success;
	public Integer actual;

}
