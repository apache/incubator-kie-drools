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

import java.io.Serializable;
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

public abstract class AbstractModel implements PMML4Model {
    private String modelId;
    private PMML4ModelType modelType;
    private PMML4Unit owner;
    protected Map<String, MiningField> miningFieldMap;
    protected Map<String, OutputField> outputFieldMap;
    protected static PMML4Helper helper = new PMML4Helper();
    public static String PMML_JAVA_PACKAGE_NAME = "org.drools.pmml.pmml_4_2.model";


    public AbstractModel(String modelId, PMML4ModelType modelType, PMML4Unit owner) {
        this.modelId = modelId;
        this.modelType = modelType;
        this.owner = owner;
    }

    protected void initMiningFieldMap() {
        MiningSchema schema = getMiningSchema();
        if (schema != null) {
            miningFieldMap = schema.getMiningFields().stream().filter(serializable -> serializable instanceof MiningField)
                    .map(serializable -> (MiningField) serializable)
                    .collect(Collectors.toMap(MiningField::getName,
                                              miningField -> miningField));
        }
    }

    protected void initOutputFieldMap() {
        Output output = getOutput();
        if (output != null) {
            outputFieldMap = output.getOutputFields().stream().filter(serializable -> serializable instanceof OutputField)
                    .map(serializable -> (OutputField) serializable)
                    .collect(Collectors.toMap(OutputField::getName,
                                              outputField -> outputField));
        }
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
