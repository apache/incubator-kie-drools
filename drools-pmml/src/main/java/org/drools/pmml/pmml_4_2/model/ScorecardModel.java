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
package org.drools.pmml.pmml_4_2.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public class ScorecardModel extends AbstractModel {
    private static String SCORECARD_MINING_POJO_TEMPLATE = "/org/drools/pmml/pmml_4_2/templates/mvel/scorecard/scorecardDataClass.mvel";
    private static String SCORECARD_OUTPUT_POJO_TEMPLATE = "/org/drools/pmml/pmml_4_2/templates/mvel/scorecard/scorecardOutputClass.mvel";
    private static String SCORECARD_MINING_TEMPLATE_NAME = "ScorecardDataTemplate";
    private Scorecard rawModel;

    public Function<ScorecardModel,String> miningPojoClassname = (model) -> {
        return helper.compactUpperCase(getModelId()).concat("ScoreCardData");
    };
    public Function<ScorecardModel,String> outputPojoClassname = (model) -> {
        return "OverallScore";
    };

    public ScorecardModel(String modelId, Scorecard rawModel, PMML4Unit owner) {
        super(modelId, PMML4ModelType.SCORECARD, owner);
        this.rawModel = rawModel;
        initMiningFieldMap();
        initOutputFieldMap();
    }

    @Override
    public MiningSchema getMiningSchema() {
        return rawModel.getExtensionsAndCharacteristicsAndMiningSchemas().stream()
                .filter(serializable -> serializable instanceof MiningSchema)
                .map(serializable -> (MiningSchema)serializable)
                .findFirst().orElse(null);
    }

    @Override
    public Output getOutput() {
        return rawModel.getExtensionsAndCharacteristicsAndMiningSchemas().stream()
                .filter(serializable -> serializable instanceof Output)
                .map(serializable -> (Output)serializable)
                .findFirst().orElse(null);
    }

    @Override
    public List<OutputField> getRawOutputFields() {
        List<OutputField> outputFields = null;
        if (outputFieldMap == null || outputFieldMap.isEmpty()) {
            initOutputFieldMap();
            outputFields = (outputFieldMap != null && !outputFieldMap.isEmpty()) ?
                    new ArrayList<>(outputFieldMap.values()) : new ArrayList<>();
        } else {
            outputFields = new ArrayList<>(outputFieldMap.values());
        }
        return outputFields;
    }


    @Override
    public List<PMMLDataField> getOutputFields() {
        // Until a full featured output field definition is completed
        // this will return a null
        return null;
    }


    public String getOutputPojo() {
        TemplateRegistry registry = getTemplateRegistry();
        List<PMMLDataField> dataFields = getOutputFields();
        Map<String, Object> vars = new HashMap<>();
        String className = "OverallScore";
        vars.put("pmmlPackageName","org.drools.pmml.pmml_4_2.model");
        vars.put("className",className);
        vars.put("imports",new ArrayList<>());
        vars.put("dataFields",dataFields);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TemplateRuntime.execute( registry.getNamedTemplate("ScoreCardOutputTemplate"),
                                 null,
                                 new MapVariableResolverFactory(vars),
                                 baos );
        String returnVal = new String(baos.toByteArray());
        
        return returnVal;
    }

    @Override
    protected TemplateRegistry getTemplateRegistry() {
        TemplateRegistry registry = new SimpleTemplateRegistry();
        InputStream inputStream = Scorecard.class.getResourceAsStream(SCORECARD_MINING_POJO_TEMPLATE);
        CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
        registry.addNamedTemplate(SCORECARD_MINING_TEMPLATE_NAME,ct);
        inputStream = Scorecard.class.getResourceAsStream(SCORECARD_OUTPUT_POJO_TEMPLATE);
        ct = TemplateCompiler.compileTemplate(inputStream);
        registry.addNamedTemplate("ScoreCardOutputTemplate",ct);
        return registry;
    }

    public String getMiningPojoClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("ScoreCardData"),true);
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
    public String getMiningPojoTemplateName() {
        return SCORECARD_MINING_TEMPLATE_NAME;
    }
}
