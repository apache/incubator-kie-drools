package org.drools.pmml.pmml_4_2.model;

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
