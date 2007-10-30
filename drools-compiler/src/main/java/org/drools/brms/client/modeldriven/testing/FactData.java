package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;

public class FactData implements Fixture {

	/**
	 * The type (class)
	 */
	public String type;

	/**
	 * The name of the "variable"
	 */
	public String name;


	/**
	 * If its a global, then we will punt it in as a global, not a fact.
	 */
	public boolean isGlobal;

	public FieldData[] fieldData;

	public FactData() {}
	public FactData(String type, String name, FieldData[] fieldData, boolean global) {
		this.type = type;
		this.name = name;
		this.fieldData = fieldData;
		this.isGlobal = global;
	}


}
