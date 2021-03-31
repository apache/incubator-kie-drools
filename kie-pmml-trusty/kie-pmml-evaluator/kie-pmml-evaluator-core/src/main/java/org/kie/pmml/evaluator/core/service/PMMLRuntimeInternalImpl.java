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
package org.kie.pmml.evaluator.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinderImpl;
import org.kie.pmml.evaluator.core.utils.KnowledgeBaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.enums.ResultCode.OK;

public class PMMLRuntimeInternalImpl implements PMMLRuntimeInternal {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeInternalImpl.class);

    private final KieBase knowledgeBase;
    private final PMMLModelEvaluatorFinderImpl pmmlModelExecutorFinder;

    public PMMLRuntimeInternalImpl(final KieBase knowledgeBase,
                                   final PMMLModelEvaluatorFinderImpl pmmlModelExecutorFinder) {
        this.knowledgeBase = knowledgeBase;
        this.pmmlModelExecutorFinder = pmmlModelExecutorFinder;
    }

    @Override
    public KieBase getKnowledgeBase() {
        return knowledgeBase;
    }

    @Override
    public List<KiePMMLModel> getKiePMMLModels() {
        return KnowledgeBaseUtils.getModels(knowledgeBase);
    }

    @Override
    public List<PMMLModel> getPMMLModels() {
        List<KiePMMLModel> kiePMMLModels = getKiePMMLModels();
        return new ArrayList<>(kiePMMLModels);
    }

    @Override
    public Optional<KiePMMLModel> getKiePMMLModel(final String modelName) {
        return KnowledgeBaseUtils.getModel(knowledgeBase, modelName);
    }

    @Override
    public Optional<PMMLModel> getPMMLModel(String modelName) {
        return getKiePMMLModel(modelName).map(KiePMMLModel.class::cast);
    }

    @Override
    public PMML4Result evaluate(final String modelName, final PMMLContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("evaluate {} {}", modelName, context);
        }
        KiePMMLModel toEvaluate = getKiePMMLModel(modelName).orElseThrow(() -> new KiePMMLException("Failed to retrieve model with name " + modelName));
        return evaluate(toEvaluate, context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected PMML4Result evaluate(final KiePMMLModel model, final PMMLContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("evaluate {} {}", model, context);
        }
        addMissingValuesReplacements(model, context);
        final PMMLRequestData requestData = context.getRequestData();
        final Map<String, ParameterInfo> mappedRequestParams = requestData.getMappedRequestParams();
        final List<KiePMMLNameValue> kiePMMLNameValues =
                getKiePMMLNameValuesFromParameterInfos(mappedRequestParams.values());
        executeTransformations(model, context, requestData, mappedRequestParams, kiePMMLNameValues);
        PMMLModelEvaluator executor = getFromPMMLModelType(model.getPmmlMODEL())
                .orElseThrow(() -> new KiePMMLException(String.format("PMMLModelEvaluator not found for model %s",
                                                                      model.getPmmlMODEL())));
        PMML4Result toReturn = executor.evaluate(knowledgeBase, model, context);
        executeTargets(toReturn, model);
        updateTargetValueType(model, toReturn);
        populateOutputFields(model, toReturn, kiePMMLNameValues);
        return toReturn;
    }

    /**
     * Add missing input values if defined in original PMML as <b>missingValueReplacement</b>.
     * <p>
     * "missingValueReplacement: If this attribute is specified then a missing input value is automatically replaced
     * by the given value.
     * That is, the model itself works as if the given value was found in the original input. "
     * @param model
     * @param context
     * @see
     * <a href="http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_MISSING-VALUE-TREATMENT-METHOD">MISSING-VALUE-TREATMENT-METHOD</a>
     */
    protected void addMissingValuesReplacements(final KiePMMLModel model, final PMMLContext context) {
        logger.debug("addMissingValuesReplacements {} {}", model, context);
        final PMMLRequestData requestData = context.getRequestData();
        final Map<String, ParameterInfo> mappedRequestParams = requestData.getMappedRequestParams();
        final Map<String, Object> missingValueReplacementMap = model.getMissingValueReplacementMap();
        missingValueReplacementMap.forEach((fieldName, missingValueReplacement) -> {
            if (!mappedRequestParams.containsKey(fieldName)) {
                logger.debug("missingValueReplacement {} {}", fieldName, missingValueReplacement);
                requestData.addRequestParam(fieldName, missingValueReplacement);
                context.addMissingValueReplaced(fieldName, missingValueReplacement);
            }
        });
    }

    /**
     * Execute <b>Transformations</b> on input data.
     * @param model
     * @param context
     * @param requestData
     * @param mappedRequestParams
     * @param kiePMMLNameValues
     * @see <a href="http://dmg.org/pmml/v4-4/Transformations.html">Transformations</a>
     * @see
     * <a href="http://dmg.org/pmml/v4-4/Transformations.html#xsdElement_LocalTransformations">LocalTransformations</a>
     */
    protected void executeTransformations(final KiePMMLModel model,
                                          final PMMLContext context,
                                          final PMMLRequestData requestData,
                                          final Map<String, ParameterInfo> mappedRequestParams,
                                          final List<KiePMMLNameValue> kiePMMLNameValues) {
        logger.debug("executeTransformations {} {}", model, requestData);
        final Map<String, Function<List<KiePMMLNameValue>, Object>> commonTransformationsMap =
                model.getCommonTransformationsMap();
        commonTransformationsMap.forEach((fieldName, transformationFunction) -> {
            // Common Transformations need to be done only once
            if (!mappedRequestParams.containsKey(fieldName)) {
                logger.debug("commonTransformation {} {}", fieldName, transformationFunction);
                Object commonTranformation = transformationFunction.apply(kiePMMLNameValues);
                requestData.addRequestParam(fieldName, commonTranformation);
                context.addCommonTranformation(fieldName, commonTranformation);
                kiePMMLNameValues.add(new KiePMMLNameValue(fieldName, commonTranformation));
            }
        });
        final Map<String, Function<List<KiePMMLNameValue>, Object>> localTransformationsMap =
                model.getLocalTransformationsMap();
        localTransformationsMap.forEach((fieldName, transformationFunction) -> {
            logger.debug("localTransformation {} {}", fieldName, transformationFunction);
            Object localTransformation = transformationFunction.apply(kiePMMLNameValues);
            // Local Transformations need to be done for every model, eventually replacing previous ones
            if (mappedRequestParams.containsKey(fieldName)) {
                final ParameterInfo toRemove = mappedRequestParams.get(fieldName);
                requestData.removeRequestParam(toRemove);
            }
            requestData.addRequestParam(fieldName, localTransformation);
            context.addLocalTranformation(fieldName, localTransformation);
        });
    }

    /**
     * Execute <b>modifications</b> on target result.
     * @param toModify
     * @param model
     * @see <a href="http://dmg.org/pmml/v4-4-1/Targets.html>Targets</a>
     */
    protected void executeTargets(final PMML4Result toModify, final KiePMMLModel model) {
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
    void updateTargetValueType(final KiePMMLModel model, final PMML4Result toUpdate) {
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
    void populateOutputFields(final KiePMMLModel model, final PMML4Result toUpdate,
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
    }

    void populatePredictedOutputField(final KiePMMLOutputField outputField,
                                      final PMML4Result toUpdate,
                                      final KiePMMLModel model,
                                      final List<KiePMMLNameValue> kiePMMLNameValues) {
        logger.debug("populatePredictedOutputField {} {} {} {}", outputField, toUpdate, model, kiePMMLNameValues);
        String targetFieldName = outputField.getTargetField().orElse(toUpdate.getResultObjectName());
        Optional<Object> variableValue = Optional.empty();
        if (targetFieldName != null) {
            variableValue = Stream.of(getValueFromPMMLResultByVariableName(targetFieldName, toUpdate),
                                      getValueFromKiePMMLNameValueByVariableName(targetFieldName, kiePMMLNameValues))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        }
        variableValue.ifPresent(objValue -> toUpdate.addResultVariable(outputField.getName(), objValue));
    }

    void populateTransformedOutputField(final KiePMMLOutputField outputField,
                                        final PMML4Result toUpdate,
                                        final KiePMMLModel model,
                                        final List<KiePMMLNameValue> kiePMMLNameValues) {
        logger.debug("populateTransformedOutputField {} {} {} {}", outputField, toUpdate, model, kiePMMLNameValues);
        String variableName = outputField.getName();
        Optional<Object> variableValue = Optional.empty();
        if (outputField.getKiePMMLExpression() != null) {
            final KiePMMLExpression kiePMMLExpression = outputField.getKiePMMLExpression();
            variableValue = getValueFromKiePMMLExpression(kiePMMLExpression, model, kiePMMLNameValues);
        }
        variableValue.ifPresent(objValue -> toUpdate.addResultVariable(variableName, objValue));
    }

    Optional<Object> getValueFromKiePMMLExpression(final KiePMMLExpression kiePMMLExpression,
                                                   final KiePMMLModel model,
                                                   final List<KiePMMLNameValue> kiePMMLNameValues) {
        String expressionType = kiePMMLExpression.getClass().getSimpleName();
        Optional<Object> toReturn = Optional.empty();
        switch (expressionType) {
            case "KiePMMLApply":
                toReturn = Stream.of(getValueFromKiePMMLApplyFunction((KiePMMLApply) kiePMMLExpression,
                                                                      model,
                                                                      kiePMMLNameValues),
                                     getValueFromKiePMMLApplyMapMissingTo((KiePMMLApply) kiePMMLExpression),
                                     getValueFromKiePMMLApplyMapDefaultValue((KiePMMLApply) kiePMMLExpression))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst();
                break;
            case "KiePMMLConstant":
                toReturn = getValueFromKiePMMLConstant((KiePMMLConstant) kiePMMLExpression);
                break;
            case "KiePMMLFieldRef":
                toReturn =
                        Stream.of(getValueFromKiePMMLNameValueByVariableName(((KiePMMLFieldRef) kiePMMLExpression).getName(), kiePMMLNameValues),
                                  getMissingValueFromKiePMMLFieldRefMapMissingTo((KiePMMLFieldRef) kiePMMLExpression))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .findFirst();
                break;
            default:
                // Not implemented, yet
                break;
        }
        return toReturn;
    }

    private Optional<Object> getValueFromKiePMMLApplyFunction(final KiePMMLApply kiePMMLExpression,
                                                              final KiePMMLModel model,
                                                              final List<KiePMMLNameValue> kiePMMLNameValues) {
        String functionName = kiePMMLExpression.getFunction();
        Optional<Object> optionalObjectParameter = Optional.empty();
        final Map<String, BiFunction<List<KiePMMLNameValue>, Object, Object>> functionsMap = model.getFunctionsMap();
        if (kiePMMLExpression.getKiePMMLExpressions() != null && !kiePMMLExpression.getKiePMMLExpressions().isEmpty()) {
            optionalObjectParameter = getValueFromKiePMMLExpression(kiePMMLExpression.getKiePMMLExpressions().get(0),
                                                                    model,
                                                                    kiePMMLNameValues);
        }
        final Object objectParameter = optionalObjectParameter.orElse(null);
        return functionsMap.keySet()
                .stream()
                .filter(funName -> funName.equals(functionName))
                .findFirst()
                .map(functionsMap::get)
                .map(function -> function.apply(kiePMMLNameValues, objectParameter));
    }

    private Optional<Object> getValueFromKiePMMLConstant(final KiePMMLConstant kiePMMLConstant) {
        return Optional.ofNullable(kiePMMLConstant.getValue());
    }

    private Optional<Object> getValueFromKiePMMLApplyMapMissingTo(final KiePMMLApply kiePMMLApply) {
        return Optional.ofNullable(kiePMMLApply.getMapMissingTo());
    }

    private Optional<Object> getValueFromKiePMMLApplyMapDefaultValue(final KiePMMLApply kiePMMLApply) {
        return Optional.ofNullable(kiePMMLApply.getDefaultValue());
    }

    private Optional<Object> getValueFromKiePMMLNameValueByVariableName(final String variableName,
                                                                        final List<KiePMMLNameValue> kiePMMLNameValues) {
        return kiePMMLNameValues.stream()
                .filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(variableName))
                .findFirst()
                .map(KiePMMLNameValue::getValue);
    }

    private Optional<Object> getValueFromPMMLResultByVariableName(final String variableName,
                                                                  final PMML4Result pmml4Result) {
        return Optional.ofNullable(pmml4Result.getResultVariables().get(variableName));
    }

    private Optional<Object> getMissingValueFromKiePMMLFieldRefMapMissingTo(final KiePMMLFieldRef kiePMMLFieldRef) {
        return Optional.ofNullable(kiePMMLFieldRef.getMapMissingTo());
    }

    /**
     * Returns an <code>Optional&lt;PMMLModelExecutor&gt;</code> to allow
     * incremental development of different model-specific executors
     * @param pmmlMODEL
     * @return
     */
    private Optional<PMMLModelEvaluator> getFromPMMLModelType(final PMML_MODEL pmmlMODEL) {
        logger.trace("getFromPMMLModelType {}", pmmlMODEL);
        return pmmlModelExecutorFinder.getImplementations(false)
                .stream()
                .filter(implementation -> pmmlMODEL.equals(implementation.getPMMLModelType()))
                .findFirst();
    }

    private List<KiePMMLNameValue> getKiePMMLNameValuesFromParameterInfos(final Collection<ParameterInfo> parameterInfos) {
        return parameterInfos.stream()
                .map(parameterInfo -> new KiePMMLNameValue(parameterInfo.getName(), parameterInfo.getValue()))
                .collect(Collectors.toList());
    }
}
