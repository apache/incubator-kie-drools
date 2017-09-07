package org.drools.pmml.pmml_4_2.model.datatypes;

public class PMML4Double extends PMML4Data<Double> {

	PMML4Double(String name, String context, String displayName, Double value) {
		super(name, context, displayName, value);
	}

	PMML4Double(String name, String context, String displayName, Double value, Double weight, Boolean valid, Boolean missing) {
		super(name, context, displayName, value, weight, valid, missing);
	}
	
	PMML4Double(String name, String context, String displayName, Double value, Double weight) {
		super(name, context, displayName, value, weight);
	}
	
	@Override
	public Double getValue() {
		return super.getValue();
	}

	@Override
	public void registerWithDataFactory() {
		PMML4DataFactory.registerDataType(Double.class.getName(), PMML4Double.class);
	}

}
