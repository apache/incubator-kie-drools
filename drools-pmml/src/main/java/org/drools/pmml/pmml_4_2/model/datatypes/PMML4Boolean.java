package org.drools.pmml.pmml_4_2.model.datatypes;

public class PMML4Boolean extends PMML4Data<Boolean> {

	public PMML4Boolean(String name, String context, String displayName, Boolean value) {
		super(name, context, displayName, value);
	}

	public PMML4Boolean(String name, String context, String displayName, Boolean value, Double weight, Boolean valid,
			Boolean missing) {
		super(name, context, displayName, value, weight, valid, missing);
	}
	
	PMML4Boolean(String name, String context, String displayName, Boolean value, Double weight) {
		super(name, context, displayName, value, weight);
	}
	
	@Override
	public Boolean getValue() {
		return super.getValue();
	}

	@Override
	public void registerWithDataFactory() {
		PMML4DataFactory.registerDataType(Boolean.class.getName(), PMML4Boolean.class);
	}

}
