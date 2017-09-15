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
