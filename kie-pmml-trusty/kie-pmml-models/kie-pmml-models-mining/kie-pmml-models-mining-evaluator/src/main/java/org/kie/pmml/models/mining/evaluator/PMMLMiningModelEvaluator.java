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
package org.kie.pmml.models.mining.evaluator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.drools.util.StringUtils;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.model.tuples.KiePMMLValueWeight;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.enums.ResultCode.FAIL;
import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;
import static org.kie.pmml.evaluator.core.utils.PMMLListenerUtils.stepExecuted;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Mining</b>
 */
public class PMMLMiningModelEvaluator implements PMMLModelEvaluator<KiePMMLMiningModel> {

    private static final Logger logger = LoggerFactory.getLogger(PMMLMiningModelEvaluator.class.getName());
    private static final String EXPECTED_A_KIE_PMMLMINING_MODEL_RECEIVED = "Expected a KiePMMLMiningModel, received %s";
    private static final String TARGET_FIELD_REQUIRED_RETRIEVED = "TargetField required, retrieved %s";

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.MINING_MODEL;
    }

    @Override
    public PMML4Result evaluate(final KiePMMLMiningModel model,
                                final PMMLRuntimeContext pmmlContext) {
        validate(model);
        return evaluateMiningModel(model, pmmlContext);
    }

    PMML4Result getPMML4Result(final KiePMMLMiningModel toEvaluate,
                               final LinkedHashMap<String, KiePMMLNameValueProbabilityMapTuple> inputData,
                               final PMMLRuntimeContext pmmlContext) {
        final MULTIPLE_MODEL_METHOD multipleModelMethod = toEvaluate.getSegmentation().getMultipleModelMethod();
        Object result = null;
        LinkedHashMap<String, Double> probabilityResultMap = null;
        ResultCode resultCode = OK;
        final LinkedHashMap<String, KiePMMLNameValue> toUseForPrediction = new LinkedHashMap<>();
        final LinkedHashMap<String, List<KiePMMLNameValue>> toUseForProbability = new LinkedHashMap<>();
        inputData.forEach((key, value) -> {
            toUseForPrediction.put(key, value.predictionValue);
            toUseForProbability.put(key, value.probabilityValues);
        });
        try {
            if (MINING_FUNCTION.CLASSIFICATION.equals(toEvaluate.getMiningFunction())) {
                result = multipleModelMethod.applyClassification(toUseForPrediction);
                probabilityResultMap = multipleModelMethod.applyProbability(toUseForProbability);
            } else {
                result = multipleModelMethod.applyPrediction(toUseForPrediction);
            }
        } catch (KieEnumException e) {
            logger.warn(e.getMessage());
            resultCode = FAIL;
        }
        pmmlContext.setProbabilityResultMap(probabilityResultMap);
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(toEvaluate.getTargetField(), result);
        toReturn.setResultObjectName(toEvaluate.getTargetField());
        toReturn.setResultCode(resultCode.getName());
        return toReturn;
    }

    /**
     * Returns a <code>KiePMMLNameValue</code> representation of the <code>PMML4Result</code>.
     * <b>It is based on the assumption there is only one result to be considered, defined as</b>
     * {@link PMML4Result#getResultObjectName() }
     *
     * @param result
     * @param multipleModelMethod
     * @param weight
     * @return
     * @throws KiePMMLException
     */
    KiePMMLNameValue getKiePMMLNameValue(PMML4Result result, MULTIPLE_MODEL_METHOD multipleModelMethod,
                                         double weight) {
        String fieldName = result.getResultObjectName();
        Object retrieved = getEventuallyWeightedResult(result.getResultVariables().get(fieldName),
                                                       multipleModelMethod, weight);
        return new KiePMMLNameValue(fieldName, retrieved);
    }

    /**
     * Returns a <code>List&lt;KiePMMLNameValue&gt;</code> representation of the given <code>Map&lt;String,
     * Double&gt;</code>.
     *
     * @param probabilityMap
     * @param multipleModelMethod
     * @param weight
     * @return
     * @throws KiePMMLException
     */
    List<KiePMMLNameValue> getKiePMMLNameValues(Map<String, Double> probabilityMap,
                                                MULTIPLE_MODEL_METHOD multipleModelMethod,
                                                double weight) {
        return probabilityMap.entrySet().stream().map(stringDoubleEntry -> {
            Object retrieved = getEventuallyWeightedResult(stringDoubleEntry.getValue(),
                                                           multipleModelMethod, weight);
            return new KiePMMLNameValue(stringDoubleEntry.getKey(), retrieved);
        }).collect(Collectors.toList());
    }

    /**
     * Returns a <code>KiePMMLValueWeight</code> if the given <code>MULTIPLE_MODEL_METHOD</code> expect it;
     * the original <b>rawObject</b>, otherwise
     *
     * @param rawObject
     * @param multipleModelMethod
     * @param weight
     * @return
     * @throws KiePMMLException
     */
    Object getEventuallyWeightedResult(Object rawObject, MULTIPLE_MODEL_METHOD multipleModelMethod,
                                       double weight) {
        switch (multipleModelMethod) {
            case MAJORITY_VOTE:
            case MODEL_CHAIN:
            case SELECT_ALL:
            case SELECT_FIRST:
                return rawObject;
            case MAX:
            case SUM:
            case MEDIAN:
            case AVERAGE:
            case WEIGHTED_SUM:
            case WEIGHTED_MEDIAN:
            case WEIGHTED_AVERAGE:
                if (!(rawObject instanceof Number)) {
                    throw new KiePMMLException("Expected a number, retrieved " + rawObject.getClass().getName());
                }
                return new KiePMMLValueWeight(((Number) rawObject).doubleValue(), weight);
            case WEIGHTED_MAJORITY_VOTE:
                throw new KiePMMLException(multipleModelMethod + " not implemented, yet");
            default:
                throw new KiePMMLException("Unrecognized MULTIPLE_MODEL_METHOD " + multipleModelMethod);
        }
    }

    void validate(final KiePMMLModel toValidate) {
        if (!(toValidate instanceof KiePMMLMiningModel)) {
            throw new KiePMMLModelException(String.format(EXPECTED_A_KIE_PMMLMINING_MODEL_RECEIVED,
                                                          toValidate.getClass().getName()));
        }
        validateMining((KiePMMLMiningModel) toValidate);
    }

    void validateMining(final KiePMMLMiningModel toValidate) {
        if (toValidate.getTargetField() == null || StringUtils.isEmpty(toValidate.getTargetField().trim())) {
            throw new KiePMMLInternalException(String.format(TARGET_FIELD_REQUIRED_RETRIEVED,
                                                             toValidate.getTargetField()));
        }
    }

    void populateInputDataWithSegmentResult(final PMML4Result pmml4Result,
                                            final PMMLRuntimeContext pmmlContext,
                                            final MULTIPLE_MODEL_METHOD multipleModelMethod,
                                            final KiePMMLSegment segment,
                                            final LinkedHashMap<String, KiePMMLNameValueProbabilityMapTuple> toPopulate) {
        pmml4Result.getResultVariables().forEach((s, o) -> pmmlContext.getRequestData().addRequestParam(s, o));

        PMML4ResultProbabilityMapTuple pmml4ResultTuple = new PMML4ResultProbabilityMapTuple(pmml4Result,
                                                                                             pmmlContext.getProbabilityMap());

        KiePMMLNameValue predictionValue = getKiePMMLNameValue(pmml4ResultTuple.pmml4Result,
                                                               multipleModelMethod,
                                                               segment.getWeight());
        List<KiePMMLNameValue> probabilityValues = getKiePMMLNameValues(pmml4ResultTuple.probabilityResultMap,
                                                                        multipleModelMethod,
                                                                        segment.getWeight());
        toPopulate.put(segment.getId(), new KiePMMLNameValueProbabilityMapTuple(predictionValue,
                                                                                probabilityValues));
        addStep(() -> getStep(segment, pmml4Result), pmmlContext);
    }

    /**
     * Send the given <code>PMMLStep</code>
     * to the <code>PMMLRuntimeContext</code>
     *
     * @param stepSupplier
     * @param pmmlContext
     */
    void addStep(final Supplier<PMMLStep> stepSupplier, final PMMLRuntimeContext pmmlContext) {
        stepExecuted(stepSupplier, pmmlContext);
    }

    /**
     * Return a <code>PMMLStep</code> out of the given <code>KiePMMLSegment</code> and
     * <code>PMML4Result</code>
     * @param segment
     * @param pmml4Result
     * @return
     */
    PMMLStep getStep(final KiePMMLSegment segment, final PMML4Result pmml4Result) {
        PMMLStep toReturn = new PMMLMiningModelStep();
        toReturn.addInfo("SEGMENT", segment.getName());
        toReturn.addInfo("MODEL", segment.getModel().getName());
        toReturn.addInfo("RESULT CODE", pmml4Result.getResultCode());
        if (ResultCode.OK.getName().equals(pmml4Result.getResultCode())) {
            toReturn.addInfo("RESULT", pmml4Result.getResultVariables().get(pmml4Result.getResultObjectName()));
        }
        return toReturn;
    }

    /**
     * Evaluate the whole <code>KiePMMLMiningModel</code>
     * Being it a <b>meta</b> model, it actually works as the top-level PMML model,
     * recursively and indirectly invoking model-specific evaluators (through <code>PMMLRuntime</code> container)
     * @param toEvaluate
     * @param pmmlContext
     * @return
     */
    private PMML4Result evaluateMiningModel(final KiePMMLMiningModel toEvaluate,
                                            final PMMLRuntimeContext pmmlContext) {
        final MULTIPLE_MODEL_METHOD multipleModelMethod = toEvaluate.getSegmentation().getMultipleModelMethod();
        final List<KiePMMLSegment> segments = toEvaluate.getSegmentation().getSegments();
        final LinkedHashMap<String, KiePMMLNameValueProbabilityMapTuple> inputData = new LinkedHashMap<>();
        for (KiePMMLSegment segment : segments) {
            Optional<PMML4Result> segmentResult = evaluateSegment(segment, pmmlContext);
            segmentResult.ifPresent(pmml4Result -> populateInputDataWithSegmentResult(pmml4Result,
                                                                                      pmmlContext,
                                                                                      multipleModelMethod,
                                                                                      segment,
                                                                                      inputData));
        }
        return getPMML4Result(toEvaluate, inputData, pmmlContext);
    }

    /**
     * Evaluate the model contained in the <code>KiePMMLSegment</code>, indirectly invoking
     * the model-specific runtime (through <code>PMMLRuntime</code> container)
     *
     * @param toEvaluate
     * @param pmmlContext
     * @return
     */
    private Optional<PMML4Result> evaluateSegment(final KiePMMLSegment toEvaluate,
                                                  final PMMLRuntimeContext pmmlContext) {
        logger.trace("evaluateSegment {}", toEvaluate.getId());
        final KiePMMLPredicate kiePMMLPredicate = toEvaluate.getKiePMMLPredicate();
        Optional<PMML4Result> toReturn = Optional.empty();
        Map<String, Object> values = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        if (kiePMMLPredicate.evaluate(values)) {
            toReturn = Optional.ofNullable(PMMLRuntimeHelper.evaluate(toEvaluate.getModel(), pmmlContext));
        }
        return toReturn;
    }

    static class PMML4ResultProbabilityMapTuple {

        private final PMML4Result pmml4Result;
        private final Map<String, Double> probabilityResultMap;

        public PMML4ResultProbabilityMapTuple(PMML4Result pmml4Result, Map<String, Double> probabilityResultMap) {
            this.pmml4Result = pmml4Result;
            this.probabilityResultMap = probabilityResultMap;
        }
    }

    static class KiePMMLNameValueProbabilityMapTuple {

        private final KiePMMLNameValue predictionValue;
        private final List<KiePMMLNameValue> probabilityValues;

        public KiePMMLNameValueProbabilityMapTuple(KiePMMLNameValue kiePMMLNameValue,
                                                   List<KiePMMLNameValue> probabilityValues) {
            this.predictionValue = kiePMMLNameValue;
            this.probabilityValues = probabilityValues;
        }
    }
}
