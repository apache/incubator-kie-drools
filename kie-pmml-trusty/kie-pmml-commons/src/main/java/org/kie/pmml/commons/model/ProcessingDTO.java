/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.commons.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

/**
 * DTO class used to bring around data related to Pre/Post processing
 * phases
 */
public class ProcessingDTO {

    private final List<KiePMMLDefineFunction> defineFunctions;
    private final List<KiePMMLDerivedField> derivedFields;
    private final List<KiePMMLOutputField> outputFields;
    private final List<KiePMMLTarget> kiePMMLTargets;
    private final List<KiePMMLNameValue> kiePMMLNameValues;
    private final List<String> orderedReasonCodes;
    private final List<MiningField> miningFields;
    private Object predictedDisplayValue;
    private Object entityId;
    private Object affinity;
    private Map<String, Double> probabilityMap;

    /**
     *
     * @param model
     * @param kiePMMLNameValues a <b>mutable</b> list of <code>KiePMMLNameValue</code>
     */
    public ProcessingDTO(final KiePMMLModel model, final List<KiePMMLNameValue> kiePMMLNameValues) {
        this.derivedFields = new ArrayList<>();
        this.defineFunctions = new ArrayList<>();
        if (model.getTransformationDictionary() != null) {
            if (model.getTransformationDictionary().getDerivedFields() != null) {
                this.derivedFields.addAll(model.getTransformationDictionary().getDerivedFields());
            }
            if (model.getTransformationDictionary().getDefineFunctions() != null) {
                this.defineFunctions.addAll(model.getTransformationDictionary().getDefineFunctions());
            }
        }
        if (model.getLocalTransformations() != null && model.getLocalTransformations().getDerivedFields() != null) {
            this.derivedFields.addAll(model.getLocalTransformations().getDerivedFields());
        }
        this.outputFields = model.getKiePMMLOutputFields();
        this.kiePMMLTargets = model.getKiePMMLTargets();
        this.kiePMMLNameValues = kiePMMLNameValues;
        this.orderedReasonCodes = new ArrayList<>();
        this.miningFields = model.getMiningFields();
    }

    /**
     * @param defineFunctions
     * @param derivedFields
     * @param outputFields
     * @param kiePMMLTargets
     * @param kiePMMLNameValues a <b>mutable</b> list of <code>KiePMMLNameValue</code>
     * @param miningFields
     * @param orderedReasonCodes a <b>mutable</b> list
     */
    public ProcessingDTO(final List<KiePMMLDefineFunction> defineFunctions,
                         final List<KiePMMLDerivedField> derivedFields,
                         final List<KiePMMLOutputField> outputFields,
                         final List<KiePMMLTarget> kiePMMLTargets,
                         final List<KiePMMLNameValue> kiePMMLNameValues,
                         final List<MiningField> miningFields,
                         final List<String> orderedReasonCodes) {
        this.defineFunctions = defineFunctions;
        this.derivedFields = derivedFields;
        this.outputFields = outputFields;
        this.kiePMMLTargets = kiePMMLTargets;
        this.miningFields = miningFields;
        this.kiePMMLNameValues = kiePMMLNameValues;
        this.orderedReasonCodes = orderedReasonCodes;
        this.predictedDisplayValue = null;
        this.entityId = null;
        this.affinity = null;
        this.probabilityMap = new LinkedHashMap<>();
    }

    public List<KiePMMLDefineFunction> getDefineFunctions() {
        return Collections.unmodifiableList(defineFunctions);
    }

    public List<KiePMMLDerivedField> getDerivedFields() {
        return Collections.unmodifiableList(derivedFields);
    }

    public List<KiePMMLOutputField> getOutputFields() {
        return Collections.unmodifiableList(outputFields);
    }

    public List<KiePMMLTarget> getKiePMMLTargets() {
        return Collections.unmodifiableList(kiePMMLTargets);
    }

    public List<KiePMMLNameValue> getKiePMMLNameValues() {
        return Collections.unmodifiableList(kiePMMLNameValues);
    }

    /**
     * Add the given <code>KiePMMLNameValue</code> to <b>kiePMMLNameValues</b>
     * if there is not another with the same name; otherwise replace it.
     *
     * @param toAdd
     * @return
     */
    public boolean addKiePMMLNameValue(KiePMMLNameValue toAdd) {
        kiePMMLNameValues.removeIf(kpm -> kpm.getName().equals(toAdd.getName()));
        return kiePMMLNameValues.add(toAdd);
    }

    public List<String> getOrderedReasonCodes() {
        return Collections.unmodifiableList(orderedReasonCodes);
    }

    public boolean addOrderedReasonCodes(List<String> toAdd) {
        return orderedReasonCodes.addAll(toAdd);
    }

    public List<MiningField> getMiningFields() {
        return Collections.unmodifiableList(miningFields);
    }

    public Object getPredictedDisplayValue() {
        return predictedDisplayValue;
    }

    public void setPredictedDisplayValue(Object predictedDisplayValue) {
        this.predictedDisplayValue = predictedDisplayValue;
    }

    public Object getEntityId() {
        return entityId;
    }

    public void setEntityId(Object entityId) {
        this.entityId = entityId;
    }

    public Object getAffinity() {
        return affinity;
    }

    public void setAffinity(Object affinity) {
        this.affinity = affinity;
    }

    public Map<String, Double> getProbabilityMap() {
        return probabilityMap;
    }

    public void setProbabilityMap(Map<String, Double> probabilityMap) {
        this.probabilityMap = probabilityMap;
    }
}
