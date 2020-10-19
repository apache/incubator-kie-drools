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
package org.kie.pmml.models.drools.scorecard.model;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;

public class KiePMMLScorecardModel extends KiePMMLDroolsModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.SCORECARD_MODEL;

    protected KiePMMLScorecardModel(String modelName, List<KiePMMLExtension> extensions) {
        super(modelName, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
        return new Builder(name, extensions, miningFunction);
    }

    public static PMML_MODEL getPmmlModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public Object evaluate(final Object knowledgeBase, Map<String, Object> requestData) {
        final PMML4Result toReturn = (PMML4Result) super.evaluate(knowledgeBase, requestData);
        populateWithOutputFields(toReturn);
        return toReturn;
    }

    protected void populateWithOutputFields(PMML4Result toPopulate) {
        if (kiePMMLOutputFields != null) {
            final Map<String, Double> sortedByValue
                    = outputFieldsMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() instanceof Double && (Double) entry.getValue() > 0) // TODO: check removal of negative values
                    .map((Function<Map.Entry<String, Object>, Map.Entry<String, Double>>) entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), (Double) entry.getValue()))
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            final List<String> orderedReasonCodes = new ArrayList<>(sortedByValue.keySet());
            for (KiePMMLOutputField outputField : kiePMMLOutputFields) {
                switch (outputField.getResultFeature()) {
                    case REASON_CODE:
                        if (outputField.getRank() != null) {
                            int index = outputField.getRank() - 1;
                            String resultCode = null;
                            String resultVariableName = outputField.getName();
                            if (index < orderedReasonCodes.size()) {
                                resultCode = orderedReasonCodes.get(index);
                            }
                            toPopulate.updateResultVariable(resultVariableName, resultCode);
                        }
                        break;
                    default:
                        // not managed, yet
                }
            }
        }
    }

    public static class Builder extends KiePMMLDroolsModel.Builder<KiePMMLScorecardModel> {

        private Builder(String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
            super("Scorecard-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMMLScorecardModel(name, extensions));
        }
    }
}
