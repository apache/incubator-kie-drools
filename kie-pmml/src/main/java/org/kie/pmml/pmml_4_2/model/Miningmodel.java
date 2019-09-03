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

import org.dmg.pmml.pmml_4_2.descr.DataDictionary;
import org.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;
import org.dmg.pmml.pmml_4_2.descr.MININGFUNCTION;
import org.dmg.pmml.pmml_4_2.descr.MiningModel;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.dmg.pmml.pmml_4_2.descr.Segmentation;
import org.kie.pmml.pmml_4_2.PMML4Model;
import org.kie.pmml.pmml_4_2.PMML4Unit;
import org.kie.pmml.pmml_4_2.model.mining.MiningSegment;
import org.kie.pmml.pmml_4_2.model.mining.MiningSegmentation;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.TemplateRuntimeError;

public class Miningmodel extends AbstractModel<MiningModel> {
    private static String MINING_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/mining/miningMiningPojo.mvel";
    private static String OUTPUT_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/mining/miningOutputPojo.mvel";
    private static String RULE_UNIT_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/mining/miningRuleUnit.mvel";

    private static String INIT_MINING_SEGS_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/rules/initializeMiningSegments.mvel";
    private static String INIT_MINING_SEGS_ID = "initializeMiningSegments";

    private static String MODEL_APPLIER_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/mining/miningModelApplier.mvel";
    private static String MODEL_APPLIER_ID = "miningModelApplier";

    private Map<String,PMML4Model> childModels;
    private MiningSegmentation segmentation;
    private MININGFUNCTION functionName;
    private String algorithmName;
    private boolean scoreable;

    public Miningmodel(String modelId, MiningModel model, PMML4Model parentModel, PMML4Unit owner) {
        super(modelId, PMML4ModelType.MINING, owner, parentModel, model);
        this.scoreable = model.getIsScorable();
        initChildModels();
    }
    
    @Override
    public Map<String,PMML4Model> getChildModels() {
        return childModels != null && !childModels.isEmpty() ? new HashMap<>(childModels) : new HashMap<>();
    }
    
    protected void initChildModels() {
        childModels = new HashMap<>();
        Iterator<Serializable> extenIter = rawModel.getExtensionsAndMiningSchemasAndOutputs().iterator();
        segmentation = null;
        while (extenIter.hasNext() && segmentation == null) {
            Object obj = extenIter.next();
            if (obj instanceof Segmentation) {
                segmentation = new MiningSegmentation(this,(Segmentation)obj);
            }
        }
        
        if (segmentation != null) {
            List<MiningSegment> segments = segmentation.getMiningSegments();
            DataDictionary dd = this.getDataDictionary();
            for (MiningSegment seg : segmentation.getMiningSegments()) {
                childModels.put(seg.getModel().getModelId(), seg.getModel());
            }
        }
        
    }
    
    private PMMLOutputField getChildOutputField( PMML4Model parentModel, String fieldName) {
        PMMLOutputField output = null;
        for (Iterator<PMML4Model> childIter = parentModel.getChildModels().values().iterator(); childIter.hasNext() && output == null;) {
            PMML4Model child = childIter.next();
            List<PMMLOutputField> fields = child.getOutputFields();
            if (fields != null && !fields.isEmpty()) {
                for (Iterator<PMMLOutputField> fieldIter = fields.iterator(); fieldIter.hasNext() && output == null;) {
                    PMMLOutputField fld = fieldIter.next();
                    if (fieldName.equals(fld.getName())) {
                        output = fld;
                    }
                }
            }
            if (child instanceof Miningmodel && output == null) {
                output = getChildOutputField(child,fieldName);
            }
        }
        return output;
    }
    
    public String getTargetField() {
        return this.getMiningFields().stream()
        .filter(mf -> mf.getFieldUsageType() == FIELDUSAGETYPE.TARGET || mf.getFieldUsageType() == FIELDUSAGETYPE.PREDICTED)
        .map(mf -> { return helper.compactAsJavaId(mf.getName(),true); })
        .findFirst().orElse(null);
    }
    
    
    @Override
    public PMMLOutputField findOutputField( String fieldName) {
        return getChildOutputField(this,fieldName);
    }
    
    @Override
    public List<PMMLMiningField> getMiningFields() {
        List<PMMLMiningField> fields = super.getMiningFields();
        for (PMMLMiningField field : fields) {
            String type = field.getType();
            if (type == null || type.trim().isEmpty()) {
                fields.remove(field);
            }
        }
        if (this.segmentation != null) {
            segmentation.getMiningSegments().stream()
                    .flatMap(ms -> ms.getModel().getMiningFields().stream() )
                    .filter(fld -> !fields.contains(fld))
                    .forEach(fields::add);
        }
        return fields;
    }

    @Override
    public Map.Entry<String, String> getModelInitializerClass() {
        Map<String, String> initializerClasses = new HashMap<>();
        String className = this.getModelInitializationClassName();
        templateRegistry = addTemplateToRegistry(INIT_MINING_SEGS_ID, INIT_MINING_SEGS_TEMPLATE, templateRegistry);
        Map<String, Object> params = new HashMap<>();
        params.put("modelName", this.getModelId());
        params.put("packageName", this.getModelPackageName());
        params.put("className", className);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TemplateRuntime.execute(templateRegistry.getNamedTemplate(INIT_MINING_SEGS_ID),
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
    public Map.Entry<String, String> getModelApplierClass() {
        Map<String, String> applierClasses = new HashMap<>();
        String className = this.getModelApplierClassName();
        String miningPojoClassName = PMML_JAVA_PACKAGE_NAME + "." + this.getMiningPojoClassName();
        templateRegistry = addTemplateToRegistry(MODEL_APPLIER_ID, MODEL_APPLIER_TEMPLATE, templateRegistry);
        Map<String, Object> params = new HashMap<>();
        params.put("modelName", this.getModelId());
        params.put("packageName", this.getModelPackageName());
        params.put("className", className);
        params.put("segments", this.getSegmentation().getMiningSegments());
        params.put("modelMethod", this.getSegmentation().getMultipleModelHandling());
        params.put("miningPojoClass", miningPojoClassName);
        params.put("targetField", this.getTargetField());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TemplateRuntime.execute(templateRegistry.getNamedTemplate(MODEL_APPLIER_ID),
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
    public String getMiningPojoClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("MiningModelData"), true);
    }
    
    @Override
    public String getOutputPojoClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("MiningModelOutput"), true);
    }
    
    @Override
    public String getRuleUnitClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("MiningModelRuleUnit"),true);
    }

    @Override
    public String getModelInitializationClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("MiningModelInitializer"), true);
    }

    @Override
    public String getModelApplierClassName() {
        return helper.compactAsJavaId(this.getModelId().concat("MiningModelApplier"), true);
    }

    @Override
    public MiningSchema getMiningSchema() {
        for (Serializable serializable: rawModel.getExtensionsAndMiningSchemasAndOutputs()) {
            if (serializable instanceof MiningSchema) {
                return (MiningSchema)serializable;
            }
        }
        return null;
    }

    @Override
    public Output getOutput() {
        for (Serializable serializable: rawModel.getExtensionsAndMiningSchemasAndOutputs()) {
            if (serializable instanceof Output) {
                return (Output)serializable;
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

    
    @Override
    protected void addRuleUnitTemplateToRegistry(TemplateRegistry registry) {
        InputStream inputStream = Scorecard.class.getResourceAsStream(RULE_UNIT_TEMPLATE);
        if (inputStream != null) {
            CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
            if (ct != null) {
                registry.addNamedTemplate(getRuleUnitTemplateName(), ct);
            }
        }
    }

    public MININGFUNCTION getFunctionName() {
        return functionName;
    }

    public void setFunctionName(MININGFUNCTION functionName) {
        this.functionName = functionName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public boolean isScoreable() {
        return scoreable;
    }

    public void setScoreable(boolean scoreable) {
        this.scoreable = scoreable;
    }
    
    public MiningSegmentation getSegmentation() {
        return this.segmentation;
    }

    public String generateRules() {
        StringBuilder bldr = new StringBuilder();
        if (this.scoreable && this.segmentation != null) {
            bldr.append(segmentation.generateSegmentationRules());
        }
        return bldr.toString();
    }
    
}
