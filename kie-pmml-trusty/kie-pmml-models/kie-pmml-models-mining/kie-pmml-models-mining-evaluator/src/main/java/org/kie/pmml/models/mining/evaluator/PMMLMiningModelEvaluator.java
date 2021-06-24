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
package org.kie.pmml.models.mining.evaluator;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.util.StringUtils;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.model.tuples.KiePMMLValueWeight;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.enums.ResultCode.FAIL;
import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Mining</b>
 */
public class PMMLMiningModelEvaluator implements PMMLModelEvaluator<KiePMMLMiningModel> {

    private static final Logger logger = LoggerFactory.getLogger(PMMLMiningModelEvaluator.class.getName());
    private static final String EXPECTED_A_KIE_PMMLMINING_MODEL_RECEIVED = "Expected a KiePMMLMiningModel, received %s";
    private static final String TARGET_FIELD_REQUIRED_RETRIEVED = "TargetField required, retrieved %s";
    private static final Map<String, InternalKnowledgeBase> MAPPED_KIEBASES = new HashMap<>();

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.MINING_MODEL;
    }

    @Override
    public PMML4Result evaluate(final KieBase knowledgeBase,
                                final KiePMMLMiningModel model,
                                final PMMLContext pmmlContext) {
        validate(model);
        return evaluateMiningModel(model, pmmlContext, knowledgeBase);
    }

    PMML4Result getPMML4Result(final KiePMMLMiningModel toEvaluate,
                    final LinkedHashMap<String, KiePMMLNameValue> inputData) {
        final MULTIPLE_MODEL_METHOD multipleModelMethod = toEvaluate.getSegmentation().getMultipleModelMethod();
        Object prediction = null;
        ResultCode resultCode = OK;
        try {
            prediction = multipleModelMethod.apply(inputData);
        } catch (KieEnumException e) {
            logger.warn(e.getMessage());
            resultCode = FAIL;
        }
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(toEvaluate.getTargetField(), prediction);
        toReturn.setResultObjectName(toEvaluate.getTargetField());
        toReturn.setResultCode(resultCode.getName());
        return toReturn;
    }

    /**
     * Retrieve the <code>PMMLRuntime</code> to be used for the given <b>segment</b>
     * It creates new <code>InternalKnowledgeBase</code>s and store them in a <code>Map</code>,
     * to reuse them.
     * @param kModulePackageName
     * @param knowledgeBase
     * @param containerModelName
     * @return
     */
    PMMLRuntime getPMMLRuntime(final String kModulePackageName, final KieBase knowledgeBase,
                                       final String containerModelName) {
        final String key = containerModelName + "_" + kModulePackageName;
        InternalKnowledgeBase kieBase = MAPPED_KIEBASES.computeIfAbsent(key, s -> {
            final KiePackage kiePackage = knowledgeBase.getKiePackage(kModulePackageName);
            final List<KiePackage> packages = kiePackage != null ? Collections.singletonList(knowledgeBase.getKiePackage(kModulePackageName)) : Collections.emptyList();
            RuleBaseConfiguration conf = new RuleBaseConfiguration();
            conf.setClassLoader(((KnowledgeBaseImpl) knowledgeBase).getRootClassLoader());
            InternalKnowledgeBase toReturn = KnowledgeBaseFactory.newKnowledgeBase(kModulePackageName, conf);
            toReturn.addPackages(packages);
            return toReturn;
        });
        KieRuntimeFactory kieRuntimeFactory = KieRuntimeFactory.of(kieBase);
        return kieRuntimeFactory.get(PMMLRuntime.class);
    }

    /**
     * Returns a <code>KiePMMLNameValue</code> representation of the <code>PMML4Result</code>.
     * <b>It is based on the assumption there is only one result to be considered, defined as</b>
     * {@link PMML4Result#getResultObjectName() }
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
     * Returns a <code>KiePMMLValueWeight</code> if the given <code>MULTIPLE_MODEL_METHOD</code> expect it;
     * the original <b>rawObject</b>, otherwise
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
            case MODEL_CHAIN:
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

    /**
     * Evaluate the whole <code>KiePMMLMiningModel</code>
     * Being it a <b>meta</b> model, it actually works as the top-level PMML model,
     * recursively and indirectly invoking model-specific evaluators (through <code>PMMLRuntime</code> container)
     *
     * @param toEvaluate
     * @param pmmlContext
     * @param knowledgeBase
     * @return
     */
    private PMML4Result evaluateMiningModel(final KiePMMLMiningModel toEvaluate,
                                            final PMMLContext pmmlContext,
                                            final KieBase knowledgeBase) {
        final MULTIPLE_MODEL_METHOD multipleModelMethod = toEvaluate.getSegmentation().getMultipleModelMethod();
        final List<KiePMMLSegment> segments = toEvaluate.getSegmentation().getSegments();
        final LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        for (KiePMMLSegment segment : segments) {
            Optional<PMML4Result> segmentResult = evaluateSegment(segment, pmmlContext, knowledgeBase, toEvaluate.getName());
            segmentResult.ifPresent(pmml4Result -> {
                KiePMMLNameValue kiePMMLNameValue = getKiePMMLNameValue(pmml4Result, multipleModelMethod,
                                                                        segment.getWeight());
                inputData.put(segment.getId(), kiePMMLNameValue);
            });
        }
        return getPMML4Result(toEvaluate, inputData);
    }

    /**
     * Evaluate the model contained in the <code>KiePMMLSegment</code>, indirectly invoking
     * the model-specific evaluator (through <code>PMMLRuntime</code> container)
     *
     * @param toEvaluate
     * @param pmmlContext
     * @param knowledgeBase
     * @param containerModelName
     * @return
     */
    private Optional<PMML4Result> evaluateSegment(final KiePMMLSegment toEvaluate, final PMMLContext pmmlContext,
                                                  final KieBase knowledgeBase, final String containerModelName) {
        logger.trace("evaluateSegment {}", toEvaluate.getId());
        final KiePMMLPredicate kiePMMLPredicate = toEvaluate.getKiePMMLPredicate();
        Optional<PMML4Result> toReturn = Optional.empty();
        Map<String, Object> values = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        String modelName = toEvaluate.getModel().getName();
        if (kiePMMLPredicate.evaluate(values)) {
            final PMMLRuntime pmmlRuntime = getPMMLRuntime(toEvaluate.getModel().getKModulePackageName(),
                                                                   knowledgeBase, containerModelName);
            logger.trace("{}: matching predicate, evaluating... ", toEvaluate.getId());
            toReturn = Optional.of(pmmlRuntime.evaluate(modelName, pmmlContext));
        }
        return toReturn;
    }
}
