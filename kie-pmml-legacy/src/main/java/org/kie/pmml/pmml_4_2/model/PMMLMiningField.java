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
package org.kie.pmml.pmml_4_2.model;

import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;
import org.dmg.pmml.pmml_4_2.descr.MiningField;

public class PMMLMiningField extends PMMLDataField {
	private boolean inDictionary;
	private String modelId;
	private FIELDUSAGETYPE fieldUsageType;

	public PMMLMiningField(MiningField miningField, DataField field, String modelId, boolean inDictionary) {
		super(miningField,field);
		this.modelId = modelId;
		this.fieldUsageType = miningField.getUsageType();
		this.inDictionary = inDictionary;
	}
	
	public PMMLMiningField(MiningField miningField, String modelId) {
		super(miningField, null);
		this.fieldUsageType = miningField.getUsageType();
		this.modelId = modelId;
		this.inDictionary = false;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public FIELDUSAGETYPE getFieldUsageType() {
		return fieldUsageType;
	}

	public void setFieldUsageType(FIELDUSAGETYPE fieldUsageType) {
		this.fieldUsageType = fieldUsageType;
	}

	public boolean isInDictionary() {
		return this.inDictionary;
	}

}
