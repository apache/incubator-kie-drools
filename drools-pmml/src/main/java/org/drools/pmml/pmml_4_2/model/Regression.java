package org.drools.pmml.pmml_4_2.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.dmg.pmml.pmml_4_2.descr.RegressionModel;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

public class Regression extends AbstractModel {
	private static final String MINING_POJO_TEMPLATE = "/org/drools/pmml/pmml_4_2/templates/mvel/regression/regressionMiningPojo.mvel";
	private static final String MINING_TEMPLATE_NAME = "RegressionTemplate";
	private RegressionModel rawModel;

	public Regression(String modelId, RegressionModel rawModel, PMML4Unit owner) {
		super(modelId, PMML4ModelType.REGRESSION, owner);
		this.rawModel = rawModel;
	}

	@Override
	public List<MiningField> getRawMiningFields() {
		List<MiningField> fields = new ArrayList<>();
		if (miningFieldMap == null) {
			initMiningFieldMap();
		}
		if (miningFieldMap != null && !miningFieldMap.isEmpty()) {
			fields.addAll(miningFieldMap.values());
		}
		return fields;
	}

	@Override
	public List<OutputField> getRawOutputFields() {
		List<OutputField> fields = new ArrayList<>();
		if (this.outputFieldMap == null) {
			initOutputFieldMap();
		}
		if (outputFieldMap != null && !outputFieldMap.isEmpty()) {
			fields.addAll(outputFieldMap.values());
		}
		return fields;
	}

	@Override
	public List<PMMLDataField> getMiningFields() {
		List<PMMLDataField> fields = new ArrayList<>();
		List<PMMLDataField> dataDictionary = getOwner().getDataDictionaryFields();
		if (dataDictionary != null && !dataDictionary.isEmpty()) {
			for (PMMLDataField field: dataDictionary) {
				if (isValidMiningField(field)) {
					fields.add(field);
				}
			}
		}
		return fields;
	}

	@Override
	public List<PMMLDataField> getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getMiningPojoClassName() {
		return this.getModelId()+this.getModelType();
	}

	@Override
	public MiningSchema getMiningSchema() {
		for (Serializable ser: rawModel.getExtensionsAndRegressionTablesAndMiningSchemas()) {
			if (ser instanceof MiningSchema) {
				return (MiningSchema)ser;
			}
		}
		return null;
	}

	@Override
	public Output getOutput() {
		// TODO Auto-generated method stub
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
    public String getMiningPojoTemplateName() {
    	return MINING_TEMPLATE_NAME;
    }
}
