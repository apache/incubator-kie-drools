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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.dmg.pmml.pmml_4_2.descr.Attribute;
import org.dmg.pmml.pmml_4_2.descr.Characteristic;
import org.dmg.pmml.pmml_4_2.descr.Characteristics;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.kie.pmml.pmml_4_2.PMML4Model;
import org.kie.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.TemplateRuntimeError;


public class ScorecardModel extends AbstractModel<Scorecard> {
    private static String SCORECARD_MINING_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/scorecard/scorecardDataClass.mvel";
    private static String SCORECARD_OUTPUT_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/scorecard/scorecardOutputClass.mvel";
    private static String SCORECARD_RULE_UNIT_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/scorecard/scorecardRuleUnit.mvel";
    private static String INIT_SCORECARD_ROWS_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/rules/initializeScorecardRows.mvel";
    private static String SCORECARD_APPLIER_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/scorecard/scorecardApplierClass.mvel";

    private static String INIT_SCORECARD_ROWS_ID = "initializeScorecardRows";
    private static String SCORECARD_APPLIER_ID = "applyScorecardModel";

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

    @Override
    public Map.Entry<String, String> getModelApplierClass() {
        Map<String, String> applierClasses = new HashMap<>();
        String className = this.getModelApplierClassName();
        String miningPojoClassName = PMML_JAVA_PACKAGE_NAME + "." + this.getMiningPojoClassName();
        templateRegistry = addTemplateToRegistry(SCORECARD_APPLIER_ID, SCORECARD_APPLIER_TEMPLATE, templateRegistry);
        Characteristics characters = this.rawModel.getExtensionsAndCharacteristicsAndMiningSchemas().stream()
                                                  .filter(se -> se instanceof Characteristics)
                                                  .findFirst()
                                                  .map(se -> {
                                                      return (Characteristics) se;
                                                  })
                                                  .orElse(null);
        List<Characteristic> characteristics = characters != null ? characters.getCharacteristics() : null;
        Map<String, Object> params = new HashMap<>();
        params.put("modelName", this.getModelId());
        params.put("packageName", this.getModelPackageName());
        params.put("className", className);
        params.put("miningPojoClass", miningPojoClassName);
        params.put("initialScore", this.rawModel.getInitialScore());
        params.put("characteristics", characteristics);
        params.put("enableRC", this.rawModel.getUseReasonCodes());
        params.put("pointsBelow", "pointsBelow".equals(this.rawModel.getReasonCodeAlgorithm()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TemplateRuntime.execute(templateRegistry.getNamedTemplate(SCORECARD_APPLIER_ID),
                                    null,
                                    new MapVariableResolverFactory(params),
                                    baos);
        } catch (TemplateRuntimeError tre) {
            // need to figure out logging here
            return null;
        }
        String rule = new String(baos.toByteArray());
        applierClasses.put(this.getModelPackageName() + "." + className, rule);
        return applierClasses.entrySet().stream().findFirst().orElse(null);
    }

    @Override
    public Map.Entry<String, String> getModelInitializerClass() {
        Map<String, String> initializerClasses = new HashMap<>();
        String className = this.getModelInitializationClassName();
        templateRegistry = addTemplateToRegistry(INIT_SCORECARD_ROWS_ID, INIT_SCORECARD_ROWS_TEMPLATE, templateRegistry);
        Map<String, Object> params = new HashMap<>();
        params.put("modelName", this.getModelId());
        params.put("packageName", this.getModelPackageName());
        params.put("className", className);
        params.put("modelBaseline", this.rawModel.getBaselineScore());
        Characteristics chars = rawModel.getExtensionsAndCharacteristicsAndMiningSchemas().stream()
                                        .filter(ecm -> ecm instanceof Characteristics)
                                        .map(ecm -> {
                                            return (Characteristics) ecm;
                                        })
                                        .findFirst()
                                        .orElse(null);
        params.put("characteristics", chars.getCharacteristics());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TemplateRuntime.execute(templateRegistry.getNamedTemplate(INIT_SCORECARD_ROWS_ID),
                                    null,
                                    new MapVariableResolverFactory(params),
                                    baos);
        } catch (TemplateRuntimeError tre) {
            // need to figure out logging here
            return null;
        }
        String rule = new String(baos.toByteArray());
        initializerClasses.put(this.getModelPackageName() + "." + className, rule);
        return initializerClasses.entrySet().stream().findFirst().orElse(null);
    }


    @Override
    public Map<String, String> getExecutableModelRules() {
        Map<String, String> rules = new HashMap<>();
        List<Characteristic> karacteristics = null;
        Optional<Characteristics> characteristics = this.rawModel.getExtensionsAndCharacteristicsAndMiningSchemas().stream()
                                                                 .filter(se -> se instanceof Characteristics)
                                                                 .map(se -> {
                                                                     return (Characteristics) se;
                                                                 })
                                                                 .findFirst();
        karacteristics = characteristics.isPresent() ? characteristics.get().getCharacteristics() : null;
        if (karacteristics != null && !karacteristics.isEmpty()) {
            StringBuilder bldr = new StringBuilder("PartialScore_" + this.getModelId());
            int baseLength = bldr.length();
            karacteristics.forEach(chs -> {
                bldr.setLength(baseLength);
                bldr.append("_").append(chs.getName());
                int chsLength = bldr.length();
                int count = 0;
                for (Iterator<Attribute> iter = chs.getAttributes().iterator(); iter.hasNext(); count++) {
                    bldr.setLength(chsLength);
                    bldr.append("_").append(count);
                    Attribute attrib = iter.next();
                    String pred = helper.getPredicate(attrib);
                }
            });
        }
        return rules;
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
    public String getRuleUnitClassName() {
    	return helper.compactAsJavaId(this.getModelId().concat("ScorecardRuleUnit"), true);
    }

    @Override
    public String getModelInitializationClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("ScorecardInitializer"), true);
    }

    @Override
    public String getModelApplierClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("ScorecardModelApplier"), true);
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
	
	@Override
	protected void addRuleUnitTemplateToRegistry(TemplateRegistry registry) {
        //        testRunExecutableRule();
		InputStream inputStream = Scorecard.class.getResourceAsStream(SCORECARD_RULE_UNIT_TEMPLATE);
		if (inputStream != null) {
			CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
			if (ct != null) {
				registry.addNamedTemplate(getRuleUnitTemplateName(), ct);
			}
		}
	}
}
