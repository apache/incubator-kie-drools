package org.drools.pmml.pmml_4_2.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.dmg.pmml.pmml_4_2.descr.TreeModel;
import org.drools.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

public class Treemodel extends AbstractModel {
	private static final String MINING_POJO_TEMPLATE="/org/drools/pmml/pmml_4_2/templates/mvel/tree/treeMiningPojo.mvel";
	private static final String MINING_TEMPLATE_NAME="TreeTemplate";
	private TreeModel rawModel;

	public Treemodel(String modelId, TreeModel rawModel, PMML4Unit owner) {
		super(modelId, PMML4ModelType.TREE, owner);
		this.rawModel = rawModel;
	}

	@Override
	public List<OutputField> getRawOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PMMLDataField> getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMiningPojoClassName() {
		return this.getModelId()+"TreeToken";
	}

	@Override
	public MiningSchema getMiningSchema() {
		for (Serializable ser: rawModel.getExtensionsAndNodesAndMiningSchemas()) {
			if (ser instanceof MiningSchema) {
				return (MiningSchema)ser;
			}
		}
		return null;
	}

	@Override
	public Output getOutput() {
		for (Serializable ser: rawModel.getExtensionsAndNodesAndMiningSchemas()) {
			if (ser instanceof Output) {
				return (Output)ser;
			}
		}
		return null;
	}

	@Override
	protected TemplateRegistry getTemplateRegistry() {
        TemplateRegistry registry = new SimpleTemplateRegistry();
        InputStream inputStream = Scorecard.class.getResourceAsStream(MINING_POJO_TEMPLATE);
        CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
        registry.addNamedTemplate(MINING_TEMPLATE_NAME,ct);
        return registry;
	}

	@Override
	protected String getMiningPojoTemplateName() {
		return MINING_TEMPLATE_NAME;
	}

}
