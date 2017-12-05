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
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.OPTYPE;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.dmg.pmml.pmml_4_2.descr.RESULTFEATURE;

public class PMMLOutputField extends PMMLDataField {
	private String modelId;
	private OPTYPE opType;
	private String targetField;
	private RESULTFEATURE featureType;
	

	public PMMLOutputField(OutputField outputField, DataField field, String modelId) {
		super(outputField,field);
		this.modelId = modelId;
		this.opType = outputField.getOptype();
		this.targetField = outputField.getTargetField();
		this.featureType = outputField.getFeature();
	}
	
	public PMMLOutputField(MiningField miningField, DataField field, String modelId) {
		super(miningField,field);
		this.modelId = modelId;
		this.featureType = RESULTFEATURE.PREDICTED_VALUE;
	}


	public String getModelId() {
		return modelId;
	}


	public void setModelId(String modelId) {
		this.modelId = modelId;
	}


	public OPTYPE getOpType() {
		return opType;
	}


	public void setOpType(OPTYPE opType) {
		this.opType = opType;
	}


	public String getTargetField() {
		return targetField;
	}


	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}


	public RESULTFEATURE getFeatureType() {
		return featureType;
	}


	public void setFeatureType(RESULTFEATURE featureType) {
		this.featureType = featureType;
	}

	
}
