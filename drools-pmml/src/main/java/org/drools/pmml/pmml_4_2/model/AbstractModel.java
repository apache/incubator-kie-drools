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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.drools.pmml.pmml_4_2.PMML4Helper;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public abstract class AbstractModel implements PMML4Model {
    private String modelId;
    private PMML4ModelType modelType;
    private PMML4Unit owner;
    protected Map<String, MiningField> miningFieldMap;
    protected Map<String, OutputField> outputFieldMap;
    protected static PMML4Helper helper = new PMML4Helper();
    public static String PMML_JAVA_PACKAGE_NAME = "org.drools.pmml.pmml_4_2.model";

    protected abstract TemplateRegistry getTemplateRegistry();
    protected abstract String getMiningPojoTemplateName();


    public AbstractModel(String modelId, PMML4ModelType modelType, PMML4Unit owner) {
        this.modelId = modelId;
        this.modelType = modelType;
        this.owner = owner;
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
        for (OutputField field: output.getOutputFields()) {
            outputFieldMap.put(field.getName(), field);
        }
    }

    protected boolean isValidMiningField(PMMLDataField dataField) {
        if (miningFieldMap == null || miningFieldMap.isEmpty()) {
            initMiningFieldMap();
        }
        if (miningFieldMap.containsKey(dataField.getName())) {
            return true;
        }
        return false;
    }


    @Override
    public String getMiningPojo() {
        TemplateRegistry registry = this.getTemplateRegistry();
        List<PMMLDataField> dataFields = this.getMiningFields();
        Map<String, Object> vars = new HashMap<>();
        String className = this.getMiningPojoClassName();
        vars.put("pmmlPackageName",PMML_JAVA_PACKAGE_NAME);
        vars.put("className",className);
        vars.put("imports",new ArrayList<>());
        vars.put("dataFields",dataFields);
        vars.put("modelName", this.getModelId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TemplateRuntime.execute( registry.getNamedTemplate(this.getMiningPojoTemplateName()),
                                 null,
                                 new MapVariableResolverFactory(vars),
                                 baos );

        return new String(baos.toByteArray());
    }

    @Override
    public PMML4ModelType getModelType() {
        return this.modelType;
    }

    @Override
    public String getModelId() {
        return this.modelId;
    }

    @Override
    public PMML4Unit getOwner() {
        return this.owner;
    }

}
