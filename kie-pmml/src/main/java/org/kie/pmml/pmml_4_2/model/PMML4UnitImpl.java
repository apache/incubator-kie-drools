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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.dmg.pmml.pmml_4_2.descr.DataDictionary;
import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.drools.core.io.impl.ClassPathResource;
import org.kie.pmml.pmml_4_2.PMML4Model;
import org.kie.pmml.pmml_4_2.PMML4Unit;

public class PMML4UnitImpl implements PMML4Unit {
    private static PMML4ModelFactory modelFactory = PMML4ModelFactory.getInstance();
    private PMML rawPmml;
    private Map<String,PMML4Model> modelsMap;
    private Map<String, PMMLDataField> dataDictionaryMap;

    public PMML4UnitImpl(PMML rawPmml) {
        this.rawPmml = rawPmml;
        initFromPMML();
    }

    public PMML4UnitImpl(String pmmlSourcePath) {
        ClassPathResource resource = new ClassPathResource(pmmlSourcePath);
        try {
            JAXBContext context = JAXBContext.newInstance(PMML.class.getPackage().getName());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            this.rawPmml = (PMML)unmarshaller.unmarshal(resource.getInputStream());
            initFromPMML();
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to resolve the PMML from the given input",e);
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while attempting to resolve the PMML from the given input", e);
        }
    }
    
    /**
     * This method initializes the internal map structures. The
     * order of initialization is important. The internal structures
     * for many models will require that the data dictionary be available
     */
    private void initFromPMML() {
    	if (this.rawPmml != null) {
    		initDataDictionaryMap();
    		initModelsMap();
    	}
    }
    
    /**
     * Initializes the internal structure that holds data dictionary information.
     * This initializer should be called prior to any other initializers, since
     * many other structures may have a dependency on the data dictionary.
     */
    private void initDataDictionaryMap() {
    	DataDictionary dd = rawPmml.getDataDictionary();
    	if (dd != null) {
    		dataDictionaryMap = new HashMap<>();
	    	for (DataField dataField: dd.getDataFields()) {
	    		PMMLDataField df = new PMMLDataField(dataField);
	    		dataDictionaryMap.put(df.getName(), df);
	    	}
    	} else {
    		throw new IllegalStateException("BRMS-PMML requires a data dictionary section in the definition file");
    	}
    }
    
    /**
     * Initializes the internal map of models. This map includes all models that are
     * defined within the PMML, including child models (i.e. models contained within models)
     */
    private void initModelsMap() {
    	modelsMap = new HashMap<>();
    	List<PMML4Model> rootModels = modelFactory.getModels(this);
    	if (rootModels != null && !rootModels.isEmpty()) {
    		for (PMML4Model model : rootModels) {
    			modelsMap.put(model.getModelId(), model);
    			addChildModelsToMap(model);
    		}
    	} else {
    		throw new IllegalStateException("BRMS-PMML requires at least one model of the model types that are recognized");
    	}
    }

    /**
     * Recursive method that adds children models (i.e. models contained within models)
     * to the internal map of models
     *  
     * @param parentModel The model which may contain child models
     */
    private void addChildModelsToMap(PMML4Model parentModel) {
    	Map<String,PMML4Model> childModels = parentModel.getChildModels();
    	if (childModels != null && !childModels.isEmpty()) {
    		for (PMML4Model model: childModels.values()) {
    			modelsMap.put(model.getModelId(), model);
    			addChildModelsToMap(model);
    		}
    	}
    }
    
    @Override
    public PMML getRawPMML() {
        return this.rawPmml;
    }
    
    /**
     * Retrieves a List of the raw MiningField objects for a
     * given model
     * @param modelId The identifier of the model for which the list of MiningField objects is retrieved 
     * @return The list of MiningField objects belonging to the identified model
     */
    public List<MiningField> getMiningFieldsForModel(String modelId) {
    	PMML4Model model = modelsMap.get(modelId);
    	if (model != null) {
    		return model.getRawMiningFields();
    	}
    	return null;
    }
    
    /**
     * Retrieves a Map with entries that consist of 
     * key -> a model identifier
     * value -> the List of raw MiningField objects belonging to the model referenced by the key  
     * @return The Map of model identifiers and their corresponding list of raw MiningField objects
     */
    public Map<String, List<MiningField>> getMiningFields() {
    	Map<String, List<MiningField>> miningFieldsMap = new HashMap<>();
    	for (PMML4Model model : getModels()) {
    		List<MiningField> miningFields = model.getRawMiningFields();
    		miningFieldsMap.put(model.getModelId(), miningFields);
    		model.getChildModels();
    	}
    	return miningFieldsMap;
    }
    
    public Map<String, PMML4Model> getModelsMap() {
    	return this.modelsMap;
    }

    /**
     * Retrieves a Map with entries that consist of
     *    key -> a model identifier
     *    value -> the PMML4Model object that the key refers to
     * where the PMML4Model does not indicate a parent model (i.e. the
     * model is not a child model)
     * @return The Map of model identifiers and their corresponding PMML4Model objects
     */
    @Override
    public Map<String,PMML4Model> getRootModels() {
    	Map<String,PMML4Model> rootModels = new HashMap<>();
    	for (PMML4Model model: this.modelsMap.values()) {
    		if (model.getParentModel() == null) {
    			rootModels.put(model.getModelId(), model);
    		}
    	}
    	return rootModels;
    }
    
    @Override
    public Miningmodel getRootMiningModel() {
    	for (PMML4Model model: this.modelsMap.values()) {
    		if (model.getParentModel() == null && model instanceof Miningmodel) {
    			return (Miningmodel)model;
    		}
    	}
    	return null;
    }
    
    /**
     * Retrieves a Map whose entries consist of
     *    key -> a model identifier
     *    value -> the PMML4Model object that the key refers to
     * where the PMML4Model indicates that it
     * @param parentModelId
     * @return
     */
    public Map<String,PMML4Model> getChildModels(String parentModelId) {
    	PMML4Model parent = modelsMap.get(parentModelId);
    	Map<String,PMML4Model> childMap = parent.getChildModels();
    	return (childMap != null && !childMap.isEmpty()) ? new HashMap<>(childMap) : new HashMap<>();
    }
    
    @Override
    public Map<String,PMML4Model> getModels(PMML4ModelType type, PMML4Model parent) {
    	Map<String,PMML4Model> filteredModels = new HashMap<>();
    	Map<String,PMML4Model> unfilteredModels = parent == null ? 
    			getRootModels() : getChildModels(parent.getModelId());
    	if (unfilteredModels != null && !unfilteredModels.isEmpty()) {
    		for (PMML4Model model : unfilteredModels.values()) {
    			if (model.getModelType() == type) {
    				filteredModels.put(model.getModelId(), model);
    			}
    		}
    	}
    	return filteredModels;
    }
    
    @Override
    public boolean containsMiningModel() {
    	for (PMML4Model model : this.modelsMap.values()) {
    		if (model.getModelType() == PMML4ModelType.MINING) {
    			return true;
    		}
    	}
    	return false;
    }

    @Override
    public List<PMML4Model> getModels() {
    	Collection<PMML4Model> models = getModelsMap().values(); 
        return (models != null && !models.isEmpty()) ? new ArrayList<>(models) : new ArrayList<>();
    }

    @Override
    public List<PMMLDataField> getDataDictionaryFields() {
        return (dataDictionaryMap != null && !dataDictionaryMap.isEmpty()) ?
                new ArrayList<>(dataDictionaryMap.values()) : new ArrayList<>();
    }
    
    public Map<String, PMMLDataField> getDataDictionaryMap() {
    	return new HashMap<>(dataDictionaryMap);
    }
    
}
