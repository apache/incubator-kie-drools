package org.drools.pmml.pmml_4_2.model;

import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;

public class PMMLMiningField extends PMMLDataField {
	private String modelId;
	private FIELDUSAGETYPE fieldUsageType;

	public PMMLMiningField(DataField field, String modelId, FIELDUSAGETYPE fieldUsageType) {
		super(field);
		this.fieldUsageType = fieldUsageType;
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


}
