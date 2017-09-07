package org.drools.pmml.pmml_4_2.model.datatypes;

public class PMML4Integer extends PMML4Data<Integer> {

	PMML4Integer(String name, String context, String displayName, Integer value) {
		super(name, context, displayName, value);
	}

	PMML4Integer(String name, String context, String displayName, Integer value, Double weight, Boolean valid,
			Boolean missing) {
		super(name, context, displayName, value, weight, valid, missing);
	}
	
	PMML4Integer(String name, String context, String displayName, Integer value, Double weight) {
		super(name, context, displayName, value, weight);
	}
	
	@Override
	public void registerWithDataFactory() {
		PMML4DataFactory.registerDataType(Integer.class.getName(), PMML4Integer.class);
	}

	@Override
	public Integer getValue() {
		return super.getValue();
	}

}
