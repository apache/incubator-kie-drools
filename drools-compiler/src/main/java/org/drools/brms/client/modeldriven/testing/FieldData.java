package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;

public class FieldData implements Serializable {

	public String name;
	public String value;


	public FieldData() {}
	public FieldData(String name, String value, boolean isExpression) {
		this.name = name;
		this.value = value;
		this.isExpression = isExpression;
	}

	/**
	 * If it is an expression, the value itself will be evaled, and then used to set the field.
	 */
	public boolean isExpression = false;
}
