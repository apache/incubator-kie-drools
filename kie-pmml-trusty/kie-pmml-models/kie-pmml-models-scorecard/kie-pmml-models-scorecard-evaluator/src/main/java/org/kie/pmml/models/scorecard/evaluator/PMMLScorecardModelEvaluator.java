/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package  org.kie.pmml.models.scorecard.evaluator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModel;

import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Scorecard</b>
 */
public class PMMLScorecardModelEvaluator implements PMMLModelEvaluator<KiePMMLScorecardModel> {

    @Override
    public PMML_MODEL getPMMLModelType(){
        return PMML_MODEL.SCORECARD_MODEL;
    }

    @Override
    public PMML4Result evaluate(final KieBase knowledgeBase,
                                final KiePMMLScorecardModel model,
                                final PMMLContext pmmlContext) {
        PMML4Result toReturn = new PMML4Result();
        String targetField = model.getTargetField();
        final Map<String, Object> requestData =
                getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        Object result = model.evaluate(knowledgeBase, requestData);
        toReturn.addResultVariable(targetField, result);
        toReturn.setResultObjectName(targetField);
        toReturn.setResultCode(OK.getName());
        populateWithOutputFields(toReturn, model.getKiePMMLOutputFields(),  model.getOutputFieldsMap());
        return toReturn;
    }

    protected void populateWithOutputFields(final PMML4Result toPopulate, final List<KiePMMLOutputField> kiePMMLOutputFields, final Map<String, Object> outputFieldsMap) {
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
                    case PREDICTED_VALUE:
                        toPopulate.updateResultVariable(outputField.getName(), toPopulate.getResultVariables().get(toPopulate.getResultObjectName()));
                        break;
                    default:
                        // not managed, yet
                }
            }
        }
    }
}
