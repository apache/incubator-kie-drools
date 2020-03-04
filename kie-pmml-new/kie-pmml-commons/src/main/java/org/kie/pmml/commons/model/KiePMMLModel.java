/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;

/**
 * KIE representation of PMML model
 */
public abstract class KiePMMLModel {

    protected String name;
    protected PMML_MODEL pmmlMODEL;
    protected MINING_FUNCTION miningFunction;
    protected String targetField;
    protected Map<String, Object> outputFieldsMap = new HashMap<>();

    protected KiePMMLModel(String name) {
        this.name = name;
    }

    public PMML_MODEL getPmmlMODEL() {
        return pmmlMODEL;
    }

    public MINING_FUNCTION getMiningFunction() {
        return miningFunction;
    }

    public String getTargetField() {
        return targetField;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getOutputFieldsMap() {
        return Collections.unmodifiableMap(outputFieldsMap);
    }

    public abstract Object evaluate(Map<String, Object> requestData);
}
