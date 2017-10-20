package org.drools.pmml.pmml_4_2.model;

import java.util.List;
import java.util.Map;

import org.kie.api.definition.rule.Unit;
import org.kie.api.runtime.rule.RuleUnit;

@Unit(value = MiningModelCompilationUnit.class)
public class MiningModelCompilationUnit implements RuleUnit {
	private String modelId;
	private String context;
	private Miningmodel model;
	private List<PMMLMiningField> miningFields;
	private List<PMMLOutputField> outputFields;
	
	public MiningModelCompilationUnit(Miningmodel model) {
		this.modelId = model.getModelId();
		this.context = model.getParentModel() != null ? model.getParentModel().getModelId().concat("."+model.getModelId()) : model.getModelId();
		this.miningFields = model.getMiningFields();
		this.outputFields = model.getOutputFields();
	}
	
	
	@Override
	public Identity getUnitIdentity() {
		return new Identity(this.getClass(),this.modelId,this.context);
	}
	
	

}
