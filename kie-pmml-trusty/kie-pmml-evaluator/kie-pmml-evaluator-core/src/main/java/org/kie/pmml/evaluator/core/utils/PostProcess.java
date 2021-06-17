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
package org.kie.pmml.evaluator.core.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.enums.ResultCode.OK;

/**
 * Class meant to provide static methods related to <b>post-process</b> manipulation
 */
public class PostProcess {

    private static final Logger logger = LoggerFactory.getLogger(PostProcess.class);

    private PostProcess() {
        // Avoid instantiation
    }

    public static void postProcess(final PMML4Result toReturn, final KiePMMLModel model,
                                   final List<KiePMMLNameValue> kiePMMLNameValues) {
        executeTargets(toReturn, model);
        updateTargetValueType(model, toReturn);
        populateOutputFields(model, toReturn, kiePMMLNameValues);
    }

    /**
     * Execute <b>modifications</b> on target result.
     * @param toModify
     * @param model
     * @see <a href="http://dmg.org/pmml/v4-4-1/Targets.html>Targets</a>
     */
    static void executeTargets(final PMML4Result toModify, final KiePMMLModel model) {
        logger.debug("executeTargets {} {}", toModify, model);
        if (!toModify.getResultCode().equals(OK.getName())) {
            return;
        }
        final String targetFieldName = toModify.getResultObjectName();
        final Map<String, Object> resultVariables = toModify.getResultVariables();
        model.getKiePMMLTargets()
                .stream()
                .filter(kiePMMLTarget -> kiePMMLTarget.getField() != null && kiePMMLTarget.getField().equals(targetFieldName))
                .findFirst()
                .ifPresent(kiePMMLTarget -> {
                    Object prediction = resultVariables.get(targetFieldName);
                    logger.debug("Original prediction {}", prediction);
                    Object modifiedPrediction = kiePMMLTarget.modifyPrediction(resultVariables.get(targetFieldName));
                    logger.debug("Modified prediction {}", modifiedPrediction);
                    resultVariables.put(targetFieldName, modifiedPrediction);
                });
    }

    /**
     * Verify that the returned value has the required type as defined inside <code>DataDictionary/MiningSchema</code>
     * @param model
     * @param toUpdate
     */
    static void updateTargetValueType(final KiePMMLModel model, final PMML4Result toUpdate) {
        DATA_TYPE dataType = model.getMiningFields().stream()
                .filter(miningField -> model.getTargetField().equals(miningField.getName()))
                .map(MiningField::getDataType)
                .findFirst()
                .orElseThrow(() -> new KiePMMLException("Failed to find DATA_TYPE for " + model.getTargetField()));
        Object prediction = toUpdate.getResultVariables().get(model.getTargetField());
        if (prediction != null) {
            Object convertedPrediction = dataType.getActualValue(prediction);
            toUpdate.getResultVariables().put(model.getTargetField(), convertedPrediction);
        }
    }

    /**
     * Populated the <code>PMML4Result</code> with <code>OutputField</code> results
     * @param model
     * @param toUpdate
     * @param kiePMMLNameValues
     */
    static void populateOutputFields(final KiePMMLModel model, final PMML4Result toUpdate,
                                     final List<KiePMMLNameValue> kiePMMLNameValues) {
        logger.debug("populateOutputFields {} {} {}", model, toUpdate, kiePMMLNameValues);
        final Map<RESULT_FEATURE, List<KiePMMLOutputField>> outputFieldsByFeature = model.getKiePMMLOutputFields()
                .stream()
                .collect(Collectors.groupingBy(KiePMMLOutputField::getResultFeature));
        List<KiePMMLOutputField> predictedOutputFields = outputFieldsByFeature.get(RESULT_FEATURE.PREDICTED_VALUE);
        if (predictedOutputFields != null) {
            predictedOutputFields
                    .forEach(outputField -> populatePredictedOutputField(outputField, toUpdate,
                                                                         model,
                                                                         kiePMMLNameValues));
        }
        List<KiePMMLOutputField> transformedOutputFields = outputFieldsByFeature.get(RESULT_FEATURE.TRANSFORMED_VALUE);
        if (transformedOutputFields != null) {
            transformedOutputFields
                    .forEach(outputField -> populateTransformedOutputField(outputField, toUpdate,
                                                                           model,
                                                                           kiePMMLNameValues));
        }
        List<KiePMMLOutputField> reasonCodeOutputFields = outputFieldsByFeature.get(RESULT_FEATURE.REASON_CODE);
        if (reasonCodeOutputFields != null) {
            final Map<String, Double> sortedByValue
                    = model.getOutputFieldsMap().entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() instanceof Double && (Double) entry.getValue() > 0)
                    .map((Function<Map.Entry<String, Object>, Map.Entry<String, Double>>) entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), (Double) entry.getValue()))
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                              LinkedHashMap::new));
            final List<String> orderedReasonCodes = new ArrayList<>(sortedByValue.keySet());
            reasonCodeOutputFields
                    .forEach(outputField -> populateReasonCodeOutputField(outputField, toUpdate, orderedReasonCodes));
        }
    }

    static void populatePredictedOutputField(final KiePMMLOutputField outputField,
                                             final PMML4Result toUpdate,
                                             final KiePMMLModel model,
                                             final List<KiePMMLNameValue> kiePMMLNameValues) {
        logger.debug("populatePredictedOutputField {} {} {} {}", outputField, toUpdate, model, kiePMMLNameValues);
        if (!RESULT_FEATURE.PREDICTED_VALUE.equals(outputField.getResultFeature())) {
            throw new KiePMMLException("Unexpected " + outputField.getResultFeature());
        }
        String targetFieldName = outputField.getTargetField().orElse(toUpdate.getResultObjectName());
        Optional<Object> variableValue = Optional.empty();
        if (targetFieldName != null) {
            variableValue = Stream.of(getValueFromPMMLResultByVariableName(targetFieldName, toUpdate),
                                      getValueFromKiePMMLNameValuesByVariableName(targetFieldName, kiePMMLNameValues))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        }
        variableValue.ifPresent(objValue -> toUpdate.addResultVariable(outputField.getName(), objValue));
    }

    static void populateTransformedOutputField(final KiePMMLOutputField outputField,
                                               final PMML4Result toUpdate,
                                               final KiePMMLModel model,
                                               final List<KiePMMLNameValue> kiePMMLNameValues) {
        logger.debug("populateTransformedOutputField {} {} {} {}", outputField, toUpdate, model, kiePMMLNameValues);
        if (!RESULT_FEATURE.TRANSFORMED_VALUE.equals(outputField.getResultFeature())) {
            throw new KiePMMLException("Unexpected " + outputField.getResultFeature());
        }
        String variableName = outputField.getName();
        List<KiePMMLDerivedField> derivedFields = new ArrayList<>();
        List<KiePMMLDefineFunction> defineFunctions = new ArrayList<>();
        if (model.getTransformationDictionary() != null) {
            if (model.getTransformationDictionary().getDerivedFields() != null) {
                derivedFields.addAll(model.getTransformationDictionary().getDerivedFields());
            }
            if (model.getTransformationDictionary().getDefineFunctions() != null) {
                defineFunctions.addAll(model.getTransformationDictionary().getDefineFunctions());
            }
        }
        if (model.getLocalTransformations() != null && model.getLocalTransformations().getDerivedFields() != null) {
            derivedFields.addAll(model.getLocalTransformations().getDerivedFields());
        }
        Optional<Object> variableValue = Optional.ofNullable(outputField.evaluate(defineFunctions, derivedFields, model.getKiePMMLOutputFields(), kiePMMLNameValues));
        variableValue.ifPresent(objValue -> toUpdate.addResultVariable(variableName, objValue));
    }

    static void populateReasonCodeOutputField(final KiePMMLOutputField outputField,
                                              final PMML4Result toUpdate,
                                              final List<String> orderedReasonCodes) {
        logger.debug("populateReasonCodeOutputField {} {} {}", outputField, toUpdate, orderedReasonCodes);
        if (!RESULT_FEATURE.REASON_CODE.equals(outputField.getResultFeature())) {
            throw new KiePMMLException("Unexpected " + outputField.getResultFeature());
        }
        if (outputField.getRank() != null) {
            int index = outputField.getRank() - 1;
            String resultCode = null;
            String resultVariableName = outputField.getName();
            if (index < orderedReasonCodes.size()) {
                resultCode = orderedReasonCodes.get(index);
            }
            toUpdate.updateResultVariable(resultVariableName, resultCode);
        }
    }



    static Optional<Object> getValueFromKiePMMLNameValuesByVariableName(final String variableName,
                                                                        final List<KiePMMLNameValue> kiePMMLNameValues) {
        return kiePMMLNameValues.stream()
                .filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(variableName))
                .map(KiePMMLNameValue::getValue)
                .findFirst();
    }

    static Optional<Object> getValueFromPMMLResultByVariableName(final String variableName,
                                                                 final PMML4Result pmml4Result) {
        return Optional.ofNullable(pmml4Result.getResultVariables().get(variableName));
    }

}
