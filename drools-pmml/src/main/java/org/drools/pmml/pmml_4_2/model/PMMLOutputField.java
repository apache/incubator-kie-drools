package org.drools.pmml.pmml_4_2.model;

import org.dmg.pmml.pmml_4_2.descr.DataField;
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
