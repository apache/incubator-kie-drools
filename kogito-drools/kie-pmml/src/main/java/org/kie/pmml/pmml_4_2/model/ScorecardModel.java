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
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.kie.pmml.pmml_4_2.PMML4Model;
import org.kie.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

public class ScorecardModel extends AbstractModel<Scorecard> {
    private static String SCORECARD_MINING_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/scorecard/scorecardDataClass.mvel";
    private static String SCORECARD_OUTPUT_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/scorecard/scorecardOutputClass.mvel";

    public ScorecardModel( String modelId, Scorecard rawModel, PMML4Model parentModel, PMML4Unit owner) {
        super(modelId, PMML4ModelType.SCORECARD, owner, parentModel, rawModel);
    }

    @Override
    public MiningSchema getMiningSchema() {
    	for (Serializable serializable: rawModel.getExtensionsAndCharacteristicsAndMiningSchemas()) {
    		if (serializable instanceof MiningSchema) {
    			return (MiningSchema)serializable;
    		}
    	}
        return null;
    }

    @Override
    public Output getOutput() {
    	for (Serializable serializable : rawModel.getExtensionsAndCharacteristicsAndMiningSchemas()) {
    		if (serializable instanceof Output) {
    			return (Output)serializable;
    		}
    	}
        return null;
    }

    public String getOutputPojo() {
//        TemplateRegistry registry = getTemplateRegistry();
//        List<PMMLDataField> dataFields = getOutputFields();
//        Map<String, Object> vars = new HashMap<>();
//        String className = "OverallScore";
//        vars.put("pmmlPackageName","org.kie.pmml.pmml_4_2.model");
//        vars.put("className",className);
//        vars.put("imports",new ArrayList<>());
//        vars.put("dataFields",dataFields);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        TemplateRuntime.execute( registry.getNamedTemplate("ScoreCardOutputTemplate"),
//                                 null,
//                                 new MapVariableResolverFactory(vars),
//                                 baos );
//        String returnVal = new String(baos.toByteArray());
        
        return null;
    }


    @Override
    public String getMiningPojoClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("ScoreCardData"),true);
    }
    
    @Override
    public String getOutputPojoClassName() {
    	return helper.compactAsJavaId(this.getModelId().concat("ScoreCardOutput"),true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScorecardModel model = (ScorecardModel) o;

        return rawModel != null ? rawModel.equals(model.rawModel) : model.rawModel == null;
    }

    @Override
    public int hashCode() {
        return rawModel != null ? rawModel.hashCode() : 0;
    }

	@Override
	protected void addMiningTemplateToRegistry(TemplateRegistry registry) {
        InputStream inputStream = Scorecard.class.getResourceAsStream(SCORECARD_MINING_POJO_TEMPLATE);
        if (inputStream != null) {
	        CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
	        registry.addNamedTemplate(getMiningPojoTemplateName(),ct);
        }
	}
	
	@Override
	protected void addOutputTemplateToRegistry(TemplateRegistry registry) {
		InputStream inputStream = Scorecard.class.getResourceAsStream(SCORECARD_OUTPUT_POJO_TEMPLATE);
		if (inputStream != null) {
			CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
			if (ct != null) {
				registry.addNamedTemplate(getOutputPojoTemplateName(), ct);
			}
		}
	}
}
