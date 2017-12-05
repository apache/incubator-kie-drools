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

import java.io.InputStream;
import java.io.Serializable;

import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.RegressionModel;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.kie.pmml.pmml_4_2.PMML4Model;
import org.kie.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

public class Regression extends AbstractModel<RegressionModel> {
	private static final String MINING_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/regression/regressionMiningPojo.mvel";
	private static final String OUTPUT_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/regression/regressionOutputPojo.mvel";

	public Regression( String modelId, RegressionModel rawModel, PMML4Model parentModel, PMML4Unit owner) {
		super(modelId, PMML4ModelType.REGRESSION, owner, parentModel, rawModel);
	}

	@Override
	public String getMiningPojoClassName() {
		return helper.compactAsJavaId(this.getModelId().concat("RegressionData"), true);
	}
	
	@Override
	public String getOutputPojoClassName() {
		return helper.compactAsJavaId(this.getModelId().concat("RegressionOutput"), true);
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
		for (Serializable ser: rawModel.getExtensionsAndRegressionTablesAndMiningSchemas()) {
			if (ser instanceof Output) {
				return (Output)ser;
			}
		}
		return null;
	}

	@Override
	protected void addMiningTemplateToRegistry(TemplateRegistry registry) {
        InputStream inputStream = Scorecard.class.getResourceAsStream(MINING_POJO_TEMPLATE);
        if (inputStream != null) {
	        CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
	        registry.addNamedTemplate(getMiningPojoTemplateName(),ct);
        }
	}
	
	@Override
	protected void addOutputTemplateToRegistry(TemplateRegistry registry) {
		InputStream inputStream = Scorecard.class.getResourceAsStream(OUTPUT_POJO_TEMPLATE);
		if (inputStream != null) {
			CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
			if (ct != null) {
				registry.addNamedTemplate(getOutputPojoTemplateName(), ct);
			}
		}
	}

}
