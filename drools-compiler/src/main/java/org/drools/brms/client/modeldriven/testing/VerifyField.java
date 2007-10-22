package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;

public class VerifyField implements Serializable {

	public String fieldName;
	public String expected;
	public String actual;
	public Boolean success;

	public VerifyField() {}

	public VerifyField(String fieldName, String expected) {
		this.fieldName = fieldName;
		this.expected = expected;
	}


}
