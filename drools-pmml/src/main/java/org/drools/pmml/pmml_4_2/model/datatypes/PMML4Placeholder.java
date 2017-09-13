package org.drools.pmml.pmml_4_2.model.datatypes;

import java.util.Date;

public class PMML4Placeholder extends PMML4Data<Object> {

	PMML4Placeholder(String name, String context) {
		super(name, context, true);
		setMissing(true);
		setValue(new Integer(0));
	}

	@Override
	public void registerWithDataFactory() {
		PMML4DataFactory.registerDataType(Object.class.getName(), PMML4Placeholder.class);
	}

}
