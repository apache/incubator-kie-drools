package org.drools.pmml.pmml_4_2.model.datatypes;

import java.util.Date;

public class PMML4Date extends PMML4Data<Date> {

	PMML4Date(String name, String context, String displayName, Date value) {
		super(name, context, displayName, value);
	}


	PMML4Date(String name, String context, String displayName, Date value, Double weight, Boolean valid, Boolean missing) {
		super(name, context, displayName, value, weight, valid, missing);
	}
	
	PMML4Date(String name, String context, String displayName, Date value, Double weight) {
		super(name, context, displayName, value, weight);
	}
	
	@Override
	public void registerWithDataFactory() {
		PMML4DataFactory.registerDataType(Date.class.getName(), PMML4Date.class);
	}


	@Override
	public Date getValue() {
		return super.getValue();
	}

}
