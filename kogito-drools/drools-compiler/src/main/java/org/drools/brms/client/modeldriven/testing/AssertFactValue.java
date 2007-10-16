package org.drools.brms.client.modeldriven.testing;

public class AssertFactValue implements Assertion {

	public AssertFieldValue[] fieldValues;

	/**
	 * An MVEL expression that will resolve to true or false
	 */
	public String expression;


}
