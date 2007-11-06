package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;

public class FieldData implements Serializable {

	/** the name of the field */
	public String name;

	/** The value of the field to be set to.
	 * This will either be a literal value (which will be coerced by MVEL).
	 * Or if it starts with an "=" then it is an EL that will be evaluated to yield a value.
	 */
	public String value;


	public FieldData() {}
	public FieldData(String name, String value) {
		this.name = name;
		this.value = value;
	}


}
