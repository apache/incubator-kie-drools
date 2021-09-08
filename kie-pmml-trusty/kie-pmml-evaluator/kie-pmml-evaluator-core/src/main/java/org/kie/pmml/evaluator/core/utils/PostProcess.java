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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
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

    public static void postProcess(final PMML4Result toReturn, final KiePMMLModel model, final ProcessingDTO processingDTO) {
        executeTargets(toReturn, processingDTO);
        updateTargetValueType(model, toReturn);
        populateProcessingDTO(toReturn, model,  processingDTO);
        populateOutputFields(toReturn, processingDTO,  model);
    }

    static void populateProcessingDTO(final PMML4Result pmml4Result, final KiePMMLModel model, final ProcessingDTO toPopulate) {
        pmml4Result.getResultVariables().forEach((key, value) -> toPopulate.addKiePMMLNameValue(new KiePMMLNameValue(key, value)));
        final Map<String, Double> sortedByValue
                = model.getOutputFieldsMap().entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof Double && (Double) entry.getValue() > 0)
                .map((Function<Map.Entry<String, Object>, Map.Entry<String, Double>>) entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), (Double) entry.getValue()))
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                          LinkedHashMap::new));
        final List<String> orderedReasonCodes = new ArrayList<>(sortedByValue.keySet());
        toPopulate.addOrderedReasonCodes(orderedReasonCodes);
    }

    /**
     * Execute <b>modifications</b> on target result.
     * @param toModify
     * @param processingDTO
     * @see <a href="http://dmg.org/pmml/v4-4-1/Targets.html>Targets</a>
     */
    static void executeTargets(final PMML4Result toModify, final ProcessingDTO processingDTO) {
        logger.debug("executeTargets {} {}", toModify, processingDTO);
        if (!toModify.getResultCode().equals(OK.getName())) {
            return;
        }
        final String targetFieldName = toModify.getResultObjectName();
        final Map<String, Object> resultVariables = toModify.getResultVariables();
        processingDTO.getKiePMMLTargets()
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
     * @param toUpdate
     * @param processingDTO
     */
    static void populateOutputFields(final PMML4Result toUpdate,
                                     final ProcessingDTO processingDTO,
                                     final KiePMMLModel model) {
        logger.debug("populateOutputFields {} {}", toUpdate, processingDTO);
        for (KiePMMLOutputField outputField : processingDTO.getOutputFields()) {
            Object variableValue = outputFieldToValue(outputField, processingDTO, model);
            if (variableValue != null) {
                String variableName = outputField.getName();
                toUpdate.addResultVariable(variableName, variableValue);
                processingDTO.addKiePMMLNameValue(new KiePMMLNameValue(variableName, variableValue));
            }
        }
    }

    /**
     * This method extract the value of the specified output field either from
     * the processing DTO or the model, depending from the field type.
     *
     * @param outputField
     * @param processingDTO
     * @param model
     *
     * @return the correct value as Object if the field is implemented or null if
     * the field is not implemented. It is up to the caller to handle the null case.
     */
    private static Object outputFieldToValue(KiePMMLOutputField outputField, ProcessingDTO processingDTO, KiePMMLModel model) {
        RESULT_FEATURE resultFeature = RESULT_FEATURE.getOrDefault(outputField.getResultFeature());
        switch (resultFeature) {
            case PREDICTED_VALUE:
                return outputField.evaluatePredictedValue(processingDTO);

            case TRANSFORMED_VALUE:
                return outputField.evaluateTransformedValue(processingDTO);

            case REASON_CODE:
                return outputField.evaluateReasonCodeValue(processingDTO);

            case PREDICTED_DISPLAY_VALUE:
                return model.getPredictedDisplayValue();

            case ENTITY_ID:
            case CLUSTER_ID:
                return model.getEntityId();

            case AFFINITY:
            case ENTITY_AFFINITY:
            case CLUSTER_AFFINITY:
                return model.getAffinity();

            default:
                logger.warn("OutputField with feature \"{}\" is currently not implemented and will be ignored.", resultFeature.getName());
                return null;
        }
    }
}
