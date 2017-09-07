package org.drools.pmml.pmml_4_2.model.datatypes;

public class PMML4String extends PMML4Data<String>{
	
	PMML4String(String name, String context, String displayName, String value) {
		super(name, context, displayName, value);
	}

	PMML4String(String name, String context, String displayName, String value, Double weight, Boolean valid, Boolean missing) {
		super(name, context, displayName, value, weight, valid, missing);
	}

	PMML4String(String name, String context, String displayName, String value, Double weight) {
		super(name, context, displayName, value, weight);
	}
	
	@Override
	public void registerWithDataFactory() {
		PMML4DataFactory.get().registerDataType(String.class.getName(),PMML4String.class);
	}

	@Override
	public String getValue() {
		return super.getValue();
	}
}
