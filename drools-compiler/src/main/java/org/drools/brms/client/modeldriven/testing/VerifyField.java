package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;

public class VerifyField implements Serializable {

	public String fieldName;
	public String expected;

	public String actualResult;
	public Boolean successResult;

	/**
	 * Operator is generally "==" or "!="  - an MVEL operator.
	 */
	public String operator = "==";

	public VerifyField() {}

	public VerifyField(String fieldName, String expected, String operator) {
		this.fieldName = fieldName;
		this.expected = expected;
		this.operator = operator;
	}


}
