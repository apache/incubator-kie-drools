/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
