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
