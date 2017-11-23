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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dmg.pmml.pmml_4_2.descr.DataDictionary;
import org.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.drools.pmml.pmml_4_2.PMML4Helper;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateError;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.TemplateRuntimeError;

public abstract class AbstractModel<T> implements PMML4Model {
    private String modelId;
    private PMML4ModelType modelType;
    private PMML4Unit owner;
    private PMML4Model parentModel;
    protected T rawModel;
    protected Map<String, MiningField> miningFieldMap;
    protected Map<String, OutputField> outputFieldMap;
    private static Map<String,Integer> generatedModelIds;
    protected static PMML4Helper helper = new PMML4Helper();
	protected final static String MINING_TEMPLATE_NAME = "MiningSchemaPOJOTemplate";
	protected final static String OUTPUT_TEMPLATE_NAME = "OutputPOJOTemplate";
	protected static TemplateRegistry templateRegistry;
    public final static String PMML_JAVA_PACKAGE_NAME = "org.drools.pmml.pmml_4_2.model";

    protected abstract void addMiningTemplateToRegistry(TemplateRegistry registry);
    protected abstract void addOutputTemplateToRegistry(TemplateRegistry registry);

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
    
    protected void initFieldMaps() {
    	initMiningFieldMap();
    	initOutputFieldMap();
    }

    protected void initMiningFieldMap() {
        MiningSchema schema = getMiningSchema();
        miningFieldMap = new HashMap<>();
        for (MiningField field: schema.getMiningFields()) {
            miningFieldMap.put(field.getName(), field);
        }
    }

    protected void initOutputFieldMap() {
        Output output = getOutput();
        outputFieldMap = new HashMap<>();
        if (output != null) {
	        for (OutputField field: output.getOutputFields()) {
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
    	return this.modelType.toString()+"_"+MINING_TEMPLATE_NAME;
    }
    
    protected String getOutputPojoTemplateName() {
    	return this.modelType.toString()+"_"+OUTPUT_TEMPLATE_NAME;
    }
    
    public Map.Entry<String, String> getMappedMiningPojo() {
    	Map<String,String> result = new HashMap<>();
    	if (!templateRegistry.contains(getMiningPojoTemplateName())) {
    		this.addMiningTemplateToRegistry(templateRegistry);
    	}
        List<PMMLMiningField> dataFields = this.getMiningFields();
        Map<String, Object> vars = new HashMap<>();
        String className = this.getMiningPojoClassName();
        vars.put("pmmlPackageName",PMML_JAVA_PACKAGE_NAME);
        vars.put("className",className);
        vars.put("imports",new ArrayList<>());
        vars.put("dataFields",dataFields);
        vars.put("modelName", this.getModelId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
	        TemplateRuntime.execute( templateRegistry.getNamedTemplate(this.getMiningPojoTemplateName()),
	                                 null,
	                                 new MapVariableResolverFactory(vars),
	                                 baos );
        } catch (TemplateRuntimeError tre) {
        	// need to figure out logging here
        	return null;
        }
        result.put(className, new String(baos.toByteArray()));
        return result.entrySet().iterator().next();
    }

    @Override
    public String getMiningPojo() {
    	if (!templateRegistry.contains(getMiningPojoTemplateName())) {
    		this.addMiningTemplateToRegistry(templateRegistry);
    	}
        List<PMMLMiningField> dataFields = this.getMiningFields();
        Map<String, Object> vars = new HashMap<>();
        String className = this.getMiningPojoClassName();
        vars.put("pmmlPackageName",PMML_JAVA_PACKAGE_NAME);
        vars.put("className",className);
        vars.put("imports",new ArrayList<>());
        vars.put("dataFields",dataFields);
        vars.put("modelName", this.getModelId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
	        TemplateRuntime.execute( templateRegistry.getNamedTemplate(this.getMiningPojoTemplateName()),
	                                 null,
	                                 new MapVariableResolverFactory(vars),
	                                 baos );
        } catch (TemplateRuntimeError tre) {
        	// need to figure out logging here
        	return null;
        }
        return new String(baos.toByteArray());
    }
    
    public Map.Entry<String, String> getMappedOutputPojo() {
    	Map<String,String> result = new HashMap<>();
    	if (!templateRegistry.contains(getOutputPojoTemplateName())) {
    		this.addOutputTemplateToRegistry(templateRegistry);
    	}
    	List<PMMLOutputField> dataFields = this.getOutputFields();
    	Map<String,Object> vars = new HashMap<>();
    	String className = this.getOutputPojoClassName();
    	vars.put("pmmlPackageName", PMML_JAVA_PACKAGE_NAME);
    	vars.put("className", className);
    	vars.put("imports", new ArrayList<>());
    	vars.put("dataFields", dataFields);
    	vars.put("modelName", this.getModelId());
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	try {
    		TemplateRuntime.execute( templateRegistry.getNamedTemplate(this.getOutputPojoTemplateName()),
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
    
    @Override
    public String getOutputPojo() {
    	if (!templateRegistry.contains(getOutputPojoTemplateName())) {
    		this.addOutputTemplateToRegistry(templateRegistry);
    	}
    	List<PMMLOutputField> dataFields = this.getOutputFields();
    	Map<String,Object> vars = new HashMap<>();
    	String className = this.getOutputPojoClassName();
    	vars.put("pmmlPackageName", PMML_JAVA_PACKAGE_NAME);
    	vars.put("className", className);
    	vars.put("imports", new ArrayList<>());
    	vars.put("dataFields", dataFields);
    	vars.put("modelName", this.getModelId());
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	try {
    		TemplateRuntime.execute( templateRegistry.getNamedTemplate(this.getOutputPojoTemplateName()),
    								 null,
    								 new MapVariableResolverFactory(vars),
    								 baos);
    	} catch (TemplateRuntimeError tre) {
    		// need to figure out logging here
    		return null;
    	}
    	return new String(baos.toByteArray());
    }
    
    @Override
    public List<PMMLMiningField> getMiningFields() {
		List<PMMLMiningField> fields = new ArrayList<>();
		Map<String,MiningField> excludesTargetMap = 
				getFilteredMiningFieldMap(false, FIELDUSAGETYPE.TARGET);
		Map<String, PMMLDataField> dataDictionary = getOwner().getDataDictionaryMap();
		for (String key: excludesTargetMap.keySet()) {
			PMMLDataField df = dataDictionary.get(key);
			MiningField mf = miningFieldMap.get(key);
			if (df != null) {
				fields.add(new PMMLMiningField(mf, df.getRawDataField(), this.getModelId(), true));
			} else {
				fields.add(new PMMLMiningField(mf, this.getModelId()));
			}
		}
		return fields;
    }
    
    @Override
    public List<PMMLOutputField> getOutputFields() {
    	List<PMMLOutputField> fields = new ArrayList<>();
    	for (String key: outputFieldMap.keySet()) {
    		OutputField of = outputFieldMap.get(key);
    		fields.add(new PMMLOutputField(of, null, this.getModelId()));
    	}
    	Map<String,MiningField> includeFromMining = getFilteredMiningFieldMap(true, FIELDUSAGETYPE.PREDICTED, FIELDUSAGETYPE.TARGET);
    	Map<String, PMMLDataField> dataDictionary = getOwner().getDataDictionaryMap();
    	if (includeFromMining != null && !includeFromMining.isEmpty()) {
    		for (String key: includeFromMining.keySet()) {
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
        return this.modelId;
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
    
    /**
     * Default method returns an empty Map
     */
    @Override
    public Map<String,PMML4Model> getChildModels() {
    	return new HashMap<>();
    }
    
    public PMML4Model getParentModel() {
    	return this.parentModel;
    }
    
    public void setParentModel(PMML4Model parentModel) {
    	this.parentModel = parentModel;
    }

    public Map<String,MiningField> getMiningFieldMap() {
    	return new HashMap<>(miningFieldMap);
    }
    
    public Map<String,MiningField> getFilteredMiningFieldMap(boolean includeFiltered,FIELDUSAGETYPE...filterTypes) {
    	Map<String, MiningField> mfm = new HashMap<>();
    	List<FIELDUSAGETYPE> filteredTypes = Arrays.asList(filterTypes);
    	for (String key: miningFieldMap.keySet()) {
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
    		lastId = new Integer(-1);
    	}
    	lastId++;
    	mid.append(lastId);
    	generatedModelIds.put(mt, lastId);
    	return mid.toString();
    }
    
    public DataDictionary getDataDictionary() {
    	if (this.getParentModel() == null) {
    		return this.getOwner().getRawPMML().getDataDictionary();
    	}
    	return this.getParentModel().getDataDictionary();
    }
    
    public Serializable getRawModel() {
    	return (Serializable)this.rawModel;
    }
}
