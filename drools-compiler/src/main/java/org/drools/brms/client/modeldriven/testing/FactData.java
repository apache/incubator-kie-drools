package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

	/**
	 * If its a modify, obviously we are modifying existing data in working memory.
	 */
	public boolean isModify;

	public FactData() {}
	public FactData(String type, String name, FieldData[] fieldData, boolean global, boolean modify) {
		if (isGlobal && modify) {
			throw new IllegalArgumentException("Not able to be a global modify.");
		}
		this.type = type;
		this.name = name;
		this.fieldData = fieldData;
		this.isGlobal = global;
		this.isModify = modify;

	}

	/**
	 * Using arrays for type safety.
	 * Clumsy, but works.
	 */
	public void addFieldData(FieldData fd) {
		FieldData[] nf = new FieldData[this.fieldData.length + 1];
		for (int i = 0; i < this.fieldData.length; i++) {
			nf[i] = this.fieldData[i];
		}
		nf[this.fieldData.length] = fd;
		this.fieldData = nf;
	}


}
