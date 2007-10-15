package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;

public class FactData implements Serializable {


	public FactData() {}
	public FactData(String type, String name, FieldData[] fieldData) {
		this.type = type;
		this.name = name;
		this.fieldData = fieldData;
	}

	/**
	 * The type (class)
	 */
	public String type;

	/**
	 * The name of the "variable"
	 */
	public String name;


	public FieldData[] fieldData;


}
