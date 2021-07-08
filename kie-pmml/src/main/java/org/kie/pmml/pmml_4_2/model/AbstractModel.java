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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dmg.pmml.pmml_4_2.descr.DATATYPE;
import org.dmg.pmml.pmml_4_2.descr.DataDictionary;
import org.dmg.pmml.pmml_4_2.descr.Extension;
import org.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.dmg.pmml.pmml_4_2.descr.RESULTFEATURE;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.pmml_4_2.PMML4Exception;
import org.kie.pmml.pmml_4_2.PMML4Helper;
import org.kie.pmml.pmml_4_2.PMML4Model;
import org.kie.pmml.pmml_4_2.PMML4Unit;
import org.kie.pmml.pmml_4_2.model.ExternalBeanRef.ModelUsage;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateError;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.TemplateRuntimeError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractModel<T> implements PMML4Model {

    private String modelId;
    private PMML4ModelType modelType;
    private PMML4Unit owner;
    private PMML4Model parentModel;
    protected T rawModel;
    protected Map<String, MiningField> miningFieldMap;
    protected Map<String, OutputField> outputFieldMap;
    protected List<ExternalBeanRef> externalMiningFields;
    private static Map<String, Integer> generatedModelIds;
    protected static PMML4Helper helper = new PMML4Helper();
    protected final static String MINING_TEMPLATE_NAME = "MiningSchemaPOJOTemplate";
    protected final static String OUTPUT_TEMPLATE_NAME = "OutputPOJOTemplate";
    protected final static String RULE_UNIT_TEMPLATE_NAME = "RuleUnitTemplate";
    protected static TemplateRegistry templateRegistry;
    private static String BASIC_OUTPUT_POJO_TEMPLATE = "/org/kie/pmml/pmml_4_2/templates/mvel/global/outputbean.mvel";
    public final static String PMML_JAVA_PACKAGE_NAME = "org.kie.pmml.pmml_4_2.model";
    private static final Logger logger = LoggerFactory.getLogger(AbstractModel.class);

    protected abstract void addMiningTemplateToRegistry(TemplateRegistry registry);

    protected abstract void addOutputTemplateToRegistry(TemplateRegistry registry);

    protected abstract void addRuleUnitTemplateToRegistry(TemplateRegistry registry);

    static {
        generatedModelIds = new HashMap<>();
        templateRegistry = new SimpleTemplateRegistry();
    }

    public AbstractModel(String modelId, PMML4ModelType modelType, PMML4Unit owner, T rawModel) {
        this.modelId = modelId;
        this.modelType = modelType;
        this.owner = owner;
        this.rawModel = rawModel;
        this.parentModel = null;
        initFieldMaps();
    }

    public AbstractModel(String modelId, PMML4ModelType modelType, PMML4Unit owner, PMML4Model parentModel, T rawModel) {
        this.modelId = modelId;
        this.modelType = modelType;
        this.owner = owner;
        this.rawModel = rawModel;
        this.parentModel = parentModel;
        initFieldMaps();
    }

    protected void initFieldMaps() throws IllegalArgumentException {
        initMiningFieldMap();
        initOutputFieldMap();
    }

    protected void initMiningFieldMap() throws IllegalArgumentException {
        MiningSchema schema = getMiningSchema();
        boolean addExternalBeanRefs = isUseExternalBeanRefs(schema);
        miningFieldMap = new HashMap<>();
        externalMiningFields = new ArrayList<>();
        for (MiningField field : schema.getMiningFields()) {
            miningFieldMap.put(field.getName(), field);
            if (addExternalBeanRefs) {
                Extension ext = getExternalClassInfo(field.getExtensions());
                if (ext != null) {
                    ExternalBeanRef ref;
                    try {
                        ref = new ExternalBeanRef(field.getName(), ext.getValue(), ModelUsage.MINING);
                        if (ExternalBeanDefinition.DEFAULT_BEAN_PKG.equals(ref.getBeanPackageName()) && this.getOwner().getRootPackage() != null) {
                            ref.setBeanPackageName(this.getOwner().getRootPackage());
                        }
                        externalMiningFields.add(ref);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Error while initializing the MiningField map. ",e);
                    }
                }
            }
        }
    }

    protected boolean isUseExternalBeanRefs(MiningSchema schema) {
        List<Extension> extensions = schema.getExtensions();
        if (extensions != null && !extensions.isEmpty()) {
            Optional<Extension> ext = extensions.stream()
                    .filter(e -> ("adapter".equals(e.getName()) && "BEAN".equals(e.getValue())))
                    .findFirst();
            return ext.isPresent();
        }
        return false;
    }

    protected Extension getExternalClassInfo(List<Extension> extensions) {
        Extension ext = null;
        if (extensions != null && !extensions.isEmpty()) {
            ext = extensions.stream().filter(e -> "externalClass".equals(e.getName())).findFirst().orElse(null);
        }
        return ext;
    }

    protected void initOutputFieldMap() {
        Output output = getOutput();
        outputFieldMap = new HashMap<>();
        if (output != null) {
            for (OutputField field : output.getOutputFields()) {
                outputFieldMap.put(field.getName(), field);
            }
        }
    }

    protected MiningField getValidMiningField(PMMLDataField dataField) {
        if (miningFieldMap.containsKey(dataField.getName())) {
            return miningFieldMap.get(dataField.getName());
        }
        return null;
    }

    protected String getMiningPojoTemplateName() {
        return this.modelType.toString() + "_" + MINING_TEMPLATE_NAME;
    }

    protected String getOutputPojoTemplateName() {
        return this.modelType.toString() + "_" + OUTPUT_TEMPLATE_NAME;
    }

    protected String getRuleUnitTemplateName() {
        return this.modelType.toString() + "_" + RULE_UNIT_TEMPLATE_NAME;
    }

    public String getModelPackageName() {
        String pkgName = "";
        if (this.getParentModel() != null) {
            pkgName = this.getParentModel().getModelPackageName();
        } else {
            pkgName = this.getOwner().getRootPackage();
        }
        return pkgName.concat("." + this.getModelId());
    }

    public Map.Entry<String, String> getMappedMiningPojo() {
        Map<String, String> result = new HashMap<>();
        if (!templateRegistry.contains(getMiningPojoTemplateName())) {
            this.addMiningTemplateToRegistry(templateRegistry);
        }
        List<PMMLMiningField> dataFields = this.getMiningFields();
        Map<String, Object> vars = new HashMap<>();
        String className = this.getMiningPojoClassName();
        vars.put("pmmlPackageName", PMML_JAVA_PACKAGE_NAME);
        vars.put("className", className);
        vars.put("imports", new ArrayList<>());
        vars.put("dataFields", dataFields);
        vars.put("modelName", this.getModelId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TemplateRuntime.execute(templateRegistry.getNamedTemplate(this.getMiningPojoTemplateName()),
                                    null,
                                    new MapVariableResolverFactory(vars),
                                    baos);
        } catch (TemplateRuntimeError tre) {
            // need to figure out logging here
            return null;
        }
        result.put(PMML_JAVA_PACKAGE_NAME + "." + className, new String(baos.toByteArray()));
        return result.entrySet().iterator().next();
    }

    @Override
    public Map.Entry<String, String> getMappedOutputPojo() {
        Map<String, String> result = new HashMap<>();
        if (!templateRegistry.contains(getOutputPojoTemplateName())) {
            this.addOutputTemplateToRegistry(templateRegistry);
        }
        List<PMMLOutputField> dataFields = this.getOutputFields();
        Map<String, Object> vars = new HashMap<>();
        String className = this.getOutputPojoClassName();
        vars.put("pmmlPackageName", PMML_JAVA_PACKAGE_NAME);
        vars.put("className", className);
        vars.put("imports", new ArrayList<>());
        vars.put("dataFields", dataFields);
        vars.put("modelName", this.getModelId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TemplateRuntime.execute(templateRegistry.getNamedTemplate(this.getOutputPojoTemplateName()),
                                    null,
                                    new MapVariableResolverFactory(vars),
                                    baos);
        } catch (TemplateError te) {
            return null;
        } catch (TemplateRuntimeError tre) {
            // need to figure out logging here
            return null;
        }
        result.put(className, new String(baos.toByteArray()));
        return result.entrySet().iterator().next();
    }

    protected List<ExternalBeanDefinition> getExternalMiningClasses() {
        List<ExternalBeanDefinition> list = null;
        if (externalMiningFields != null && !externalMiningFields.isEmpty()) {
            list = externalMiningFields.stream()
                    .map(ref -> ref.getBeanDefinition())
                    .distinct()
                    .collect(Collectors.toList());
        }
        // DO NOT USE Collections.emptyList() here!!! emptyList() returns an EmptyList instance
        // which is an internal class from Collections class, therefore not accessible
        // by reflection - when mvel parses a RuleUnit template and there is
        // a size call, it tries to invoke the method using reflection
        return list != null ? list : new ArrayList<>(0);
    }

    public List<ExternalBeanRef> getExternalMiningFields() {
        return this.externalMiningFields;
    }

    public String getExternalBeansMiningRules() {
        if (!templateRegistry.contains("ExternalBeansMiningRules")) {
            InputStream istrm = AbstractModel.class.getResourceAsStream("/org/kie/pmml/pmml_4_2/templates/mvel/global/externalBeanInput.mvel");
            if (istrm != null) {
                CompiledTemplate ct = TemplateCompiler.compileTemplate(istrm);
                templateRegistry.addNamedTemplate("ExternalBeansMiningRules", ct);
            }
        }
        Map<String, Object> vars = new HashMap<>();
        vars.put("externalBeanRefs", externalMiningFields);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TemplateRuntime.execute(templateRegistry.getNamedTemplate("ExternalBeansMiningRules"),
                                    null,
                                    new MapVariableResolverFactory(vars),
                                    baos);
        } catch (TemplateError te) {
            return null;
        }
        return new String(baos.toByteArray());
    }

    @Override
    public Map.Entry<String, String> getMappedRuleUnit() throws PMML4Exception {
        Map<String, String> result = new HashMap<>();
        if (!templateRegistry.contains(this.getRuleUnitTemplateName())) {
            this.addRuleUnitTemplateToRegistry(templateRegistry);
        }
        Map<String, Object> vars = new HashMap<>();
        String className = this.getRuleUnitClassName();
        vars.put("pmmlPackageName", this.getModelPackageName());
        vars.put("className", className);
        vars.put("pojoInputClassName", PMMLRequestData.class.getName());
        List<ExternalBeanDefinition> beanDefs = getExternalMiningClasses();
        if (beanDefs != null) {
            vars.put("externMiningBeans", getExternalMiningClasses());
        }
        if (this instanceof Miningmodel) {
            vars.put("miningPojoClassName", this.getMiningPojoClassName());
        }
        processTemplate(this.getRuleUnitTemplateName(), vars, this.getModelPackageName() + "." + className, result);

        return result.isEmpty() ? null : result.entrySet().iterator().next();
    }

    @Override
    public List<PMMLMiningField> getMiningFields() {
        PMML4Unit rootOwner = getOwner();
        List<PMMLMiningField> fields = new ArrayList<>();
        Map<String, MiningField> excludesTargetMap = miningFieldMap;
        for (String key : excludesTargetMap.keySet()) {
            PMMLDataField df = rootOwner.findDataDictionaryEntry(key);
            MiningField mf = miningFieldMap.get(key);
            if (df != null) {
                fields.add(new PMMLMiningField(mf, df.getRawDataField(), this.getModelId(), true));
            } else {
                PMMLMiningField fld = new PMMLMiningField(mf, this.getModelId());

                if (this.getParentModel() != null) {
                    PMML4Model ultimateParentModel = this.getParentModel();
                    if (ultimateParentModel instanceof Miningmodel) {
                        while (ultimateParentModel.getParentModel() != null) {
                            ultimateParentModel = ultimateParentModel.getParentModel();
                        }

                        PMMLOutputField ofld = ((Miningmodel) ultimateParentModel).findOutputField(fld.getName());
                        if (ofld != null) {
                            fld.setType(ofld.getType());
                            fields.add(fld);
                        }
                    }
                }
            }
        }
        return fields;
    }

    @Override
    public PMMLMiningField findMiningField(String fieldName) {
        List<PMMLMiningField> candidates = getMiningFields();
        for (PMMLMiningField fld : candidates) {
            if (fld.getName().equals(fieldName)) {
                return fld;
            }
        }
        return null;
    }

    @Override
    public PMMLOutputField findOutputField(String fieldName) {
        List<PMMLOutputField> candidates = getOutputFields();
        for (PMMLOutputField fld : candidates) {
            if (fld.getName().equals(fieldName)) {
                return fld;
            }
        }
        return null;
    }

    @Override
    public List<PMMLOutputField> getOutputFields() {
        List<PMMLOutputField> fields = new ArrayList<>();
        for (String key : outputFieldMap.keySet()) {
            OutputField of = outputFieldMap.get(key);
            fields.add(new PMMLOutputField(of, null, this.getModelId()));
        }
        Map<String, MiningField> includeFromMining = getFilteredMiningFieldMap(true, FIELDUSAGETYPE.PREDICTED, FIELDUSAGETYPE.TARGET);
        Map<String, PMMLDataField> dataDictionary = getOwner().getDataDictionaryMap();
        if (includeFromMining != null && !includeFromMining.isEmpty()) {
            for (String key : includeFromMining.keySet()) {
                MiningField field = includeFromMining.get(key);
                PMMLDataField df = dataDictionary.get(key);
                fields.add(new PMMLOutputField(field, df.getRawDataField(), this.getModelId()));
            }
        }
        return fields;
    }

    @Override
    public PMML4ModelType getModelType() {
        return this.modelType;
    }

    @Override
    public String getModelId() {
        if (this.modelId == null) {
            this.modelId = generateModelId();
        }
        return helper.compactAsJavaId(this.modelId, true);
    }

    @Override
    public PMML4Unit getOwner() {
        PMML4Unit ownedBy = this.owner;
        if (ownedBy == null) {
            PMML4Model parent = this.getParentModel();
            if (parent != null) {
                ownedBy = parent.getOwner();
            }
        }
        return ownedBy;
    }

    @Override
    public List<MiningField> getRawMiningFields() {
        return (miningFieldMap != null && !miningFieldMap.isEmpty()) ?
                new ArrayList<>(miningFieldMap.values()) :
                new ArrayList<>();
    }

    @Override
    public List<OutputField> getRawOutputFields() {
        return (outputFieldMap != null && !outputFieldMap.isEmpty()) ?
                new ArrayList<>(outputFieldMap.values()) :
                new ArrayList<>();
    }
    
    private void setSimilarVarValues(String className, String name, String displayValue, Map<String, Object> vars) {
        vars.put("name", name);
        vars.put("className", className);
        vars.put("displayValue", displayValue);
        vars.put("packageName", this.getModelPackageName());
        vars.put("modelId", this.getModelId());
    }

    private void processTemplate(String templateId,
                                 Map<String, Object> vars,
                                 String resultKey,
                                 Map<String, String> results) throws PMML4Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            TemplateRuntime.execute(templateRegistry.getNamedTemplate(templateId),
                                    null,
                                    new MapVariableResolverFactory(vars),
                                    baos);
            results.put(resultKey, new String(baos.toByteArray()));
        } catch (Throwable cause) {
            throw new PMML4Exception(this.getModelId(), "Error applying template - " + templateId, cause);
        }
    }

    @Override
    public Map<String, String> getOutputTargetPojos() throws PMML4Exception {
    	String templateId = getModelId()+"_OUTPUT_BEAN_TEMPLATE";
    	if (!templateRegistry.contains(templateId)) {
    		addTemplateToRegistry(templateId, BASIC_OUTPUT_POJO_TEMPLATE, templateRegistry);
    	}
    	Map<String,String> results = new HashMap<>();
		String packageName = this.getModelPackageName();
    	Map<String,MiningField> fields = this.getFilteredMiningFieldMap(true, FIELDUSAGETYPE.PREDICTED, FIELDUSAGETYPE.TARGET);
    	if (fields != null && !fields.isEmpty()) {
            for (MiningField mf : fields.values()) {
                Map<String, Object> vars = new HashMap<>();
                String className = helper.compactUpperCase(mf.getName());
                setSimilarVarValues(className, mf.getName(), className, vars);
                PMMLDataField ddf = getOwner().findDataDictionaryEntry(mf.getName());
                if (ddf != null) {
                    vars.put("typeName", ddf.getType());
                } else {
                    vars.put("typeName", "String");
                }
                processTemplate(templateId, vars, packageName + "." + className, results);
            }
    	}
    	Map<String,OutputField> flds = this.getOutputFieldMap();
    	if (flds != null && !flds.isEmpty()) {
            for (OutputField of : flds.values()) {
                Map<String, Object> vars = new HashMap<>();
                String className = helper.compactUpperCase(of.getName());
                setSimilarVarValues(className, of.getName(), className, vars);
                String typeName = "String";
                if (of.getDataType() != null) {
                    typeName = helper.mapDatatype(of.getDataType(), true);
                } else {
                    if (RESULTFEATURE.PROBABILITY.equals(of.getFeature())) {
                        typeName = helper.mapDatatype(DATATYPE.DOUBLE, true);
                    } else if (of.getTargetField() != null) {
                        PMMLDataField ddf = getOwner().findDataDictionaryEntry(of.getTargetField());
                        if (ddf != null) {
                            typeName = ddf.getType();
                        }
                    }
                }
                vars.put("typeName", typeName);
                processTemplate(templateId, vars, packageName + "." + className, results);
            }
    	}
    	return results;
    }

    /**
     * Default method returns an empty Map
     */
    @Override
    public Map<String, PMML4Model> getChildModels() {
        return new HashMap<>();
    }

    public PMML4Model getParentModel() {
        return this.parentModel;
    }

    public void setParentModel(PMML4Model parentModel) {
        this.parentModel = parentModel;
    }

    public Map<String, MiningField> getMiningFieldMap() {
        return new HashMap<>(miningFieldMap);
    }

    public Map<String, OutputField> getOutputFieldMap() {
        return new HashMap<>(outputFieldMap);
    }

    public Map<String, MiningField> getFilteredMiningFieldMap(boolean includeFiltered, FIELDUSAGETYPE... filterTypes) {
        Map<String, MiningField> mfm = new HashMap<>();
        List<FIELDUSAGETYPE> filteredTypes = Arrays.asList(filterTypes);
        for (String key : miningFieldMap.keySet()) {
            MiningField field = miningFieldMap.get(key);
            FIELDUSAGETYPE usageType = field.getUsageType();
            if ((includeFiltered && filteredTypes.contains(usageType))
                    || (!includeFiltered && !filteredTypes.contains(usageType))) {
                mfm.put(key, field);
            }
        }
        return mfm;
    }

    public List<PMMLMiningField> getActiveMiningFields() {
        List<PMMLMiningField> activeMiningFields = new ArrayList<>();
        List<PMMLMiningField> allMiningFields = this.getMiningFields();
        for (PMMLMiningField field : allMiningFields) {
            if (field.getFieldUsageType() == FIELDUSAGETYPE.ACTIVE) {
                activeMiningFields.add(field);
            }
        }
        return activeMiningFields;
    }

    /**
     * A method that tries to generate a model identifier
     * for those times when models arrive without an identifier
     * @return The String value that is to be used to identify the model
     */
    private String generateModelId() {
        String mt = this.modelType.toString();
        StringBuilder mid = new StringBuilder(mt);
        Integer lastId = null;
        if (generatedModelIds.containsKey(mt)) {
            lastId = generatedModelIds.get(mt);
        } else {
            lastId = Integer.valueOf(-1);
        }
        lastId++;
        mid.append(lastId);
        generatedModelIds.put(mt, lastId);
        return mid.toString();
    }

    @Override
    public String getModelRuleUnitName() {
        return this.getModelPackageName() + "." + this.getRuleUnitClassName();
    }

    public DataDictionary getDataDictionary() {
        if (this.getParentModel() == null) {
            return this.getOwner().getRawPMML().getDataDictionary();
        }
        return this.getParentModel().getDataDictionary();
    }

    public Serializable getRawModel() {
        return (Serializable) this.rawModel;
    }


	protected synchronized TemplateRegistry addTemplateToRegistry(String templateId, String templatePath,
			TemplateRegistry registry) {
		if (!registry.contains(templateId)) {
			InputStream istrm = Scorecard.class.getResourceAsStream(templatePath);
			if (istrm != null) {
				CompiledTemplate ct = TemplateCompiler.compileTemplate(istrm);
				registry.addNamedTemplate(templateId, ct);
			}
		}
		return registry;
	}
}
