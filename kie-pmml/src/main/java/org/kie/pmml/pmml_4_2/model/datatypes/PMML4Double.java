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
package org.kie.pmml.pmml_4_2.model.datatypes;


import org.kie.api.pmml.PMML4Data;

public class PMML4Double extends PMML4Data<Double> {

	PMML4Double(String correlationId, String name, String context, String displayName, Double value) {
		super(correlationId, name, context, displayName, value);
	}

	PMML4Double(String correlationId, String name, String context, String displayName, Double value, Double weight, Boolean valid, Boolean missing) {
		super(correlationId, name, context, displayName, value, weight, valid, missing);
	}
	
	PMML4Double(String correlationId, String name, String context, String displayName, Double value, Double weight) {
		super(correlationId, name, context, displayName, value, weight);
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
